package com.dandian.admission.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.dandian.admission.CampusApplication;
import com.dandian.admission.activity.AlbumFlowActivity;
import com.dandian.admission.api.CampusAPI;
import com.dandian.admission.api.CampusException;
import com.dandian.admission.api.CampusParameters;
import com.dandian.admission.api.RequestListener;
import com.dandian.admission.base.Constants;
import com.dandian.admission.db.DatabaseHelper;
import com.dandian.admission.db.InitData;
import com.dandian.admission.entity.ChatFriend;
import com.dandian.admission.entity.ContactsFriends;
import com.dandian.admission.entity.User;
import com.dandian.admission.service.SchoolService;
import com.dandian.admission.service.SchoolService.MyIBinder;
import com.dandian.admission.util.AppUtility;
import com.dandian.admission.util.BaiduPushUtility;
import com.dandian.admission.util.Base64;
import com.dandian.admission.util.DateHelper;
import com.dandian.admission.util.PrefUtility;
import com.dandian.admission.widget.BottomTabLayout;
import com.dandian.admission.widget.BottomTabLayout.OnCheckedChangeListener;
import com.dandian.admission.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


@SuppressWarnings("deprecation")
public class TabHostActivity extends TabActivity   {
	private String TAG = "TabHostActivity";
	private BottomTabLayout mainTab;
	private TabHost tabHost;
	
	private Intent submitDataIntent,myStatusIntent,messageIntent;
	private Intent albumIntent;
	private Intent schoolIntent;
	

	private Dao<ChatFriend,Integer> chatFriendDao;
	private List<ChatFriend> chatFriendList;
	
	private final static String TAB_TAG_MYSELF = "tab_tag_myself";
	private final static String TAB_TAG_SCHOOL = "tab_tag_school";
	private final static String TAB_TAG_FINISH = "tab_tag_finish";
	private final static String TAB_TAG_MSG = "tab_tag_msg";
	private final static String TAB_TAG_ALBUM = "tab_tag_album";
	
	
	// public static int currentWeek = 0,selectedWeek = 0,maxWeek =
	// 0;//��ǰ�ܴ�,ѡ���ܴ�,ѡ���ܴ�
	DatabaseHelper database;
	
	private final String ACTION_CHATINTERACT =  "ChatInteract";
	
	public static SchoolService schoolService;
	public final String STitle = "showmsg_title";
	public final String SMessage = "showmsg_message";
	public final String BAThumbData = "showmsg_thumb_data";
	private User user;
	private boolean isIntoBack;
	public static boolean ifpostuserid=false;
	public static DisplayImageOptions headOptions;
	private boolean isBind = false;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				AppUtility.showErrorToast(TabHostActivity.this, msg.obj.toString());
				break;
			
