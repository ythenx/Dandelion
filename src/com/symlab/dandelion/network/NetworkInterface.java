package com.symlab.dandelion.network;

public interface NetworkInterface {
	
	void send(String target, DataPackage data);
	
	DataPackage receive(String target);
	
	void discoverService();
	
	void stopDiscovery();
	
}
