package com.symlab.dandelion.profilers;

import android.annotation.TargetApi;
import android.os.Debug;
import android.util.Log;
import dalvik.bytecode.Opcodes;

// Sokol: removed VMDebug since it cannot be imported for android >= 2.3
//import dalvik.system.VMDebug;

public class ProgramProfiler {
	public String methodName;
	public Long execTime;
	public Long threadCpuTime;
	
	public int threadAllocSize;
	public int instructionCount;
	public int methodInvocationCount;
	public int gcThreadInvocationCount;
	public int gcGlobalInvocationCount;

	private Long mStartTime;
	private int mStartThreadAllocSize;
	private Long mStartThreadCpuTime;
	private int mStartThreadGcInvocationCount;
	private int mStartGlobalGcInvocationCount;
	
	private Debug.InstructionCount mICount;

	private static Integer profilersRunning;
	private static Boolean memAllocTrackerRunning;

	public ProgramProfiler() {
		methodName = "";
		mICount = new Debug.InstructionCount();
		if (memAllocTrackerRunning == null) {
			memAllocTrackerRunning = false;
			profilersRunning = 0;
		}
	}

	public ProgramProfiler(String mName) {
		methodName = mName;
		mICount = new Debug.InstructionCount();
		if (memAllocTrackerRunning == null) {
			memAllocTrackerRunning = false;
			profilersRunning = 0;
		}
	}

	public void startExecutionInfoTracking() {
		mStartTime = System.nanoTime();
		mStartThreadCpuTime = Debug.threadCpuTimeNanos();

		if (memAllocTrackerRunning == false) {
			Debug.startAllocCounting();
			memAllocTrackerRunning = true;
		}
		mStartThreadAllocSize = Debug.getThreadAllocSize();
		mStartThreadGcInvocationCount = Debug.getThreadGcInvocationCount();
		mStartGlobalGcInvocationCount = Debug.getGlobalGcInvocationCount();

		profilersRunning++;
		mICount.resetAndStart();
	}

	public void stopAndCollectExecutionInfoTracking() {
		mICount.collect();
		profilersRunning--;
		instructionCount = mICount.globalTotal();
		methodInvocationCount = mICount.globalMethodInvocations();
		threadAllocSize = Debug.getThreadAllocSize() - mStartThreadAllocSize;
		gcThreadInvocationCount = Debug.getThreadGcInvocationCount() - mStartThreadGcInvocationCount;
		gcGlobalInvocationCount = Debug.getGlobalGcInvocationCount() - mStartGlobalGcInvocationCount;
		
		if (profilersRunning == 0) {
			Debug.stopAllocCounting();
			memAllocTrackerRunning = false;
		}

		threadCpuTime = Debug.threadCpuTimeNanos() - mStartThreadCpuTime;
		execTime = System.nanoTime() - mStartTime;

		Log.d("PowerDroid-Profiler", methodName + ": Thread Alloc Size - " + (Debug.getThreadAllocSize() - mStartThreadAllocSize));
		Log.d("PowerDroid-Profiler", methodName + "Total instructions executed: " + instructionCount
				+ " Method invocations: " + methodInvocationCount + "in " + execTime / 1000000 + "ms");
	}

	
}
