package com.symlab.testoffloading;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.symlab.dandelion.OfflaodableMethod;
import com.symlab.dandelion.OffloadingHelper;
import com.symlab.dandelion.R;
import com.symlab.dandelion.db.DatabaseQuery;

public class MainActivity extends Activity {
	
	private static final String TAG = "TestMain";
	
	private TextView tv;
	private EditText startNumText;
	private EditText endNumText;
	private Button run;
	private Button search;
	
	private OffloadingHelper oHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity_main);
		
		tv = (TextView)findViewById(R.id.outputField);
		startNumText = (EditText) findViewById(R.id.startNum);
		endNumText = (EditText) findViewById(R.id.endNum);
		run = (Button) findViewById(R.id.run);
		run.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try {
					runTask(v);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		});
		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try {
					searchNode(v);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		String a = BluetoothAdapter.getDefaultAdapter().getAddress();
		println("My Address: " + a);
		Log.e(TAG, "My Address: " + a);
		//showDB(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	//controller.tearDown();
        	//Intent intent = new Intent(this, com.symlab.dandelion4.MainActivity.class);
			//startActivity(intent);
        	this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		oHelper = new OffloadingHelper(this);
		oHelper.initializeOHelper();
	}
	
	@Override
	protected void onStop() {
		oHelper.tearDownOHelper();
		oHelper = null;
		super.onStop();
		
	}

	
	public void runTask(View v) throws InterruptedException {
		long startNum = 1L;//Long.parseLong(startNumText.getText().toString());
		long endNum = 10000000L;//Long.parseLong(endNumText.getText().toString());
		clear_screen();
		println("Summing up...");
		println(String.format("StartNum: %d\nEndNum: %d", startNum, endNum));
		
		final int num = 1;
		final Long[] result = new Long[num];
		TestTask[] task = new TestTask[num];
		final OfflaodableMethod[] om = new OfflaodableMethod[num];
		
		final Class<?>[] paramTypes = {long.class, long.class};
		Method method = null;
		try {
			method = TestTask.class.getDeclaredMethod("compute_sum", paramTypes);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "No such method \"compute_sum\"");
			e.printStackTrace();
		}
		
		final Long startTime = System.nanoTime();
		for (int i = 0; i < num; i++) {
			result[i] = 0L;
					
			task[i] = new TestTask();
			Object[] paramValues = {startNum, endNum};
			om[i] = oHelper.postTask(task[i], method, paramValues, Long.class);
		}
		new Thread(new Runnable(){
			public void run() {
				
				runOnUiThread(new Runnable(){
					public void run() {
						for (int i = 0; i < num; i++) {
							Log.d(TAG, "The Result is " + om[i].getResult());
							println("The " + i + " result: " + om[i].getResult());
						}
						Long estimatedTime = System.nanoTime() - startTime;
						println("Total time: " + estimatedTime/1000000 + "ms");
					}
				});
			}
		}).start();
		
	}
	
	private void showDB(final MainActivity activity) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					DatabaseQuery query = new DatabaseQuery(activity);
					//String classMethodName = receiver.getClass().toString() + "#" + method.getName();
					final ArrayList<String> queryString = query.getData();
					activity.runOnUiThread(new Runnable(){
	
						@Override
						public void run() {
							//activity.clear_screen();
							for (int i = 0; i < queryString.size(); i++) {
								activity.print(queryString.get(i) + " ");
							}
							activity.println("");
						}
						
					});
				}
			}
			
		}).start();
	}
	
	public void searchNode(View v) {
		oHelper.testStart();
	}
	
	public void println(String s) {
		tv.append(s + "\n");
	}
	
	public void print(String s) {
		tv.append(s);
	}
	
	public void clear_screen() {
		tv.setText("");
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}

}
