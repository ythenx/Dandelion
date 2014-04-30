package com.symlab.dandelion;

import android.os.Handler;

import com.symlab.dandelion.network.ConnectedDeviceList;
import com.symlab.dandelion.network.NetworkInterface;

public class StatusUpdater {

	private static final String TAG = "StatusUpdater";
	
	private NetworkInterface netInt;
	private ConnectedDeviceList deviceList;
	private StatusTable status;
	
	private Handler handler;
	
	private int timeInterval;
	
	private Runnable task = new Runnable() {

		@Override
		public void run() {
			netInt.discoverService();
			//Log.d(TAG, "Updating status...");
			try {
				status.updateStatus(deviceList);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handler.postDelayed(this,timeInterval); 
		}
		
	};
	
	public StatusUpdater(NetworkInterface ni, ConnectedDeviceList deviceList, StatusTable st, int ti) {
		netInt = ni;
		this.deviceList = deviceList;
		status = st;
		timeInterval = ti;
		handler = new Handler( );
	}
	
	public void startUpdate() {
		//netInt.discoverService();
		handler.post(task); 
	}
	
	public void stopUpdate() {
		handler.removeCallbacks(task);
		netInt.stopDiscovery();
	}
}
