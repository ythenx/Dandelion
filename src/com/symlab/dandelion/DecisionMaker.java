package com.symlab.dandelion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.symlab.dandelion.network.ConnectedDeviceList;

import android.util.Log;

public class DecisionMaker {
	
	private static final String TAG = "DecisionMaker";
	
	private StatusTableData statustable;
	private Long selectedTargetId;

	public DecisionMaker(StatusTableData st) {
		statustable = st;
		selectedTargetId = 0L;
	}
	
	public Long getSelectedTarget() {
		return selectedTargetId;
	}
	
	public List<String> globalOptimalDecision(LinkedList<OfflaodableMethod> taskQueue){
		List <String> devices=new ArrayList<String>();
		return devices;
	}
	
	public List<String> greedyDecision(TaskQueue taskQueue,ConnectedDeviceList deviceList){
		LinkedList<OfflaodableMethod> bufferQueue = new LinkedList<OfflaodableMethod>();
		
		List <String> devices=new ArrayList<String>();
		
		statustable.sortDescStatus("cpuIdleness");
		int count=0;
		for(int i=0; i<deviceList.size(); i++){
			if(deviceList.getJobStatusList().get(i)==true){
				devices.add(deviceList.getDeviceList().get(i));
			}
		}
		
		return devices;
	}
	/*
	public void selectTarget() {
		List<Long> deviceList = statustable.deviceId;
		List<Status> deviceStatus = statustable.statusCol;
		Float value = 0f;
		Float temp = 0f;
		//Long d;
		Status s;
		for (int i = 0; i < deviceList.size(); i++) {
			s = deviceStatus.get(i);
			temp = s.numOfProcessors*s.batteryPercentage*s.cpuIdelness*s.memoryFree;
			Log.d(TAG, String.format("Device: %s, Value: %f", Long.toHexString(deviceList.get(i)), temp));
			if (value < temp) {
				value = temp;
				selectedTargetId = deviceList.get(i);
			}
		}
		Log.d(TAG,"Selected target " + Long.toHexString(selectedTargetId));
	}
	*/
}
