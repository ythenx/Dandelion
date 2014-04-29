package com.symlab.dandelion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.symlab.dandelion.network.ConnectedDeviceList;

import android.content.Context;

public class TaskQueueHandler extends Thread {
	
	private TaskQueue queue;
	//private ArrayList<TaskWrapper<?>> list;
	private boolean paused = false;
	private boolean stopped = false;
	private Object lock;
	
	private Context mContext;
	private ConnectedDeviceList deviceList;
	
	private ExecutorService executor = Executors.newCachedThreadPool();

	public TaskQueueHandler(TaskQueue q, Context context, ConnectedDeviceList deviceList) {
		mContext = context;
		lock = new Object();
		queue = q;
		this.deviceList = deviceList;
		//list = new ArrayList<TaskWrapper<?>>();
	}
	
	private void submitTask(OfflaodableMethod om, String target) {
		TaskWrapper tw = new TaskWrapper(mContext, om, (target != null),deviceList.getOutputStream(target), deviceList.getInputStream(target));
		om.setHolder(executor.submit(tw));
		om.setResultReady();
	}
	
	@Override
	public void run() {
		while (!stopped && !Thread.currentThread().isInterrupted()) {

			while (queue.queueSize() != 0) {
				try {
					submitTask(queue.dequeue(), null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//queue.clearQueue();
			
			
	        synchronized(lock) {
	            while (paused) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						break;
					}
	            }
	        }
		}

	}
	
	public void pause() {
		synchronized(lock) {
			paused = true;
		}
	}
	
	public void resumeThread() {
		synchronized(lock) {
			paused = false;
			lock.notifyAll();
		}
    }
}
