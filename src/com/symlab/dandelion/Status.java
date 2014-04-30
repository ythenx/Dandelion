package com.symlab.dandelion;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Status implements Parcelable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6492377379482256344L;
	public int numOfProcessors;
	public float cpuIdleness;
	public float batteryPercentage;
	public float memoryFree;
	
	//private ClassLoader loader;
	
	public Status() {
		//this.loader = loader;
	}
	
	public Status(Status s){
		this.numOfProcessors = s.numOfProcessors;
		this.cpuIdleness = s.cpuIdleness;
		this.batteryPercentage = s.batteryPercentage;
		this.memoryFree = s.memoryFree;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(numOfProcessors);
		out.writeFloat(cpuIdleness);
		out.writeFloat(batteryPercentage);
		out.writeFloat(memoryFree);
	}
	
	public static final Parcelable.Creator<Status> CREATOR = new Parcelable.Creator<Status>() {

		@Override
		public Status createFromParcel(Parcel in) {
			return new Status(in);
		}

		@Override
		public Status[] newArray(int size) {
			return new Status[size];
		}
		
	};
	
	private Status(Parcel in) {
		numOfProcessors = in.readInt();
		cpuIdleness = in.readFloat();
		batteryPercentage = in.readFloat();
		memoryFree = in.readFloat();
    }

}
