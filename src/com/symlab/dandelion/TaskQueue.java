package com.symlab.dandelion;

import java.util.LinkedList;

public class TaskQueue {

	private LinkedList<OfflaodableMethod> queue;
	
	public TaskQueue() {
		queue = new LinkedList<OfflaodableMethod>();
	}
	
	public void enqueue(OfflaodableMethod m) {
		queue.addLast(m);
	}
	
	public OfflaodableMethod dequeue() {
		return queue.removeFirst();
	}
	
	public int queueSize() {
		return queue.size();
	}
	
	public void clearQueue() {
		queue.clear();
	}
}
