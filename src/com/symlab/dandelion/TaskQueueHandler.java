package com.symlab.dandelion;

import java.util.List;
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
	
	private OffloadingService mContext;
	private ConnectedDeviceList deviceList;
	
	private ExecutorService executor = Executors.newCachedThreadPool();

	private DecisionMaker dmaker;
	public TaskQueueHandler(TaskQueue q, OffloadingService context, ConnectedDeviceList deviceList) {
		mContext = context;
		lock = new Object();
		queue = q;
		this.deviceList = deviceList;
		dmaker = new DecisionMaker(context.getStatusTableData());
		//list = new ArrayList<TaskWrapper<?>>();
	}
	
	private void submitTask(OfflaodableMethod om, String target) {
		TaskWrapper tw = new TaskWrapper(mContext, om, target, deviceList);
		om.setHolder(executor.submit(tw));
		om.setResultReady();
		deviceList.setJobStatusToBusy(target);
	}
	
	@Override
	public void run() {
		while (!stopped && !Thread.currentThread().isInterrupted()) {
			while (queue.queueSize() != 0) {
//				int qSize = queue.queueSize();
//				if(deviceList.size()*qSize<Constants.DECISION_MAKER_THRESHOLD){
//					List<String> devices = dmaker.globalOptimalDecision(queue);
//					for(int i=0; i<qSize; i++){
//						
//						submitTask(queue.dequeue(),devices.get(i));
//					}
//				}
//				else{
				List<String> devices = dmaker.greedyDecision(queue,deviceList);
				for(int i=0; i<devices.size(); i++)
					submitTask(queue.dequeue(), devices.get(i));
//				}
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
