package com.symlab.dandelion;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;

import com.symlab.dandelion.network.DataPackage;

public class Master extends Thread {
	
	private static final String TAG1 = "Master";
	private static final String TAG2 = "Worker";

	private DynamicObjectInputStream objIn = null;
	private ObjectOutputStream objOut = null;
	private final Context mContext;
	private final int BUFFER = 8192;
	
	private String appName;						// the app name sent by the phone
	private Object objToExecute = new Object();	// the object to be executed sent by the phone
	private String methodName;						// the method to be executed
	private Class<?>[] pTypes;							// the types of the parameters passed to the method
	private Object[] pValues;						// the values of the parameteres to be passed to the method
	//private Class<?> returnType;						// the return type of the method
	private String apkFilePath;					// the path where the apk is installed


	
	public Master(DynamicObjectInputStream ois, ObjectOutputStream oos, final Context cW) {
		Log.d(TAG1, "New Client connected");
		objIn = ois;
		objOut = oos;
		this.mContext = cW;
		
		
	}
	
	@Override
	public void run() {

		//Log.d(TAG, "Client" + mSocket.getPort() + "Connected.");
		//DataPackage receivedData = null;
		int request = 0;
		//boolean repeating = true;
		try {
			//is.skip(is.available() - 1);
			while(true) {
				request = objIn.readInt();
				//receivedData = (DataPackage) objIn.readObject();
			if (request != -1) Log.d(TAG1, "Read request: " + request);
			//repeating = false;
			switch(request) {
			case -1:
				Log.d(TAG1, "Bad Request...");
				break;
			case Constants.NETWORK_CLOSE:
				Log.d(TAG1, "Close Connection...");
				break;
			case Constants.NETWORK_PING:
				//Log.d(TAG, "Reply to PING");
				objOut.write(Constants.NETWORK_PONG);
				objOut.flush();
				break;
			case Constants.NETWORK_REQUEST_STATUS:
				//Log.d(TAG, "Someone is requiring the status...");
				Long currId = ((OffloadingService) mContext).getDeviceId();
				Status currStatus = ((OffloadingService) mContext).myStatus();
				try {
					objOut.writeLong(currId);
					objOut.writeObject(currStatus);
					objOut.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case Constants.NETWORK_CONNECTING: 
				//Log.d(TAG, "Someone is offloading...");
				Worker workerThread = new Worker();
				workerThread.start();
				try {
					workerThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			default:
				if (request != -1) Log.d(TAG1, "Cannot handle this request: " + request);
				//repeating = true;
			}
				
			}
			
			//Log.d(TAG, "Connection closed.");
		} catch (Exception e) {
			Log.d(TAG1, e.toString());
			e.printStackTrace();
		} finally {
			try {
				objIn.close();
				objOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		Log.d(TAG1, "Master Thread Finished!");
	}
	
	/**
	 * Method to retrieve an apk of an application that needs to be executed
	 * 
	 * @param objIn
	 *            Object input stream to simplify retrieval of data
	 * @return the file where the apk package is stored
	 * @throws IOException
	 *             throw up an exception thrown if socket fails
	 */
	private File receiveApk(DynamicObjectInputStream objIn, String apkFilePath) throws IOException {
		// Receiving the apk file
		// Get the length of the file receiving
		int apkLen = objIn.readInt();
		Log.d(TAG1, "Read apk len - " + apkLen);

		// Get the apk file
		byte[] tempArray = new byte[apkLen];
		Log.d(TAG1, "Read apk");
		objIn.readFully(tempArray);

		// Write it to the filesystem
		File dexFile = new File(apkFilePath);
		FileOutputStream fout = new FileOutputStream(dexFile);

		BufferedOutputStream bout = new BufferedOutputStream(fout, BUFFER);
		bout.write(tempArray);
		bout.close();

		return dexFile;
	}
	
	/**
	 * Reads in the object to execute an operation on, name of the method to be
	 * executed and executes it
	 * 
	 * @param objIn
	 *            Dynamic object input stream for reading an arbitrary object
	 *            (class loaded from a previously obtained dex file inside an
	 *            apk)
	 * @return result of executing the required method
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws OptionalDataException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 */
	private Object retrieveAndExecute(DynamicObjectInputStream objIn) {
		Long startTime = System.nanoTime();
		// Read the object in for execution
		Log.d(TAG1, "Read Object");
		try {
			// Get the object
			objToExecute = objIn.readObject();
			Log.d(TAG1,"Read Object." + objToExecute.toString());
			// Get the class of the object, dynamically
			Class<?> objClass = objToExecute.getClass();

			
			Log.d(TAG1, "Read Method");
			// Read the name of the method to be executed
			methodName = (String) objIn.readObject();
			Log.d(TAG1,"Read Method: " + methodName);
			Object tempTypes = objIn.readObject();
			pTypes = (Class[]) tempTypes;
			Log.d(TAG1,"Read pTypes: " + pTypes.toString());
			Object tempValues = objIn.readObject();
			pValues = (Object[]) tempValues;
			Log.d(TAG1,"Read pValues: " + pValues.toString());
			Log.d(TAG1, "Run Method " + methodName);
			// Get the method to be run by reflection
			Method runMethod = objClass.getDeclaredMethod(methodName, pTypes);
			// And force it to be accessible (quite often would be declared
			// private originally)
			runMethod.setAccessible(true); // Set the method to be accessible

			// Run the method and retrieve the result
			Object result = null;
			Long execDuration = null;
			try {
				Long startExecTime = System.nanoTime();
				result = runMethod.invoke(objToExecute, pValues);
				execDuration = System.nanoTime() - startExecTime;
				Log.d(TAG1, runMethod.getName() + ": pure execution time - " + (execDuration / 1000) + "us");
			} catch (InvocationTargetException e) {
				// The method might have failed if the required shared library
				// had
				// not been loaded before, try loading the apk's libraries and
				// restarting the method
				if (e.getTargetException() instanceof UnsatisfiedLinkError) {
					Log.d(TAG1, "UnsatisfiedLinkError thrown");
/*					Method libLoader = objClass.getMethod("loadLibraries",
							LinkedList.class);
					try {
						libLoader.invoke(objToExecute, libraries);
						Long startExecTime = System.nanoTime();
						result = runMethod.invoke(objToExecute, pValues);
						execDuration = System.nanoTime() - startExecTime;
						Log.d(TAG, runMethod.getName()
								+ ": pure execution time - "
								+ (execDuration / 1000000) + "ms");
					} catch (InvocationTargetException e1) {
						result = e1;
					}*/
				} else {
					result = e;
				}
			}

			Log.d(TAG1, runMethod.getName() + ": retrieveAndExecute time - "
					+ ((System.nanoTime() - startTime) / 1000) + "us");

			return new ResultContainer(false, objToExecute, result, execDuration, 0f);

		} catch (Exception e) {
			// catch and return any exception since we do not know how to handle
			// them on the server side
			return new ResultContainer(true, null, e, null, 0f);
		}

	}
	
	private class Worker extends Thread {
		private Object result;
		
		@Override
		public void run() {
			try {

				int request = 0;
				while (request != -1) {

					request = objIn.read();
					Log.d(TAG2, "Request - " + request);

					switch(request) {
					case Constants.NETWORK_RUN_TASK:					
						Log.d(TAG2, "Execute request - " + request);
						result = retrieveAndExecute(objIn);

						try {
							// Send back over the socket connection
							Log.d(TAG2, "Send result back");
							objOut.writeObject(result);
							// Clear ObjectOutputCache - Java caching unsuitable
							// in this case
							objOut.flush();
							objOut.reset();

							Log.d(TAG2, "Result successfully sent");
						} catch (IOException e) {
							Log.d(TAG2, "Connection failed");
							e.printStackTrace();
							return;
						}
						Log.d(TAG2, "Result sent!");
						break;

					case Constants.NETWORK_APK_REGISTER:
						appName = (String) objIn.readObject();
						apkFilePath = mContext.getFilesDir().getAbsolutePath() + "/" + appName + ".apk";
						Log.d(TAG2, "APK Path: " + apkFilePath);
						if (apkPresent(apkFilePath)) {
							Log.d(TAG2, "APK present");
							objOut.write(Constants.NETWORK_APK_PRESENT);
							objOut.flush();
						} else {
							Log.d(TAG2, "request APK");
							objOut.write(Constants.NETWORK_APK_REQUEST);
							objOut.flush();
							// Receive the apk file from the client
							receiveApk(objIn, apkFilePath);
						}
						File dexFile = new File(apkFilePath);
						//libraries = addLibraries(dexFile);
						objIn.addDex(dexFile);

						break;

					default:
						Log.d(TAG2, "No response to this request: " + request);	
					}
				}
		
			}catch (Exception e) {
				// We don't want any exceptions to escape from here,
				// hide everything silently if we didn't foresee them cropping
				// up... Since we don't want the server to die because
				// somebody's program is misbehaving
				Log.e(TAG2, "Exception not caught properly - " + e);
			} catch (Error e) {
				// We don't want any exceptions to escape from here,
				// hide everything silently if we didn't foresee them cropping
				// up... Since we don't want the server to die because
				// somebody's program is misbehaving
				Log.e(TAG2, "Error not caught properly - " + e);
			}
		}
	}
	
	/**
	 * Check if the application is already present on the machine
	 * 
	 * @param filename
	 *            filename of the apk file (used for identification)
	 * @return true if the apk is present, false otherwise
	 */
	private boolean apkPresent(String filename) {
		// return false;
		// TODO: more sophisticated checking for existence
		File apkFile = new File(filename);
		return apkFile.exists();
	}

}
