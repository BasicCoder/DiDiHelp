package com.way.chat.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.way.chat.common.bean.SeekInfoEntity;
import com.way.chat.common.bean.SeekInfoMessage;
import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.ClientInputThread;
import com.way.client.ClientOutputThread;
import com.way.client.MessageListener;
import com.way.util.GroupFriend;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;
import com.way.util.UserDB;

/**
 * 好友列表的Activity
 * 
 * @author way
 * 
 */
public class FriendListActivity extends MyActivity implements OnClickListener {


	private static final int PAGE1 = 0;// 页面1
	private static final int PAGE2 = 1;// 页面2
	private static final int PAGE3 = 2;// 页面3
	private static final int PAGE4 = 3;// 页面4
	private int currentIndex = PAGE1; // 默认选中第1个，可以动态的改变此参数值
	private ImageView cursor;// 标题背景图片
	
	private List<GroupFriend> group;// 需要传递给适配器的数据
	private String[] groupName = { "我的好友", "我的同学", "我的家人" };// 大组成员名
	private SharePreferenceUtil util;
	private UserDB userDB;// 保存好友列表数据库对象
	private MessageDB messageDB;// 消息数据库对象

	private ListView mSeekInfoListView; //求伞listView
	
	private ImageView mImgSend; //发布信息配图
	private TextView mAddressSend; //发布地址
	private EditText mSaysSend; //发布需求
	private Button mClear; //清空
	private Button mSend; //发送
	private Uri imageUri;
	private String fileName;
	public static final int TAKE_PHOTO = 1;  
	public static final int CROP_PHOTO = 2;  
	
	private MyListView myListView;// 好友列表自定义listView
	private MyExAdapter myExAdapter;// 好

	private ImageView mImgPersonal; //个人头像
	private EditText mSaysPersonal; //个人留言
	
	private ListView mRecentListView;// 最近会话的listView
	private int newNum = 0;

	private ListView mGroupListView;// 群组listView
	
	
	private ViewPager mPager;
	private List<View> mListViews;// Tab页面
	private LinearLayout layout_body_activity;
	
	
	private ImageView recent_umbrella;// 最近求伞
	private ImageView add_umbrella;// 添加伞
	private ImageView resent_chat;// 最近会话
	private ImageView user_info;//个人信息

	private List<ImageView> imageTab;
	private List<Drawable> drawableTab;
	private List<String> titleTab;
	private TextView title;

	
	private float offset = 0;// 动画图片偏移量
	private int bmpW;// 动画图片宽度

