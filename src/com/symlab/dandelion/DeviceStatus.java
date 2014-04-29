package com.symlab.dandelion;

import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class DeviceStatus {
	
	//private Context context;
	private Status status = new Status();;
	private float battery;
	
	private final IntentFilter intentFilter = new IntentFilter();
	private BroadcastReceiver receiver = null;
	private ActivityManager activityManager;
	private MemoryInfo mi;
	private Runtime runtime;
		
	public DeviceStatus(Context context) {
		//this.context = context;
		runtime = Runtime.getRuntime();
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		//intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
		//intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
		receiver = new BatteryBroadcastReceiver();
		context.registerReceiver(receiver, intentFilter);
		activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		mi = new MemoryInfo();
		
	}
	
	public Status readStatus() {
		//Status status = new Status();
		status.numOfProcessors = runtime.availableProcessors();
		status.cpuIdelness = 100f - readCpuUsage()*100f;
//		try {
			activityManager.getMemoryInfo(mi);
			status.memoryFree = mi.availMem/1048576f;
/*		} catch (Exception ex) {
			if (activityManager == null) Log.d("DeviceStatus", "activityManager is null");
			ex.printStackTrace();
		}
*/
		
		status.batteryPercentage = battery;
		//Log.d("DeviceStatus", String.format("Show Battery = %f", status.batteryPercentage));
		return status;
	}

	private float readCpuUsage() {
	    try {
	        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
	        String load = reader.readLine();

	        String[] toks = load.split(" ");

	        long idle1 = Long.parseLong(toks[5]);
	        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        try {
	            Thread.sleep(360);
	        } catch (Exception e) {}

	        reader.seek(0);
	        load = reader.readLine();
	        reader.close();

	        toks = load.split(" ");

	        long idle2 = Long.parseLong(toks[5]);
	        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return 0;
	} 
	
	class BatteryBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				//Log.d("DeviceStatus", "receive ACTION_BATTERY_CHANGED");
				//int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				//int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				//int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				//int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
				//int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
				//Log.d("DeviceStatus", String.format("voltage = %d", voltage));
				battery = 100 * level/(float)scale;
				//readStatus();
				//Log.d("DeviceStatus", String.format("Battery = %f", batteryPercentage));
				
				
			}
			
		}
		
	}
	
}
