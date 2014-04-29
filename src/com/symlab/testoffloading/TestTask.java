package com.symlab.testoffloading;


import com.symlab.dandelion.Offloadable;

public class TestTask extends Offloadable {

	private static final long serialVersionUID = -8294351189997833666L;
	private static final String TAG = "TestTask";
	
	
	
	public TestTask() {
		
	}
	
		
	public long compute_sum(long startNum, long endNum) {
		long resultSum = 0;
		for (long i = startNum; i <= endNum; i++) {
			resultSum += i;
		}
		return resultSum;
	}

	@Override
	public void copyState(Offloadable state) {
		
	}
}
