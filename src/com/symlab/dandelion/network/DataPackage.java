package com.symlab.dandelion.network;

import java.io.Serializable;

public class DataPackage implements Serializable {
	
	private static final long serialVersionUID = -3825185830334989901L;
	
	public int what;
	
	public Object data;
	
	private DataPackage(int what, Object data) {
		this.what = what;
		this.data = data;
	}
	
	public static DataPackage obtain(int what) {
		return new DataPackage(what, null);
	}
	
	public static DataPackage obtain(int what, Object data) {
		return new DataPackage(what, data);
	}
}
