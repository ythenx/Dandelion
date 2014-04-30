package com.symlab.dandelion;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.symlab.dandelion.network.BluetoothHelper;
import com.symlab.dandelion.network.BluetoothInterface;
import com.symlab.dandelion.network.ConnectedDeviceList;
import com.symlab.dandelion.network.NetworkInterface;

public class OffloadingService extends Service {
	
	public static final String TAG = "OffloadingService";
	private long deviceId;
	//private Map<Long, Master> deviceTable;
	
	public static boolean serviceStarted = false;
	
	//private NsdHelper nsdHelper;
	//private ServiceSocket st;
	private DeviceStatus ds;
	private StatusTable status;
	private TaskQueue queue;
	private TaskQueueHandler tqh;
	private NetworkInterface ni;
	private ConnectedDeviceList deviceList;
	
	//private BluetoothHelper bh;
	
	private StatusUpdater su;
	
	private int updateInterval = 60000;
	
	private void toast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
	
	public StatusTableData getStatusTableData(){
		return status.getStatusTableData();
	}
	
	public void updateDeviceList(){
		
	}
	
	private final IOffloadingService.Stub mBinder = new IOffloadingService.Stub() {

		@Override
		public Status getMyStatus() throws RemoteException {
			return myStatus();
		}

		@Override
		public long getMyDeviceId() throws RemoteException {
			return getDeviceId();
		}

		@Override
		public void registerNsd() throws RemoteException {
			ni.discoverService();	
			toast("Discovering");
			
		}

		@Override
		public void unregisterNsd() throws RemoteException {
			ni.stopDiscovery();
		}

		@Override
		public String displayStatusTable() throws RemoteException {
			return status.displayTable();
		}

		@Override
		public void updateStatusTable() throws RemoteException {
			try {
				status.updateStatus(deviceList);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Log.d(TAG, "Call updateStatus");
		}

		@Override
		public void discoverNSD() throws RemoteException {
			su.startUpdate();
		}

		@Override
		public void stopDiscoverNSD() throws RemoteException {
			su.stopUpdate();
		}

		@Override
		public StatusTableData getStatusTableData() throws RemoteException {
			return status.getStatusTableData();
		}

		@Override
		public void addTaskToQueue(OfflaodableMethod om) throws RemoteException {
			queue.enqueue(om);
		}

		
	};
	
	public static synchronized void setServiceOn() {
		serviceStarted = true;
	}
	
	public static synchronized void setServiceOff() {
		serviceStarted = false;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		//return mMessenger.getBinder();
		return mBinder;
	}


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
     	generateDeviceId();
    	//deviceTable = new ConcurrentHashMap<Long, Master>();
    	//st = new ServiceSocket(this);
    	//nsdHelper = new NsdHelper(this);
     	//bh = new BluetoothHelper(this);
     	deviceList = new ConnectedDeviceList();
    	ni = new BluetoothInterface(this, deviceList);
    	ds = new DeviceStatus(this);
    	status = new StatusTable(this);
    	su = new StatusUpdater(ni,deviceList, status, updateInterval);
    	su.startUpdate();
    	queue = new TaskQueue();
    	tqh = new TaskQueueHandler(queue, this, deviceList);
    	tqh.start();
    	//nsdHelper.initializeNsd();
    	//st.start();
    	//register_nsd();
    	Log.d(TAG, "Service Started!");
    	//su.startUpdate();
        return START_STICKY;
    }

    public void generateDeviceId() {
		//deviceId = new Random(System.nanoTime()).nextLong();
    	WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	WifiInfo info = manager.getConnectionInfo();
    	String address = String.format("%s%s%s%s%s%s", (Object[])info.getMacAddress().split(":"));
    	//Toast.makeText(this, address, Toast.LENGTH_LONG).show();
    	//Toast.makeText(this, Long.toHexString(Long.parseLong(address, 16)), Toast.LENGTH_LONG).show();
    	deviceId = Long.parseLong(address, 16);
	}
    
    public Long getDeviceId() {
    	return deviceId;
    }
   /* 
    public boolean deviceExists(long id) {
    	return deviceTable.containsKey(id);
    }
    
    public void addDevice(long id, Master m) {
    	deviceTable.put(id, m);
    }
    
    public void removeDevice(long id) {
    	deviceTable.remove(id);
    }
    */
    public Status myStatus() {
    	return ds.readStatus();
    }
 /*   
    public void removeNodeByIp(InetAddress ip) {
    	nsdHelper.removeByIp(ip);
    }
 */   
    
    @Override
    public void onDestroy() {
    	Log.d(TAG, "Service Stopped!");
    	//su.stopUpdate();
    	//unregister_nsd();
    	//stopDiscover_nsd();
    	super.onDestroy();
    }
  /*  
    public void register_nsd() {
    	st = new ServiceSocket(this);
    	st.start();
    	nsdHelper.registerService();
    }
    
    public void unregister_nsd() {
    	nsdHelper.unregisterService();
    	try {
			st.closeServer();
			//st = null;
			Log.e(TAG, "Call here!!!!!!!!!!!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void discover_nsd() {
    	nsdHelper.discoverServices();
    }
    
    public void stopDiscover_nsd() {
    	nsdHelper.stopDiscovery();
    }
*/
}

    