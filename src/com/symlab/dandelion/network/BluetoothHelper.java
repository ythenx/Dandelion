package com.symlab.dandelion.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothHelper {

	private static final String TAG = "BluetoothHelper";
	private static final String NAME = "DandelionBluetooth";
	
	private static final UUID DANDELION_UUID = UUID.fromString("36ca5460-c301-11e3-8a33-0800200c9a66");
	
	private Context context;
	//private Handler handler;
	private final BluetoothAdapter mAdapter;
	
	private BluetoothServerSocket bss;
	private BluetoothSocket bs;
	
	private boolean receiverRegistered = false;
	
	private CopyOnWriteArrayList<String> deviceList;
	private HashMap<String, BluetoothSocket> sockets;
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            deviceList.addIfAbsent(device.getAddress());
	            //Message.obtain(handler, 2, device.getAddress()).sendToTarget();
	        }
	    }
	};
	
	private void registerReceiver() {
		if (receiverRegistered) return;
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(mReceiver, filter);
		receiverRegistered = true;
		try {
			bss = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, DANDELION_UUID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void unRegisterReceiver() {
		if (!receiverRegistered) return;
		context.unregisterReceiver(mReceiver);
		receiverRegistered = false;
	}
	
	public BluetoothHelper(Context context) {
		this.context = context;
		//this.handler = handler;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mAdapter == null) {
			Log.e(TAG, "Device does not support Bluetooth");
		}
		deviceList = new CopyOnWriteArrayList<String>();
		sockets = new HashMap<String, BluetoothSocket>();
	}
	
	public void registerService() {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		context.startActivity(discoverableIntent);
	}
	
	public void discoverNode() {
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	deviceList.addIfAbsent(device.getAddress());
		    	//Message.obtain(handler, 1, device.getAddress()).sendToTarget();
		    }
		}
		registerReceiver();
		mAdapter.startDiscovery();
	}
	
	public void stopDiscovery() {
		mAdapter.cancelDiscovery();
		unRegisterReceiver();
	}
	
	class ServiceSocket extends Thread {
		
		
		@Override
	    public void run() {
			BluetoothSocket socket = null;
			Log.d(TAG, "ServerSocket Created, awaiting connection");
			while (!Thread.interrupted()) {
				try {
					socket = bss.accept();
					sockets.put(socket.getRemoteDevice().getAddress(), socket);
				} catch (IOException e) {
					e.printStackTrace();
				}
	            
	            
	        }
			
		}
	}
}
