package com.symlab.dandelion;
import com.symlab.dandelion.Status;
import com.symlab.dandelion.StatusTableData;
import com.symlab.dandelion.OfflaodableMethod;

interface IOffloadingService {
	Status getMyStatus();
	long getMyDeviceId();
	void registerNsd();
	void unregisterNsd();
	void updateStatusTable();
	String displayStatusTable();
	void discoverNSD();
	void stopDiscoverNSD();
	StatusTableData getStatusTableData();
	void addTaskToQueue(in OfflaodableMethod om);
}