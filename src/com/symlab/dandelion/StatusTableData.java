package com.symlab.dandelion;

import java.net.InetAddress;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class StatusTableData implements Parcelable {
	
	public CopyOnWriteArrayList<String> deviceId;
	public CopyOnWriteArrayList<Status> statusCol;
	//public CopyOnWriteArrayList<InetAddress> addressCol;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeList(deviceId);
		out.writeList(statusCol);
		//out.writeList(addressCol);
	}
	
	public void sortDescStatus(String index){
		float[] array = new float[statusCol.size()];
		if(index.compareTo("cpuIdleness")==0){
			for(int i=0; i<statusCol.size(); i++){
				array[i] = statusCol.get(i).cpuIdleness;
			}
		}else if(index.compareTo("batteryPercentage")==0){
			for(int i=0; i<statusCol.size(); i++){
				array[i] = statusCol.get(i).batteryPercentage;
			}
		}else if(index.compareTo("memoryFree")==0){
			for(int i=0; i<statusCol.size(); i++){
				array[i] = statusCol.get(i).memoryFree;
			}
		}
		
		for(int j=1; j< array.length; j++){
			float key =  array[j];
			Status keyS = new Status(statusCol.get(j));
			String keyD = deviceId.get(j);
			int i=j-1;
			while(i>=0 && array[i]<key){
				array[i+1]=array[i];
				deviceId.set(i+1, deviceId.get(i));
				statusCol.set(i+1, statusCol.get(i));
				i--;
			}
			array[i+1]=key;
			deviceId.set(i+1, keyD);
			statusCol.set(i+1, keyS);
		}
	}
	
	public static final Parcelable.Creator<StatusTableData> CREATOR = new Parcelable.Creator<StatusTableData>() {
		public StatusTableData createFromParcel(Parcel in) {
			return new StatusTableData(in);
		}

		public StatusTableData[] newArray(int size) {
			return new StatusTableData[size];
		}
	};
	
	public StatusTableData() {
		deviceId = new CopyOnWriteArrayList<String>();
		statusCol = new CopyOnWriteArrayList<Status>();
		//addressCol = new CopyOnWriteArrayList<InetAddress>();
	}
	
	private StatusTableData(Parcel in) {
		deviceId = new CopyOnWriteArrayList<String>();
		statusCol = new CopyOnWriteArrayList<Status>();
		//addressCol = new CopyOnWriteArrayList<InetAddress>();
		in.readList(deviceId, null);
		in.readList(statusCol, null);
		//in.readList(addressCol, null);
	}
	
	public void add(String id, Status s) {
		deviceId.addIfAbsent(id);
		statusCol.addIfAbsent(s);
		//addressCol.addIfAbsent(ip);
	}
	
	public Status getStatusById(String id) {
		int i = deviceId.indexOf(id);
		if (i != -1)
			return statusCol.get(i);
		else
			return null;
	}
	/*
	public InetAddress getIpById(Long id) {
		int i = deviceId.indexOf(id);
		if (i != -1)
			return addressCol.get(i);
		else
			return null;
	}
	*/
	public void removeById(String id) {
		int tmp = deviceId.indexOf(id);
		if (tmp == -1) return;
		deviceId.remove(tmp);
		statusCol.remove(tmp);
		//addressCol.remove(tmp);
	}
	
	public void clearData(){
		deviceId.clear();
		statusCol.clear();
	}
	/*
	public void removeByIp(InetAddress ip) {
		int tmp = addressCol.indexOf(ip);
		if (tmp == -1) return;
		deviceId.remove(tmp);
		statusCol.remove(tmp);
		addressCol.remove(tmp);
	}
	*/
}
