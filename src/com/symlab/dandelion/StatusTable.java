package com.symlab.dandelion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

import com.symlab.dandelion.network.ConnectedDeviceList;

public class StatusTable {
	private StatusTableData data;
	private Context mContext;
	//private ConnectedDeviceList deviceList;
	
	private static final String TAG = "StatusTable";
	
	public StatusTable(Context context) {
		data = new StatusTableData();
		mContext = context;
		//this.deviceList = deviceList;
	}
	
	public StatusTableData getStatusTableData() {
		return data;
	}

	public void updateStatus(ConnectedDeviceList deviceList) throws InterruptedException {
		//Log.d(TAG, "Start updating status...");
		//Long currId = ((OffloadingService) mContext).getDeviceId();
		//updateSelf(currId);
		Thread[] t = new Thread[deviceList.size()];
		//CountDownLatch latch = new CountDownLatch(t.length);
		data.clearData();
		for(int i = 0; i < deviceList.size(); i++) {
			String key = deviceList.getDeviceList().get(i);
			Log.d(TAG, "Updating " + key);
			t[i] = new UpdateTask(deviceList.getOutputStream(key), deviceList.getInputStream(key));
			t[i].start();
			//latch.countDown();
		}
		//latch.await(10L, TimeUnit.SECONDS);
	}
	
	public String displayTable() {
		String result = "Device ID\tNumOfCores\tCPU Idleness\tBattery\tMemoryAvil\n";
		for(int i = 0; i < data.statusCol.size(); i++) {
			Status s = data.statusCol.get(i);
			result += (data.deviceId.get(i)) + '\t' + s.numOfProcessors + '\t' + s.cpuIdleness + "%\t" + s.batteryPercentage + "%\t" + s.memoryFree + "MB\n";
		}
		return result;
	}

/*	
	private void updateSelf(Long id) {
		Status currStatus = ((OffloadingService) mContext).myStatus();
		if (!data.deviceId.contains(id)) {
			data.deviceId.add(id);
			data.statusCol.put(id, currStatus);
			data.addressCol.put(id, null);
		}
		else {
			//data.statusCol.remove(id);
			data.statusCol.replace(id, currStatus);
			//data.addressCol.remove(id);
			data.addressCol.replace(id, null);
		}
	}
*/	
	private class UpdateTask extends Thread{
		
		//private CountDownLatch mLatch;
		private ObjectInputStream objIn;
		private ObjectOutputStream objOut;
		
		public UpdateTask(ObjectOutputStream oos, ObjectInputStream ois) {
			//mLatch = latch;
			objIn = ois;
			objOut = oos;
		}
		
		@Override
		public void run() {
			//Log.e(TAG, "Prepare connection...");
			String id;
			Status status;
			
			try {
				//Log.d(TAG, "Connecting...");

				//Log.d(TAG, "Connected to other device...");
				objOut.write(Constants.NETWORK_REQUEST_STATUS);
				objOut.flush();
				
				id = (String) objIn.readObject();
				//Log.d(TAG, "Read id: " + Long.toHexString(id));
				status = (Status) objIn.readObject();
				Log.e(TAG, "Read status object...");
				
				Log.d(TAG, String.format("Device ID: %s", id));
				Log.d(TAG, String.format("CPU Idelness: %f%%\nBattery: %f%%\nMemory Free: %fMB", status.cpuIdleness, status.batteryPercentage, status.memoryFree));
 
				data.add(id, status);
				
				objOut.write(Constants.NETWORK_CLOSE);
				objOut.flush();
				
				objIn.close();
				objOut.close();

				//mLatch.countDown();
				Log.e(TAG, "Read status object finished.");

			} catch (IOException se){
				
			} catch (Exception e) {
				Log.d(TAG, e.toString());
				e.printStackTrace();
			}
		}
	}
}




