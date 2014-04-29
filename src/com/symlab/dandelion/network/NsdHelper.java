package com.symlab.dandelion.network;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.symlab.dandelion.Constants;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class NsdHelper {
	//private Context context;
	private NsdManager nsd;
	//private ConcurrentHashMap<String, InetAddress> serviceMap;
	private CopyOnWriteArrayList<String> name;
	private CopyOnWriteArrayList<InetAddress> address;
	
	
	private NsdManager.ResolveListener resolveListener;
	private NsdManager.DiscoveryListener discoveryListener;
	private NsdManager.RegistrationListener registrationListener;
	
	private boolean nsdRegistered = false;
	private boolean nsdDiscovering = false;

    public static final String SERVICE_TYPE = "_offloading._tcp.";

    public static final String TAG = "NsdHelper";
    public String serviceName = "OffloadHelper";
    public String myServiceName = "";

    public NsdHelper(Context context) {
        //this.context = context;
        //serviceMap = new ConcurrentHashMap<String, InetAddress>();
        name = new CopyOnWriteArrayList<String>();
        address = new CopyOnWriteArrayList<InetAddress>();
        nsd = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }
    
    public void initializeNsd() {
        initializeResolveListener();
        initializeDiscoveryListener();
        initializeRegistrationListener();
        Log.d(TAG, "NSD Initialized!");
        //mNsdManager.init(mContext.getMainLooper(), this);

    }
    
    public void initializeDiscoveryListener() {
    	discoveryListener = new NsdManager.DiscoveryListener() {

			@Override
			public void onDiscoveryStarted(String serviceType) {
				Log.d(TAG, "Service discovery started");
				
			}

			@Override
			public void onDiscoveryStopped(String serviceType) {
				Log.i(TAG, "Discovery stopped: " + serviceType);
				
			}

			@Override
			public void onServiceFound(NsdServiceInfo serviceInfo) {
				
				if (!serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
	                // Service type is the string containing the protocol and
	                // transport layer for this service.
	                Log.d(TAG, "Unknown Service Type: " + serviceInfo.getServiceType());
	            } else if (serviceInfo.getServiceName().equals(myServiceName)) {
	                // The name of the service tells the user what they'd be connecting to.
	                Log.d(TAG, "Same machine: " + myServiceName);
	            } else if (serviceInfo.getServiceName().contains(serviceName)){
	            	Log.d(TAG, "Service discovery success \n" + serviceInfo);
	            	nsd.resolveService(serviceInfo, resolveListener);
	            }
				nsd.resolveService(serviceInfo, resolveListener);
				
				
			}

			@Override
			public void onServiceLost(NsdServiceInfo serviceInfo) {
				Log.e(TAG, "service lost" + serviceInfo);
				final String serviceName = serviceInfo.getServiceName();
				/*if (serviceMap.containsKey(serviceName)) {
					serviceMap.remove(serviceName);
				}*/
				int tmp = name.indexOf(serviceName);
				if (tmp != -1) {
					name.remove(tmp);
					address.remove(tmp);
				}
				
			}

			@Override
			public void onStartDiscoveryFailed(String serviceType, int errorCode) {
				Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsd.stopServiceDiscovery(this);
				
			}

			@Override
			public void onStopDiscoveryFailed(String serviceType, int errorCode) {
				Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsd.stopServiceDiscovery(this);
				
			}
    		
    	};
    }
    
    public void initializeResolveListener() {
    	resolveListener = new NsdManager.ResolveListener() {

			@Override
			public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
				Log.e(TAG, "Resolve failed: " + errorCode);
				
			}

			@Override
			public void onServiceResolved(NsdServiceInfo serviceInfo) {
				Log.d(TAG, "Resolve Succeeded. " + serviceInfo.getServiceName());
				Log.d(TAG, "" + serviceInfo.getHost());
	/*			if (serviceInfo.getServiceName().equals(serviceName)) {
	                Log.d(TAG, "Same IP.");
	                return;
	            }
	*/			
				final String serviceName = serviceInfo.getServiceName();
				//serviceMap.putIfAbsent(serviceName, serviceInfo.getHost());
				int tmp = name.indexOf(serviceName);
				if (tmp == -1) {
					name.add(serviceName);
					address.add(serviceInfo.getHost());
				}
/*				
				if (!serviceMap.containsKey(serviceName)) {
					serviceMap.put(serviceName, serviceInfo.getHost());
					Log.d(TAG, String.format("Service %s:%d found.", serviceInfo.getHost().getHostAddress(), serviceInfo.getPort()));
				}
  */              
			}
    		
    	};
    }
    
    public void initializeRegistrationListener() {
    	registrationListener = new NsdManager.RegistrationListener() {

			@Override
			public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
				Log.d(TAG, "Cannot register service(" + errorCode + ")");
				
			}

			@Override
			public void onServiceRegistered(NsdServiceInfo serviceInfo) {
				Log.d(TAG, "Service registered.");
				myServiceName = serviceInfo.getServiceName();
				
			}

			@Override
			public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
				Log.d(TAG, "Service unregistered.");
				
			}

			@Override
			public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
				Log.d(TAG, "Cannot unregister service(" + errorCode + ")");
				
			}
    		
    	};
    }
    
    public void registerService() {
    	if (!nsdRegistered) {
	        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
	        serviceInfo.setPort(Constants.SERVER_PORT);
	        serviceInfo.setServiceName(serviceName);
	        serviceInfo.setServiceType(SERVICE_TYPE);
	        
	        nsd.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
	        Log.d(TAG, "Service Port: " + Constants.SERVER_PORT + "registered.");
	        nsdRegistered = true;
    	}
    }
    
    public void unregisterService() {
    	if (nsdRegistered) {
    		nsd.unregisterService(registrationListener);
    		nsdRegistered = false;
    	}
    }

    public void discoverServices() {
    	if (!nsdDiscovering) {
    		nsd.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    		nsdDiscovering = true;
    	}
    }
    
    public List<InetAddress> getServiceInfoList() {
    	//List<InetAddress> result = new ArrayList<InetAddress>();
    	//result.addAll();
    	//return Collections.list(serviceMap.elements());
    	return address;
    }
  /*  
    public Map<String, InetAddress> getServiceMap() {
    	return serviceMap;
    }
    */
    public void removeByIp(InetAddress ip) {
		int tmp = address.indexOf(ip);
		if (tmp == -1) return;
		name.remove(tmp);
		address.remove(tmp);
	}
    
    public void stopDiscovery() {
    	if (nsdDiscovering) {
    		nsd.stopServiceDiscovery(discoveryListener);
    		nsdDiscovering = false;
    	}
    }
  /*  
    public void tearDown() {
        nsd.unregisterService(registrationListener);
    }*/
}
