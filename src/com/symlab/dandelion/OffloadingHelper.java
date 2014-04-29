package com.symlab.dandelion;

import java.lang.reflect.Method;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class OffloadingHelper {

	private static final String TAG = "OffloadingHelper";
	
	private IOffloadingService mService;
	private Context	mContext;
	private Intent offloadingServiceIntent;
	
	private boolean serviceBound = false;
	
	private Handler handler = new Handler();
	private Runnable task = new Runnable() {

		@Override
		public void run() {
			//Log.d(TAG, "Updating status...");
			try {
				testStart();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//handler.postDelayed(this,10000); 
		}
		
	};
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "Service Binding...");
			mService = IOffloadingService.Stub.asInterface(service);
			//handler.postDelayed(task,1000); 
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			//handler.removeCallbacks(task);
			testStop();
			mService = null;
			Log.d(TAG, "Service Broken");
		}
		
	};
	
	public OffloadingHelper(Context context) {
		this.mContext 	= context;
		offloadingServiceIntent = new Intent(mContext, OffloadingService.class);
	}
	
	public void initializeOHelper() {
		Log.d(TAG, "Call initialize");
		//if (!OffloadingService.serviceStarted) {
			Log.d(TAG, "Start Service");
			mContext.startService(offloadingServiceIntent);
			OffloadingService.setServiceOn();
		//}
		if (!serviceBound) {
			Log.d(TAG, "Bind Service");
			mContext.bindService(offloadingServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
			serviceBound = true;
		}
	}
	
	public void tearDownOHelper() {
		if (serviceBound) {
			testStop();
			mContext.unbindService(serviceConnection);
			serviceBound = false;
			mService = null;
			Log.d(TAG, "Service Unbinding...");
		}
	}
	
	public void stopOffloadingService() {
		if (OffloadingService.serviceStarted) {
			mContext.startService(offloadingServiceIntent);
			OffloadingService.setServiceOff();
		}
	}
	
	public OfflaodableMethod postTask(Offloadable receiver, Method method, Object[] paraValue, Class<?> reutrnType) {
		OfflaodableMethod om = new OfflaodableMethod(mContext, receiver, method, paraValue, reutrnType);
		try {
			mService.addTaskToQueue(om);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return om;
	}
	
	public void testStart() {
		try {
			mService.registerNsd();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void testStop() {
		try {
			mService.unregisterNsd();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