			case 1:
				String result = msg.obj.toString();
				String resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result.getBytes("GBK")));
						Log.d(TAG, "----resultStr:"+resultStr);
						
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					JSONObject jo = null;
					try {
						jo = new JSONObject(resultStr);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(jo!=null){
						String tips = jo.optString("���ܸ���");
						String downLoadPath = jo.optString("���ص�ַ");
						String newVer=jo.optString("���°汾��");
						if(AppUtility.isNotEmpty(tips)&&AppUtility.isNotEmpty(downLoadPath)){
							showUpdateTips(tips,downLoadPath,newVer);
						}
					}
				}
				break;
			
			default:
				break;
			}
		}
	};
	private ServiceConnection connection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			schoolService = null;
			Log.d(TAG, "Client ->Disconnected the LocalService");
		}

		public void onServiceConnected(ComponentName name, IBinder binder) {
			// ��ȡ���ӵķ������
			schoolService = ((MyIBinder) binder).getService();
			Log.d(TAG, "Client ->Connected the Service");
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// Push: �������ڵ���λ�����ͣ����Դ�֧�ֵ���λ�õ����͵Ŀ���
		//PushManager.enableLbs(getApplicationContext());
		
		isIntoBack=true;
	
		user=((CampusApplication)getApplicationContext()).getLoginUserObjNull();
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		String userNumber=PrefUtility.get(Constants.PREF_CHECK_HOSTID, "");
		if(user==null || checkCode.length()==0 || userNumber.length()==0)
		{
			Intent intent = new Intent(this,
					LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			
			return;
		}
		iniImageLoader();
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                BaiduPushUtility.getMetaValue(this, "api_key"));
		/*
		String contentText = getIntent().getStringExtra("contentText");
		if (AppUtility.isNotEmpty(contentText)) {
			showDialog(contentText);
		}
		*/
		setContentView(R.layout.activity_tabhost);
		mainTab = (BottomTabLayout) findViewById(R.id.bottom_tab_layout);
		mainTab.setOnCheckedChangeListener(changeListener);
		
		try {
			chatFriendDao = getHelper().getChatFriendDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		prepareIntent();
		setupIntent();
		
		Intent intent = new Intent(TabHostActivity.this,SchoolService.class);
		isBind=bindService(intent, connection, Context.BIND_AUTO_CREATE);
		
		showUnreadCnt();
		
		//�汾���
		versionDetection();
			
		//regToWx(); // ע��΢��
		registerBoradcastReceiver();
		
		String toTag = getIntent().getStringExtra("tab");
		if(toTag==null)
			findView();
		else if(toTag.equals("1"))
		{
			tabHost.setCurrentTabByTag(TAB_TAG_MYSELF);
			View nearBtn = mainTab.findViewById(R.id.bottom_tab_myself);
			nearBtn.setSelected(true);
			
		}
		else if(toTag.equals("2"))
		{
			tabHost.setCurrentTabByTag(TAB_TAG_SCHOOL);
			View nearBtn = mainTab.findViewById(R.id.bottom_tab_school);
			nearBtn.setSelected(true);
			
		}
		
		
		Log.d(TAG,"��������:onCreate");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		showUnreadCnt();
		if(isIntoBack)
		{
			isIntoBack=false;
			//getNetLocation();
		}
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	
		if(AppUtility.isApplicationBroughtToBackground(this))
			isIntoBack=true;
		Log.d(TAG,"��������:onStop");
	}

	/**
	 * ׼��tab������Intent
	 */
	private void prepareIntent() {
		myStatusIntent = new Intent(this, MyStatusActivity.class);
		submitDataIntent = new Intent(this, SubmitDataActivity.class);
		
		// summaryIntent = new Intent(this, SummaryActivity.class);
		schoolIntent = new Intent(this, TabSchoolActivity.class);
		messageIntent = new Intent(this, ChatFriendActivity.class);
		albumIntent = new Intent(this, AlbumFlowActivity.class);
	}

	private void setupIntent() {
		FrameLayout bottom_tab_finish=(FrameLayout)findViewById(R.id.bottom_tab_finish);
		if(user.getUserType().equals("��ʦ"))
			bottom_tab_finish.setVisibility(View.GONE);
		this.tabHost = getTabHost();
		TabHost localTabHost = this.tabHost;
		localTabHost.addTab(buildTabSpec(TAB_TAG_MYSELF, R.string.mystatus,
				R.drawable.ic_launcher, myStatusIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_SCHOOL, R.string.school,
				R.drawable.ic_launcher, schoolIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_FINISH,
				R.string.curriculum, R.drawable.ic_launcher,submitDataIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_MSG,
				R.string.message, R.drawable.ic_launcher,messageIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_ALBUM, R.string.album,
		 R.drawable.ic_launcher, albumIntent));
		
		
	}

	/**
	 * ����TabHost��Tabҳ
	 * 
	 * @param tag
	 *            ���
	 * @param resLabel
	 *            ��ǩ
	 * @param resIcon
	 *            ͼ��
	 * @param content
	 *            ��tabչʾ������
	 * @return һ��tab
	 */
	private TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.tabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	// ����Ĭ��ѡ����
	private void findView() {
		View nearBtn = mainTab.findViewById(R.id.bottom_tab_myself);
		nearBtn.setSelected(true);
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		@Override
		public void OnCheckedChange(View checkview) {
			switch (checkview.getId()) {
			
			case R.id.bottom_tab_myself:
				tabHost.setCurrentTabByTag(TAB_TAG_MYSELF);
			
				break;
			case R.id.bottom_tab_school:
				tabHost.setCurrentTabByTag(TAB_TAG_SCHOOL);
			
				break;
			case R.id.bottom_tab_finish:
				tabHost.setCurrentTabByTag(TAB_TAG_FINISH);
				
				break;
			/*
			 * case R.id.bottom_tab_summary:
			 * tabHost.setCurrentTabByTag(TAB_TAG_SUMMARY);
			 * SummaryActivity.layout_menu.setOnClickListener(new
			 * MenuListener()); break;
			 */
			case R.id.bottom_tab_msg:
				tabHost.setCurrentTabByTag(TAB_TAG_MSG);
				
				break;
			case R.id.bottom_tab_album:
				tabHost.setCurrentTabByTag(TAB_TAG_ALBUM);
				
				break;
			
			
			}
			
		}

		@Override
		public void OnCheckedClick(View checkview) {

		}
	};


	/**
	 * ��������:��ʾͷ���ͼ
	 * 
	 * @author shengguo 2014-5-9 ����3:04:49
	 * 
	 * @param imagePath
	 */
	/*
	private void showImageDialog(String imagePath) {
		View view = getLayoutInflater().inflate(R.layout.view_image, null);
		AQuery aq = new AQuery(view);
		final Dialog dialog = DialogUtility.createLoadingDialog(
				TabHostActivity.this, "show_image_dialog");
		dialog.setContentView(view);
		dialog.setCancelable(true);// ���Ե㷵�ؼ�ȡ��
		dialog.show();
		aq.id(R.id.iv_img).image(imagePath).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	*/

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return database;
	}

	/**
	 * ��������:��ʾ��Ϣ����
	 *
	 * @author shengguo  2014-5-29 ����3:07:35
	 *
	 */
	private void showUnreadCnt() {
		int count = 0;
		try {
			chatFriendList = chatFriendDao.queryBuilder().where().eq("hostid", user.getUserNumber()).query();
			for (ChatFriend chatFriend:chatFriendList) {
				count += chatFriend.getUnreadCnt();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		TextView unreadCnt = (TextView) mainTab.findViewById(R.id.unreadCnt);
		if(count!=0){
			unreadCnt.setText(String.valueOf(count));
			unreadCnt.setVisibility(View.VISIBLE);
		}else{
			unreadCnt.setVisibility(View.INVISIBLE);
		}
		
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "--------------ע���㲥/�رշ���-------------");
		try
		{
			unregisterReceiver(mBroadcastReceiver);
		}
		catch(IllegalArgumentException e)
		{
			
		}
		if(schoolService != null && isBind){
			unbindService(connection);
		}
		Log.d(TAG,"��������:onDestroy");
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_CHATINTERACT);
		
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	/**
	 * ��������:�汾���
	 *
	 * @author shengguo  2014-6-3 ����4:05:05
	 *
	 */
	private void versionDetection() {
		String thisVersion = CampusApplication.getVersion();
		String check=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		long datatime = System.currentTimeMillis();
		String base64Str = null;
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("��ǰ�汾��", thisVersion);
			jsonObj.put("�û�������", check);
			jsonObj.put("DATETIME", datatime);

			base64Str = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		Log.d(TAG, "---------------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.versionDetection(params, new RequestListener() {
			
			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response"+e.getMessage());
				Message msg=new Message();
				msg.what = -1;
				msg.obj= e.getMessage();
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response"+response);
				
				Message msg=new Message();
				msg.what = 1;
				msg.obj= response;
				mHandler.sendMessage(msg);
			}
		});
	}
	
	/**
	 * ��������:ѯ���Ƿ����
	 *
	 * @author shengguo  2014-6-3 ����4:31:55
	 *
	 */
	private void showUpdateTips(String tips,final String downLoadPath,String newVer) {
		View view = LayoutInflater.from(TabHostActivity.this).inflate(
				R.layout.view_textview, null);
		TextView tvTip = (TextView) view.findViewById(R.id.tv_text);
		tvTip.setText(tips);
		AlertDialog dialog_UpdateTips = new AlertDialog.Builder(TabHostActivity.this)
				.setView(view)
				.setTitle(newVer+getString(R.string.newversion))
				.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "-------------downLoadPath:" + downLoadPath);
						schoolService.downLoadUpdate(downLoadPath, 1001);
						dialog.dismiss();
					}
				})
				.setNegativeButton(R.string.search_cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog_UpdateTips.show();
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION_CHATINTERACT)){
				showUnreadCnt();
			}
			
		}
	};
	private void iniImageLoader()
	{
        headOptions =
                new DisplayImageOptions.Builder()
                        .cacheOnDisc(true)//ͼƬ�汾��
                        .cacheInMemory(false)
                        .showImageOnFail(R.drawable.ic_launcher)
                                //.displayer(new FadeInBitmapDisplayer(50))
                        .displayer(new RoundedBitmapDisplayer(45))
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
		//��ʼ��ͼƬ���ؿ�
				DisplayImageOptions defaultOptions =
					        new DisplayImageOptions.Builder()
					            .cacheOnDisc(true)//ͼƬ�汾��
					            .cacheInMemory(false)
					            .showImageOnFail(R.drawable.empty_photo)
					            //.displayer(new FadeInBitmapDisplayer(50))
					           // .decodingOptions(decodingOptions)
					            .bitmapConfig(Bitmap.Config.RGB_565)
					            .imageScaleType(ImageScaleType.EXACTLY ) // default
					            .build();
				
				//DisplayImageOptions defaultOptions = DisplayImageOptions.createSimple();
					    ImageLoaderConfiguration config =
					        new ImageLoaderConfiguration.Builder(getApplicationContext())
					    
					    //.memoryCacheExtraOptions(480, 800) // max width, max height���������ÿ�������ļ�����󳤿�  
					    //.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) 
					    //.threadPriority(Thread.NORM_PRIORITY - 2)  
		                //.denyCacheImageMultipleSizesInMemory()  
		                //.memoryCache(new FIFOLimitedMemoryCache(2 * 1024 * 1024))
		                //.memoryCacheSize(2 * 1024 * 1024)    
		                //.discCacheSize(50 * 1024 * 1024) 
		                .defaultDisplayImageOptions(defaultOptions).build();
					    /*
					            .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
					            .memoryCacheSize(2 * 1024 * 1024)  
					            .memoryCacheSizePercentage(13) // default  
					            .denyCacheImageMultipleSizesInMemory()
					     
					            .defaultDisplayImageOptions(defaultOptions).build();
					      */ 
					    ImageLoader.getInstance().init(config);
	}
	
}