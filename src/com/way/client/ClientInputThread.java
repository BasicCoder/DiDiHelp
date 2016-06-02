package com.way.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.way.chat.common.tran.bean.TranObject;

import android.util.Log;

/**
 * 客户端读消息线程
 * 
 * @author way
 * 
 */
public class ClientInputThread extends Thread {
	private Socket socket;
	private TranObject msg;
	private boolean isStart = true;
	private ObjectInputStream ois;
	private MessageListener messageListener;// 消息监听接口对象

	public ClientInputThread(Socket socket) {
		this.socket = socket;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提供给外部的消息监听方法
	 * 
	 * @param messageListener
	 *            消息监听接口对象
	 */
	public void setMessageListener(MessageListener messageListener) {
		if(messageListener != null){
			this.messageListener = messageListener;
		}else{
			Log.e("ClientInputThread", "Someone may want to explode this");
		}
		
		if(this.messageListener != null)
			Log.e("ClientInputThread", "setMsgListener");
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				Log.e("ClientInputThread", "ReadMsg");
				msg = (TranObject) ois.readObject();
				Log.e("ClientInputThread", "GetMsg: "+ msg);
				
				if(msg == null)
					Log.e("ClientInputThread", "msg == null");
				if(messageListener == null)
					Log.e("ClientInputThread", "messageListener == null");
				if(msg == null || messageListener == null){
					Log.e("debug", "caonimahehe");
					continue;
				}
				// 每收到一条消息，就调用接口的方法，并传入该消息对象，外部在实现接口的方法时，就可以及时处理传入的消息对象了
				// 我不知道我有说明白没有？
				Log.e("ClientInputThread", "BroadcastSend");
				messageListener.Message(msg);
			}
			ois.close();
			if (socket != null)
				socket.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
