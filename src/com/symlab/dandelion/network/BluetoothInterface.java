package com.symlab.dandelion.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.symlab.dandelion.DynamicObjectInputStream;
import com.symlab.dandelion.Master;

public class BluetoothInterface implements NetworkInterface {
	
	private static final String TAG = "BluetoothInterface**";
	
	private static final String NAME = "DandelionBluetooth";
	private static final UUID DANDELION_UUID = UUID.fromString("36ca5460-c301-11e3-8a33-0800200c9a66");
	
	private final BluetoothAdapter mAdapter;
	private BluetoothServerSocket bss;
	private boolean receiverRegistered = false;
	
	private boolean occupied = false;
	private boolean resolved = false;
	private Object lock;
	
	private Context mContext;
	private ConnectedDeviceList mDeviceList;
	private CopyOnWriteArrayList<BluetoothDevice> tempDeviceList;
	
	public BluetoothInterface(Context context, ConnectedDeviceList deviceList) {
		mContext = context;
		mDeviceList = deviceList;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		tempDeviceList = new CopyOnWriteArrayList<BluetoothDevice>();
		if (mAdapter == null) {
			Log.e(TAG, "Device does not support Bluetooth");
			return;
		}
		//Log.e(TAG, "My Scan Mode is " + mAdapter.getScanMode());
		if (mAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			askForDiscoverable();
		}
		new BluetoothServiceServer().start();
	}
	
	private void askForDiscoverable() {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(discoverableIntent);
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            tempDeviceList.addIfAbsent(device);

	        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) && !resolved) {
	        	resolved = true;
	        	resolveConnections();
	        }
	    }
	};
	
	private void registerReceiver() {
		if (receiverRegistered) return;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mContext.registerReceiver(mReceiver, intentFilter);
		receiverRegistered = true;
		
	}
	
	private void resolveConnections() {
		ExecutorService pool = Executors.newCachedThreadPool();
		mAdapter.cancelDiscovery();
		Log.e(TAG, "" + tempDeviceList.size());
		for (BluetoothDevice device : tempDeviceList) {
			Log.e(TAG, "Device***" + device.getAddress());
			pool.execute(new ResolveTask(device));
			
		}
	}
	
	private class ResolveTask implements Runnable {
		private BluetoothDevice mDevice;
		private BluetoothSocket bs = null;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;
		
		public ResolveTask(BluetoothDevice device) {
			mDevice = device;
		}
		
		@Override
		public void run() {
			try {
				try {
					bs = mDevice.createInsecureRfcommSocketToServiceRecord(DANDELION_UUID);
				} catch (IOException e) {
					Log.e(TAG, "Cannot create socket to " + mDevice.getAddress());
					e.printStackTrace();
				}
				try {
					bs.connect();
					Log.d(TAG, "Connected to " + mDevice.getAddress());
				} catch (IOException e) {
					Log.e(TAG, "Cannot connect to device " + mDevice.getAddress());
					Log.e(TAG, "Cannot connect to device "  + e.getMessage()!=null?e.getMessage():e.toString());
					return;
				}
				try {
					//oos = new ObjectOutputStream(new BufferedOutputStream(bs.getOutputStream()));
					oos = new ObjectOutputStream(bs.getOutputStream());
					Log.d(TAG, "Setup Output stream to " + mDevice.getAddress());
					//ois = new ObjectInputStream(new BufferedInputStream(bs.getInputStream()));
					ois = new ObjectInputStream(bs.getInputStream());
					Log.d(TAG, "Setup Input stream to " + mDevice.getAddress());
				} catch (IOException e) {
					Log.e(TAG, "Cannot setup streams to device " + mDevice.getAddress());
					return;
				}
				mDeviceList.addIfAbsent(bs.getRemoteDevice().getAddress(), ois, oos);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}
	}
	
	private class BluetoothServiceServer extends Thread {
		
		private BluetoothSocket mSocket = null;
		private DynamicObjectInputStream ois = null;
		private ObjectOutputStream oos = null;

		@Override
		public void run() {
			while(true) {
				try {
					bss = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, DANDELION_UUID);
					mSocket = bss.accept();
					if (mSocket != null) {
						//ois = new DynamicObjectInputStream(new BufferedInputStream(mSocket.getInputStream()));
						ois = new DynamicObjectInputStream(mSocket.getInputStream());
						Log.d(TAG, "Server Setup Input stream to " + mSocket.getRemoteDevice().getAddress());
						//oos = new ObjectOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
						oos = new ObjectOutputStream(mSocket.getOutputStream());
						Log.d(TAG, "Server Setup Output stream to " + mSocket.getRemoteDevice().getAddress());
						Thread t = new Master(ois, oos, mContext);
						t.start();
						try {
							t.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				/*
				occupied = true;
				synchronized(lock) {
		            while (occupied) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							break;
						}
		            }
		        }
				*/
			}
		}
	}
	
	public void resumeServer() {
		synchronized(lock) {
			occupied = false;
			lock.notifyAll();
		}
    }

	@Override
	public void send(String target, DataPackage data) {
		ObjectOutputStream oos = null;
		oos = mDeviceList.getOutputStream(target);
		try {
			oos.writeObject(data);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DataPackage receive(String target) {
		ObjectInputStream ois = null;
		DataPackage ret = null;
		ois = mDeviceList.getInputStream(target);
		try {
			ret = (DataPackage) ois.readObject();
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public void discoverService() {
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	tempDeviceList.addIfAbsent(device);
		    }
		}
		registerReceiver();
		resolved = false;
		mAdapter.startDiscovery();
		
	}

	@Override
	public void stopDiscovery() {
		mAdapter.cancelDiscovery();
		
	}

}