	private TranObject msg;
	private List<User> list;
	private MenuInflater mi;// 菜单
	private int[] imgs = { R.drawable.icon, R.drawable.f1, R.drawable.f2,
			R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.f6,
			R.drawable.f7, R.drawable.f8, R.drawable.f9 };// 头像资源
	private MyApplication application;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.friend_list);
		application = (MyApplication) this.getApplicationContext();
		initData();// 初始化数据
		initImageView();// 初始化动画
		initUI();// 初始化界面
	}

	@Override
	protected void onResume() {// 如果从后台恢复，服务被系统干掉，就重启一下服务
		// TODO Auto-generated method stub
		newNum = application.getRecentNum();// 从新获取一下全局变量
		if (!application.isClientStart()) {
			Intent service = new Intent(this, GetMsgService.class);
			startService(service);
		}
		new SharePreferenceUtil(this, Constants.SAVE_USER).setIsStart(false);
		NotificationManager manager = application.getmNotificationManager();
		if (manager != null) {
			manager.cancel(Constants.NOTIFY_ID);
			application.setNewMsgNum(0);// 把消息数目置0
			application.getmRecentAdapter().notifyDataSetChanged();
		}
		super.onResume();
	}

	/**
	 * 初始化系统数据
	 */
	private void initData() {
		
		
		
		userDB = new UserDB(FriendListActivity.this);// 本地用户数据库
		messageDB = new MessageDB(this);// 本地消息数据库
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);

		msg = (TranObject) getIntent().getSerializableExtra(Constants.MSGKEY);// 从intent中取出消息对象
		if (msg == null) {// 如果为空，说明是从后台切换过来的，需要从数据库中读取好友列表信息
			list = userDB.getUser();
		} else {// 如果是登录界面切换过来的，就把好友列表信息保存到数据库
			list = (List<User>) msg.getObject();
			userDB.updateUser(list);
		}
		initListViewData(list);
		
		SendGetSeekInfoList();
	}
	
	private void SendGetSeekInfoList(){
		ClientOutputThread out = application.getClient().getClientOutputThread();
		TranObject o = new TranObject(TranObjectType.SEEKINFO);
		o.setFromUser(Integer.parseInt(util.getId()));
		out.setMsg(o);
	}
	
	/**
	 * 处理服务器传递过来的用户数组数据，
	 * 
	 * @param list
	 *            从服务器获取的用户数组
	 */
	private void initListViewData(List<User> list) {
		group = new ArrayList<GroupFriend>();// 实例化
		for (int i = 0; i < groupName.length; ++i) {// 根据大组的数量，循环给各大组分配成员
			List<User> child = new ArrayList<User>();// 装小组成员的list
			GroupFriend groupInfo = new GroupFriend(groupName[i], child);// 我们自定义的大组成员对象
			for (User u : list) {
				if (u.getGroup() == i)// 判断一下是属于哪个大组
					child.add(u);
			}
			group.add(groupInfo);// 把自定义大组成员对象放入一个list中，传递给适配器
		}
	}

	/**
	 * 初始化动画
	 */
	private void initImageView() {
		cursor = (ImageView) findViewById(R.id.tab_bg);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.bk).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		// System.out.println("屏幕宽度:" + screenW);
		offset = (float)screenW / 4 ;// 计算偏移量:屏幕宽度/4，平分为4分
		
		LayoutParams para; 
		para = cursor.getLayoutParams();  
		para.width = screenW / 4;
		cursor.setLayoutParams(para); 
	}

	private void initUI() {
		mi = new MenuInflater(this);
		layout_body_activity = (LinearLayout) findViewById(R.id.bodylayout);
		
		title = (TextView) findViewById(R.id.title);
		recent_umbrella = (ImageView) findViewById(R.id.tab1);
		recent_umbrella.setOnClickListener(this);
		add_umbrella = (ImageView) findViewById(R.id.tab2);
		add_umbrella.setOnClickListener(this);
		resent_chat = (ImageView) findViewById(R.id.tab3);
		resent_chat.setOnClickListener(this);
		user_info = (ImageView) findViewById(R.id.tab4);
		user_info.setOnClickListener(this);

		cursor = (ImageView) findViewById(R.id.tab_bg);

		layout_body_activity.setFocusable(true);

		mPager = (ViewPager) findViewById(R.id.viewPager);
		mListViews = new ArrayList<View>();
		LayoutInflater inflater = LayoutInflater.from(this);
		View lay1 = inflater.inflate(R.layout.tab1, null);
		View lay2 = inflater.inflate(R.layout.tab2, null);
		View lay3 = inflater.inflate(R.layout.tab3, null);
		View lay4 = inflater.inflate(R.layout.tab4, null);
		mListViews.add(lay1);
		mListViews.add(lay2);
		mListViews.add(lay3);
		mListViews.add(lay4);
		
		mPager.setAdapter(new MyPagerAdapter(mListViews));
		mPager.setCurrentItem(PAGE1);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		imageTab = new ArrayList<ImageView>();
		imageTab.add(recent_umbrella);
		imageTab.add(add_umbrella);
		imageTab.add(resent_chat);
		imageTab.add(user_info);
		
		drawableTab = new ArrayList<Drawable>();
		drawableTab.add(getResources().getDrawable(R.drawable.umbrella1));
		drawableTab.add(getResources().getDrawable(R.drawable.umbrella2));
		drawableTab.add(getResources().getDrawable(R.drawable.add1));
		drawableTab.add(getResources().getDrawable(R.drawable.add2));
		drawableTab.add(getResources().getDrawable(R.drawable.chat1));
		drawableTab.add(getResources().getDrawable(R.drawable.chat2));
		drawableTab.add(getResources().getDrawable(R.drawable.user1));
		drawableTab.add(getResources().getDrawable(R.drawable.user2));
		
		imageTab.get(0).setImageDrawable(drawableTab.get(1));
		
		titleTab = new ArrayList<String>();
		titleTab.add("借伞列表");
		titleTab.add("添加借伞");
		titleTab.add("最近联系");
		titleTab.add("个人信息");
		
		// 求伞信息列表
		mSeekInfoListView =(ListView) lay1.findViewById(R.id.tab1_listView);
		List<SeekInfoEntity> seekInfoList = application.getSeekInfoList();
		seekInfoList.add(new SeekInfoEntity(R.drawable.umbrella1, "test1", "test1", "This is a test."));
		seekInfoList.add(new SeekInfoEntity(R.drawable.umbrella1, "test2", "test2", "This is a test."));
		//SeekInfoAdapter seekInfoAdapter = new SeekInfoAdapter(this, (LinkedList<SeekInfoEntity>)seekInfoList);
		mSeekInfoListView.setAdapter(application.getSeekInfoAdapter());
		mSeekInfoListView.setOnItemClickListener(new Tab1ListViewItemClick());
		
		
		// 下面是发布求伞信息处理
		mImgSend = (ImageView) lay2.findViewById(R.id.img_send);
		mAddressSend = (TextView) lay2.findViewById(R.id.address_send);
		mSaysSend = (EditText) lay2.findViewById(R.id.says_send);
		mClear = (Button) lay2.findViewById(R.id.clear_btn);
		mSend = (Button) lay2.findViewById(R.id.publish_btn);
		mClear.setOnClickListener(new Tab2ClickEvent());
		mSend.setOnClickListener(new Tab2ClickEvent());
		mImgSend.setOnClickListener(new Tab2ClickEvent());
		
		// 下面是处理好友列表界面处理
		// myListView = (MyListView) lay3.findViewById(R.id.tab3_listView);
		// myExAdapter = new MyExAdapter(this, group);
		// myListView.setAdapter(myExAdapter);
		// myListView.setGroupIndicator(null);// 不设置大组指示器图标，因为我们自定义设置了
		// myListView.setDivider(null);// 设置图片可拉伸的
		// myListView.setFocusable(true);// 聚焦才可以下拉刷新
		// myListView.setonRefreshListener(new MyRefreshListener());
		
		// 下面是最近会话界面处理
		mRecentListView = (ListView) lay3.findViewById(R.id.tab3_listView);
		// mRecentAdapter = new RecentChatAdapter(FriendListActivity.this,
		// application.getmRecentList());// 从全局变量中获取最近聊天对象数组
		mRecentListView.setAdapter(application.getmRecentAdapter());// 先设置空对象，要么从数据库中读出

		// 下面是个人信息界面处理
		mImgPersonal = (ImageView) lay4.findViewById(R.id.img_personal);
		//mSaysPersonal = (EditText) lay4.findViewById(R.id.says_personal);
		
		// 下面是群组界面处理
		/*
		mGroupListView = (ListView) lay3.findViewById(R.id.tab3_listView);
		List<GroupEntity> groupList = new ArrayList<GroupEntity>();
		GroupEntity entity = new GroupEntity(0, "C175地带", "怀念高中生活...");
		GroupEntity entity2 = new GroupEntity(0, "Android开发",
				"爱生活...爱Android...");
		groupList.add(entity);
		groupList.add(entity2);
		GroupAdapter adapter = new GroupAdapter(this, groupList);
		mGroupListView.setAdapter(adapter);
		*/
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tab1:
			mPager.setCurrentItem(PAGE1);// 点击页面1
			break;
		case R.id.tab2:
			mPager.setCurrentItem(PAGE2);// 点击页面2
			break;
		case R.id.tab3:
			mPager.setCurrentItem(PAGE3);// 点击页面3
			break;
		case R.id.tab4:
			mPager.setCurrentItem(PAGE4);// 点击页面4
			break;
		default:
			break;
		}
	}
	
	class Tab1ListViewItemClick implements AdapterView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			List<SeekInfoEntity> seekInfoList = application.getSeekInfoList();
			User u = new User();
			SeekInfoEntity seekInfoEntity = seekInfoList.get(position);
			String name = seekInfoEntity.getName();
			int Id = seekInfoEntity.getId();
			int img = seekInfoEntity.getImg();
			u.setName(name);
			u.setId(Id);
			u.setImg(img);
			Intent intent = new Intent(FriendListActivity.this, ChatActivity.class);
			intent.putExtra("user", u);
			startActivity(intent);
		}
	}

	class Tab2ClickEvent implements View.OnClickListener{
		@Override
		public void onClick(View v){
			switch(v.getId()){
				case R.id.clear_btn:
					mSaysSend.setText("");
					break;
				case R.id.publish_btn:
					if(application.isClientStart()) {
						ClientOutputThread out = application.getClient().getClientOutputThread();
						TranObject o = new TranObject(TranObjectType.PUBLISHINFO);
						SeekInfoEntity publishSeekInfo = new SeekInfoEntity();
						//publishSeekInfo.setImg(img);
						publishSeekInfo.setId(Integer.parseInt(util.getId()));
						publishSeekInfo.setName(util.getName());
						String str = "";
						str = mSaysSend.getText().toString();
						if(str.length() >= 1){
							publishSeekInfo.setSays(str);
						}
						
						o.setObject(publishSeekInfo);
						o.setFromUser(Integer.parseInt(util.getId()));
						out.setMsg(o);
						mSaysSend.setText("");
					}
					break;
				case R.id.img_send:
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		            Date date = new Date(System.currentTimeMillis());
		            fileName = format.format(date);
		            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		            File outputImage = new File(path, fileName+".jpg");
		            try {
		                if(outputImage.exists()) {
		                    outputImage.delete();
		                }
		                outputImage.createNewFile();
		            } catch(IOException e) {
		                e.printStackTrace();
		            }
		            imageUri = Uri.fromFile(outputImage);
		            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
		            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
		            startActivityForResult(intent, TAKE_PHOTO); //启动照相
		            
					break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mi.inflate(R.menu.friend_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (messageDB != null)
			messageDB.close();
	}

	@Override
	// 菜单选项添加事件处理
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.friend_menu_add:
			Toast.makeText(getApplicationContext(), "亲！此功能暂未实现哦", 0).show();
			break;
		case R.id.friend_menu_exit:
			exitDialog(FriendListActivity.this, "QQ提示", "亲！您真的要退出吗？");
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 完全退出提示窗
	private void exitDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 关闭服务
						if (application.isClientStart()) {
							Intent service = new Intent(
									FriendListActivity.this,
									GetMsgService.class);
							stopService(service);
						}
						close();// 父类关闭方法
					}
				}).setNegativeButton("取消", null).create().show();
	}

	@Override
	public void getMessage(TranObject msg) {// 重写父类的方法，处理消息
		// TODO Auto-generated method stub
		switch (msg.getType()) {
		case MESSAGE:
			newNum++;
			application.setRecentNum(newNum);// 保存到全局变量
			TextMessage tm = (TextMessage) msg.getObject();
			String message = tm.getMessage();
			ChatMsgEntity entity = new ChatMsgEntity("", MyDate.getDateEN(),
					message, -1, true);// 收到的消息
			messageDB.saveMsg(msg.getFromUser(), entity);// 保存到数据库
			Toast.makeText(FriendListActivity.this,
					"亲！新消息哦 " + msg.getFromUser() + ":" + message, 0).show();// 提示用户
			MediaPlayer.create(this, R.raw.msg).start();// 声音提示
			User user2 = userDB.selectInfo(msg.getFromUser());// 通过id查询对应数据库该好友信息
			RecentChatEntity entity2 = new RecentChatEntity(msg.getFromUser(),
					user2.getImg(), newNum, user2.getName(), MyDate.getDate(),
					message);
			application.getmRecentAdapter().remove(entity2);// 先移除该对象，目的是添加到首部
			application.getmRecentList().addFirst(entity2);// 再添加到首部
			application.getmRecentAdapter().notifyDataSetChanged();
			break;
		case SEEKINFO:
			Log.e("SeekInfo", "ReceiveSeekList");
			List<SeekInfoEntity> seekInfoList1 = application.getSeekInfoList();
			List<SeekInfoEntity> list = (List<SeekInfoEntity>) msg.getObject();
			if(!seekInfoList1.isEmpty()){
				seekInfoList1.clear();	
			}
			seekInfoList1.addAll(list);
			application.getSeekInfoAdapter().notifyDataSetChanged();
			break;
		case PUBLISHINFO:
			Log.e("PushlishInfo", "ReceiveSeekInfo");
			SeekInfoEntity seekinfo = (SeekInfoEntity)msg.getObject();
			application.getSeekInfoList().add(seekinfo);
			application.getSeekInfoAdapter().notifyDataSetChanged();
			break;
		case LOGIN:
			User loginUser = (User) msg.getObject();
			Toast.makeText(FriendListActivity.this,
					"亲！" + loginUser.getId() + "上线了哦", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			break;
		case LOGOUT:
			User logoutUser = (User) msg.getObject();
			Toast.makeText(FriendListActivity.this,
					"亲！" + logoutUser.getId() + "下线了哦", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {// 捕获返回按键事件，进入后台运行
		// TODO Auto-generated method stub
		// 发送广播，通知服务，已进入后台运行
		Intent i = new Intent();
		i.setAction(Constants.BACKKEY_ACTION);
		sendBroadcast(i);

		util.setIsStart(true);// 设置后台运行标志，正在运行
		finish();// 再结束自己
	}

	// ViewPager页面切换监听
	public class MyOnPageChangeListener implements OnPageChangeListener {

		float one = offset;// 页卡1 -> 页卡2 偏移量

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Animation animation = null;
			animation = new TranslateAnimation(currentIndex*one, arg0*one, 0, 0);
			imageTab.get(currentIndex).setImageDrawable(drawableTab.get(currentIndex*2)); // 恢复ImageView 未选中状态图标
			imageTab.get(arg0).setImageDrawable(drawableTab.get(arg0*2+1)); // 设置当前ImageView选中图标
			title.setText(titleTab.get(arg0));
			currentIndex = arg0;// 动画结束后，改变当前图片位置
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * 好友列表下拉刷新监听与实现，异步任务
	 * 
	 * @author way
	 * 
	 */
	public class MyRefreshListener implements MyListView.OnRefreshListener {

		@Override
		public void onRefresh() {
			new AsyncTask<Void, Void, Void>() {
				List<User> list;

				@Override
				protected Void doInBackground(Void... params) {
					// 从服务器重新获取好友列表
					if (application.isClientStart()) {
						ClientOutputThread out = application.getClient()
								.getClientOutputThread();
						TranObject o = new TranObject(TranObjectType.REFRESH);
						o.setFromUser(Integer.parseInt(util.getId()));
						out.setMsg(o);
						// 为了及时收到服务器发过来的消息，我这里直接通过监听收消息线程，获取好友列表，就不通过接收广播了
						ClientInputThread in = application.getClient()
								.getClientInputThread();
						in.setMessageListener(new MessageListener() {

							@Override
							public void Message(TranObject msg) {
								// TODO Auto-generated method stub
								if (msg != null
										&& msg.getType() == TranObjectType.REFRESH) {
									list = (List<User>) msg.getObject();
									if (list.size() > 0) {
										// System.out.println("Friend:" + list);
										initListViewData(list);
										myExAdapter.updata(group);
										userDB.updateUser(list);// 保存到数据库
									}
								}
							}
						});
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					myExAdapter.notifyDataSetChanged();
					myListView.onRefreshComplete();
					Toast.makeText(FriendListActivity.this, "刷新成功", 0).show();
				}

			}.execute();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		super.onActivityResult(requestCode, resultCode, data);  
		
		if (resultCode == RESULT_OK) {  
            switch (requestCode) {  
            case TAKE_PHOTO:  
            	Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁  
                intent.setDataAndType(imageUri, "image/*");  
                intent.putExtra("scale", true);  
                //设置宽高比例  
                intent.putExtra("aspectX", 1);  
                intent.putExtra("aspectY", 1);  
                //设置裁剪图片宽高  
                intent.putExtra("outputX", 340);  
                intent.putExtra("outputY", 400);  
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  
                Toast.makeText(FriendListActivity.this, "剪裁图片", Toast.LENGTH_SHORT).show();  
                //广播刷新相册   
                Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);  
                intentBc.setData(imageUri);       
                this.sendBroadcast(intentBc);      
                startActivityForResult(intent, CROP_PHOTO); //设置裁剪参数显示图片至ImageView  
                break;  
            case CROP_PHOTO:  
                try {      
                    //图片解析成Bitmap对象  
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));  
                    Toast.makeText(FriendListActivity.this, imageUri.toString(), Toast.LENGTH_SHORT).show(); 
                    mImgSend.setBackgroundResource(0);
                    mImgSend.setImageBitmap(bitmap); //将剪裁后照片显示出来  
                } catch(FileNotFoundException e) {  
                    e.printStackTrace();  
                }  
                break;  
            default:  
                break;  
            }  
        }  
	}
	
}
