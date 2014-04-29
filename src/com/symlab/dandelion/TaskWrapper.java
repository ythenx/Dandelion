package com.symlab.dandelion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import android.content.Context;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.symlab.dandelion.network.ConnectedDeviceList;
import com.symlab.dandelion.profilers.DeviceProfiler;
import com.symlab.dandelion.profilers.NetworkProfiler;
import com.symlab.dandelion.profilers.Profiler;
import com.symlab.dandelion.profilers.ProgramProfiler;

public class TaskWrapper implements Callable<Object> {
	
	private static final String TAG = "TaskWrapper";
	
	private OfflaodableMethod toExecute;
	//private InetAddress targetAddress;
	
	private Context mContext;
	
	//private Socket mSocket;
	private ObjectOutputStream mObjOutStream;
	private ObjectInputStream mObjInStream;
	
	private Long mPureExecutionDuration;
	
	private boolean remotely;
	
	public TaskWrapper(Context context, OfflaodableMethod om, boolean isRemote, ObjectOutputStream oos, ObjectInputStream ois) {
		mContext = context;
		toExecute = om;
		mObjOutStream = oos;
		mObjInStream = ois;
		remotely = isRemote;
		//targetAddress = address;
	}

	//@SuppressWarnings("unchecked")
	@Override
	public Object call() throws Exception {
		Object result = null;
		try {
			result = execute(toExecute.method, toExecute.paraValue, toExecute.receiver);
			Log.d(TAG, "Current result: " + (Long)result);
		} catch (SecurityException e) {
			// Should never get here
			e.printStackTrace();
			throw e;
		} catch (NoSuchMethodException e) {
			// Should never get here
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private void establishConnection() {
		if (!remotely) return;
				try {
			//Long sTime = System.nanoTime();
					/*			mSocket = new Socket();
	
			try {
				mSocket.connect(new InetSocketAddress(targetAddress, Constants.SERVER_PORT), 2000);
			} catch (SocketTimeoutException ste) {
				Log.e(TAG, "Cannot connect to the selected node, executing locally...");
				remotely = false;
				return;
			}

			mObjOutStream	= new ObjectOutputStream(mSocket.getOutputStream());

			mObjInStream	= new ObjectInputStream(mSocket.getInputStream());
			
			Log.d(TAG, "Set up the io stream");

			if (mSocket != null) Log.d(TAG, "EC Connected to " + mSocket.getInetAddress().getHostAddress());
*/
			//String apkName = mPManager.getApplicationInfo(mAppName, 0).sourceDir;
			//Log.d(TAG, "Apk name - " + apkName);
			
			//Log.d(TAG, "Send first message...");
			
			mObjOutStream.writeInt(Constants.NETWORK_CONNECTING);
			mObjOutStream.flush();
			//Log.d(TAG, "Send second message...");
/*			mObjOutStream.write(Constants.NETWORK_APK_REGISTER);
			//Log.d(TAG, "Send app name...");
			mObjOutStream.writeObject(mAppName);
			//Log.d(TAG, "Waiting response...");
			int response = mObjInStream.read();
			//Log.d(TAG, "Response: " + response);
			if (response == Constants.NETWORK_APK_REQUEST) {
				// Send the APK file if needed
				sendApk(apkName, mObjOutStream);
			}
			
		*/	

		} catch (UnknownHostException e) {
			fallBackToLocalExecution("Connection setup to server failed: UnknownHost " + e.getMessage());
		} catch (IOException e) {
			fallBackToLocalExecution("Connection setup to server failed: socket is already bound " + e.getMessage());
		} catch (NetworkOnMainThreadException e) {
			fallBackToLocalExecution("Connection setup to server failed: NetworkOnMainThreadException " + e.getMessage());
		} catch (Exception e) {
			Log.d(TAG, "This Exception Again!!!" + e.toString());
			fallBackToLocalExecution("Could not connect: " + e.getMessage());
		}
	}
	
	private void fallBackToLocalExecution(String message) {
		Log.d(TAG, message);
		remotely = false;
	}
	
	private Object execute(Method m, Object o) throws Throwable {
		return execute(m, (Object[]) null, o);
	}
	
	private Object execute(Method m, Object[] pValues, Object o)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException {
		Object result;
		String classMethodName = o.getClass().toString() + "#" + m.getName();

		ProgramProfiler progProfiler = new ProgramProfiler(classMethodName);
		DeviceProfiler devProfiler = new DeviceProfiler(mContext);
		
		try {
			establishConnection();
			if (remotely) {

				//NetworkProfiler netProfiler = new NetworkProfiler(mContext);
				Log.d(TAG, "Executing Remotely...");
				//Profiler profiler = new Profiler(mContext, progProfiler, netProfiler, devProfiler);
				//establishConnection();
				// Start tracking execution statistics for the method
				//profiler.startExecutionInfoTracking();
				result = executeRemotely(m, pValues, o);
				// Collect execution statistics
				//profiler.stopAndLogExecutionInfoTracking(mPureExecutionDuration);
				//lastLogRecord = profiler.lastLogRecord;
				return result;
			} else { // Execute locally
				//NetworkProfiler netProfiler = null;
				Log.d(TAG, "Executing Locally...");
				Profiler profiler = new Profiler(mContext, progProfiler, null, devProfiler);
				
				// Start tracking execution statistics for the method
				profiler.startExecutionInfoTracking();
				result = executeLocally(m, pValues, o);
				// Collect execution statistics
				profiler.stopAndLogExecutionInfoTracking(mPureExecutionDuration);
				//lastLogRecord = profiler1.lastLogRecord;
				remotely = true;
				return result;
			}
			
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			Log.d(TAG, "InvocationTargetException " + e);
			return e;
			// throw e.getTargetException();
		}
	}
	
	private Object executeLocally(Method m, Object[] pValues, Object o)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		// Make sure that the method is accessible
		Object result = null;
		Long startTime = System.nanoTime();
		m.setAccessible(true);
		result = m.invoke(o, pValues); // Access it
		mPureExecutionDuration = System.nanoTime() - startTime;
		Log.d("PowerDroid-Profiler", "LOCAL " + m.getName()+ ": Actual Invocation duration - " + mPureExecutionDuration/ 1000 + "us");
		return result;
	}
	
	private Object executeRemotely(Method m, Object[] pValues, Object o)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SecurityException,
			ClassNotFoundException, NoSuchMethodException {
		Object result = null;
		Log.d(TAG, "Reach here!");
		try {
			Long startTime = System.nanoTime();
			mObjOutStream.write(Constants.NETWORK_RUN_TASK);
			result = sendAndExecute(m, pValues, o, mObjInStream, mObjOutStream);

			Long duration = System.nanoTime() - startTime;
			Log.d("PowerDroid-Profiler", "REMOTE " + m.getName() + ": Actual Send-Receive duration - " + duration / 1000 + "us");
		} catch (UnknownHostException e) {
			// No such host exists, execute locally
			Log.e(TAG, "ERROR " + m.getName() + ": " + e);
			result = executeLocally(m, pValues, o);
			//ConnectionRepair repair = new ConnectionRepair();
			//repair.start();
		} catch (IOException e) {
			// Connection broken, execute locally
			Log.e(TAG, "ERROR " + m.getName() + ": " + e);
			result = executeLocally(m, pValues, o);
			//ConnectionRepair repair = new ConnectionRepair();
			//repair.start();
		} catch (NullPointerException e) {
			Log.e(TAG, "ERROR Null pointer: " + e);
		}

		return result;
	}
	
	private void sendObject(Object o, Method m, Object[] pValues,
			ObjectOutputStream objOut) throws IOException {
		objOut.reset();
		Log.d(TAG, "Write Object and data");
		//Long startSend = System.nanoTime();
		//Long startRx = NetworkProfiler.getProcessRxBytes();
		//Long startTx = NetworkProfiler.getProcessTxBytes();
		
		// Send the number of clones needed to execute the method
		//objOut.writeInt(nrClones);
		
		// Send object for execution
		objOut.writeObject(o);

		// Send the method to be executed
		Log.d(TAG, "Write Method - " + m.getName());
		objOut.writeObject(m.getName());

		Log.d(TAG, "Write method parameter types");
		objOut.writeObject(m.getParameterTypes());

		Log.d(TAG, "Write method parameter values");
		objOut.writeObject(pValues);
		objOut.flush();

		// Estimate the perceived bandwidth
		//NetworkProfiler.addNewBandwidthEstimate(NetworkProfiler.getProcessRxBytes()- startRx + NetworkProfiler.getProcessTxBytes() - startTx,System.nanoTime() - startSend);
	}
	
	private Object sendAndExecute(Method m, Object[] pValues, Object o,
			ObjectInputStream objIn, ObjectOutputStream objOut)
					throws IOException, ClassNotFoundException,
					IllegalArgumentException, SecurityException,
					IllegalAccessException, InvocationTargetException,
					NoSuchMethodException {

		// Send the object itself
		sendObject(o, m, pValues, objOut);

		// Read the results from the server
		Log.d(TAG, "Read Result");
		//Long startSend = System.nanoTime();
		//Long startRx = NetworkProfiler.getProcessRxBytes();
		//Long startTx = NetworkProfiler.getProcessTxBytes();

		//Log.d(TAG, "Read Result 1");
		
		Object response = objIn.readObject();

		Log.d(TAG, "Read Result: " + response.toString());
		
		// Estimate the perceived bandwidth
		//NetworkProfiler.addNewBandwidthEstimate(NetworkProfiler.getProcessRxBytes() - startRx + NetworkProfiler.getProcessTxBytes() - startTx, System.nanoTime() - startSend);

		ResultContainer container = (ResultContainer) response;
		Object result;

		Class<?>[] pTypes = { Offloadable.class };
		try {
			// Use the copyState method that must be defined for all Remoteable
			// classes to copy the state of relevant fields to the local object
			o.getClass().getMethod("copyState", pTypes).invoke(o,
					container.caller);
		} catch (NullPointerException e) {
			// Do nothing - exception happened remotely and hence there is
			// no objet state returned.
			// The exception will be returned in the function result anyway.
			Log.d(TAG,
					"Exception received from remote server - "
							+ container.result);
		}

		result = container.result;
		mPureExecutionDuration = container.pureExecutionDuration;

		Log.d(TAG, "Finished execution");
		Log.d(TAG, "Result: " + result.toString());
		return result;
	}

}
