package com.symlab.dandelion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.util.Log;

public class ServiceSocket extends Thread {
	
	ServerSocket ssocket = null;
	private InetAddress address;
    private static final String TAG = "ServiceSocket";
    private Context context;

	public ServiceSocket(Context context) {
		try {
			this.context = context;
			ssocket = new ServerSocket(Constants.SERVER_PORT);
			address = ssocket.getInetAddress();
			Log.d(TAG, "Service Socket: " + address.getHostAddress() + ':' + Constants.SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
    public void run() {
		Log.d(TAG, "ServerSocket Created, awaiting connection");
		while (!Thread.interrupted()) {
            
            try {
            	address = InetAddress.getLocalHost();
            	//Log.d(TAG, "Service Socket Now: " + address.getHostAddress() + ':' + port);
				Socket socket = ssocket.accept();
				if (socket != null && !ssocket.isClosed()) {
					//new Master(socket, context).start();
					//Log.d(TAG, socket.getInetAddress().getHostAddress() + ':' + socket.getPort() + " Connected.");

				}
			} catch (IOException e) {
				if (ssocket != null && !ssocket.isClosed())
					try {
						ssocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				e.printStackTrace();
			}
            
        }
		try {
			if (ssocket != null && !ssocket.isClosed()) {
				ssocket.close();
			}
			Log.d(TAG, "ServerSocket Closed, Good!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeServer() throws IOException {
		ssocket.close();
		this.interrupt();
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
}
