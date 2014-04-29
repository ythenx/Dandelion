package com.symlab.dandelion;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.symlab.dandelion.db.DatabaseQuery;

public class OfflaodableMethod implements Parcelable{

	Offloadable receiver;
	Method method;
	Object[] paraValue;
	Class<?> reutrnType;
	Future<?> resultHolder = null;
	boolean resultReady = false;
	private Object lock;
	//ResultListerner resultListener;
	
	public long execDuration;
	public long energyConsumption;
	public long recordQuantity;
	
	public OfflaodableMethod(Context context, Offloadable receiver, Method method, Object[] paraValue, Class<?> reutrnType) {
		super();
		this.receiver = receiver;
		this.method = method;
		this.paraValue = paraValue;
		this.reutrnType = reutrnType;
		lock = new Object();
		//this.resultListener = resultListener;
		DatabaseQuery query = new DatabaseQuery(context);
		String classMethodName = receiver.getClass().toString() + "#" + method.getName();
		ArrayList<String> queryString = query.getData(new String[] {"execDuration", "energyConsumption", "recordQuantity"}, "methodName = ?", 
				new String[] {classMethodName} , null, null, "execDuration", " ASC");
		boolean noResult = (queryString.size() == 0);
		execDuration = noResult ? 0 : Long.parseLong(queryString.get(0));
		energyConsumption = noResult ? 0 : Long.parseLong(queryString.get(1));
		recordQuantity = noResult ? 0 : Long.parseLong(queryString.get(2));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeSerializable(receiver);
		out.writeValue(method);
		out.writeArray(paraValue);
		out.writeValue(reutrnType);
		out.writeValue(resultHolder);
		out.writeByte((byte) (resultReady ? 1 : 0));
		out.writeValue(lock);
		//out.writeValue(resultListener);
		out.writeLong(execDuration);
		out.writeLong(energyConsumption);
		out.writeLong(recordQuantity);
	}
	
	public static final Parcelable.Creator<OfflaodableMethod> CREATOR = new Parcelable.Creator<OfflaodableMethod>() {

		@Override
		public OfflaodableMethod createFromParcel(Parcel in) {
			return new OfflaodableMethod(in);
		}

		@Override
		public OfflaodableMethod[] newArray(int size) {
			return new OfflaodableMethod[size];
		}
		
	};
	
	private OfflaodableMethod(Parcel in) {
		receiver = (Offloadable) in.readSerializable();
		method = (Method) in.readValue(null);
		paraValue = in.readArray(null);
		reutrnType = (Class<?>) in.readValue(null);
		resultHolder = (Future<?>) in.readValue(null);
		resultReady = in.readByte() != 0;
		lock = in.readValue(null);
		//resultListener = (ResultListerner) in.readValue(null);
		execDuration = in.readLong();
		energyConsumption = in.readLong();
		recordQuantity = in.readLong();
    }
	
	public void setHolder(Future future) {
		resultHolder = future;
	}
	
	public void setResultReady() {
		synchronized(lock) {
			resultReady = true;
			lock.notifyAll();
		}
	}

	public Object getResult() {
		Object result = null;
		//Log.d("Future","Waiting resultHolder");
		synchronized(lock) {
            while (!resultReady) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					break;
				}
            }
        }
		//Log.d("Future","Can get result");
		try {
			result = resultHolder.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return result;
	}
}
