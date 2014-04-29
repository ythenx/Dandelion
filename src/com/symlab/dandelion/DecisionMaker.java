package com.symlab.dandelion;

import java.util.List;

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
