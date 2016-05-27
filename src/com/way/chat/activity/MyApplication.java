package com.way.chat.activity;

import java.util.LinkedList;

import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.util.SharePreferenceUtil;

import android.app.Application;
import android.app.NotificationManager;
import android.util.Log;

public class MyApplication extends Application {
	private Client client;// 客户端
	private boolean isClientStart;// 客户端连接是否启动
	private NotificationManager mNotificationManager;
	private int newMsgNum = 0;// 后台运行的消息
	private LinkedList<RecentChatEntity> mRecentList;
	private RecentChatAdapter mRecentAdapter;
	private int recentNum = 0;
	
	private LinkedList<SeekInfoEntity> mSeekInfoList;
	private SeekInfoAdapter mSeekInfoAdapter;
	
	
	@Override
	public void onCreate() {
		SharePreferenceUtil util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		System.out.println(util.getIp() + " " + util.getPort());
		Log.e("MyApplication", util.getIp() + " " + util.getPort());
		client = new Client(util.getIp(), util.getPort());// 从配置文件中读ip和地址
		
		mRecentList = new LinkedList<RecentChatEntity>();
		mRecentAdapter = new RecentChatAdapter(getApplicationContext(), mRecentList);
		
		mSeekInfoList = new LinkedList<SeekInfoEntity>();
		mSeekInfoAdapter = new SeekInfoAdapter(getApplicationContext(), mSeekInfoList); 
		
		super.onCreate();
	}

	public Client getClient() {
		return client;
	}

	public boolean isClientStart() {
		return isClientStart;
	}

	public void setClientStart(boolean isClientStart) {
		this.isClientStart = isClientStart;
	}

	public NotificationManager getmNotificationManager() {
		return mNotificationManager;
	}

	public void setmNotificationManager(NotificationManager mNotificationManager) {
		this.mNotificationManager = mNotificationManager;
	}

	public int getNewMsgNum() {
		return newMsgNum;
	}

	public void setNewMsgNum(int newMsgNum) {
		this.newMsgNum = newMsgNum;
	}

	public LinkedList<RecentChatEntity> getmRecentList() {
		return mRecentList;
	}

	public void setmRecentList(LinkedList<RecentChatEntity> mRecentList) {
		this.mRecentList = mRecentList;
	}

	public RecentChatAdapter getmRecentAdapter() {
		return mRecentAdapter;
	}

	public void setmRecentAdapter(RecentChatAdapter mRecentAdapter) {
		this.mRecentAdapter = mRecentAdapter;
	}

	public int getRecentNum() {
		return recentNum;
	}

	public void setRecentNum(int recentNum) {
		this.recentNum = recentNum;
	}
	
	public LinkedList<SeekInfoEntity> getSeekInfoList(){
		return mSeekInfoList;
	}
	
	public void setSeekInfoList(LinkedList<SeekInfoEntity> mSeekInfoEntity){
		this.mSeekInfoList = mSeekInfoEntity;
	}
	
	public SeekInfoAdapter getSeekInfoAdapter(){
		return mSeekInfoAdapter;
	}
	
	public void setSeekInfoAdapter(SeekInfoAdapter mSeekInfoAdapter){
		this.mSeekInfoAdapter = mSeekInfoAdapter;
	}
}
