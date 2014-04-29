package com.symlab.dandelion.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectedDeviceList {
	
	private CopyOnWriteArrayList<String> list;
	private HashMap<String, ObjectInputStream> inputstreams;
	private HashMap<String, ObjectOutputStream> outputstreams;
	
	public ConnectedDeviceList() {
		list = new CopyOnWriteArrayList<String>();
		inputstreams = new HashMap<String, ObjectInputStream>();
		outputstreams = new HashMap<String, ObjectOutputStream>();
	}
	
	public ObjectInputStream getInputStream(String key) {
		if (!list.contains(key)) return null;
		else return inputstreams.get(key);
	}

	public ObjectOutputStream getOutputStream(String key) {
		if (!list.contains(key)) return null;
		else return outputstreams.get(key);
	}
	
	public synchronized void addIfAbsent(String key, ObjectInputStream is, ObjectOutputStream os) {
		if (!list.contains(key)) {
			list.add(key);
			inputstreams.put(key, is);
			outputstreams.put(key, os);
		} else {
			inputstreams.remove(key);
			inputstreams.put(key, is);
			outputstreams.remove(key);
			outputstreams.put(key, os);
		}
	}
	
	public synchronized void removeNode(String key) {
		if (!list.contains(key)) return;
		list.remove(key);
		inputstreams.remove(key);
		outputstreams.remove(key);
	}
	
	public int size() {
		return list.size();
	}
	
	public List<String> getDeviceList() {
		return list;
	}
}
