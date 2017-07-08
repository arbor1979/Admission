package com.dandian.admission.service;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.dandian.admission.CampusApplication;
import com.dandian.admission.activity.ChatMsgActivity;
import com.dandian.admission.activity.LoginActivity;
import com.dandian.admission.activity.TabHostActivity;
import com.dandian.admission.base.Constants;
import com.dandian.admission.db.DatabaseHelper;
import com.dandian.admission.db.InitData;
import com.dandian.admission.entity.ChatFriend;
import com.dandian.admission.entity.User;
import com.dandian.admission.util.AppUtility;
import com.dandian.admission.util.BaiduPushUtility;
import com.dandian.admission.util.PrefUtility;
import com.dandian.admission.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
/**
 * Push��Ϣ����receiver
 */
public class MyPushMessageReceiver extends PushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();

	AlertDialog.Builder builder;

	//private Dao<ChatMsg,Integer> chatMsgDao;
	private Dao<ChatFriend,Integer> chatFriendDao;
	
	DatabaseHelper database;
	
	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

		// �󶨳ɹ��������Ѱ�flag��������Ч�ļ��ٲ���Ҫ�İ�����
		if (errorCode == 0) {
			BaiduPushUtility.setBind(true);
		}
		
		PrefUtility.put(Constants.PREF_BAIDU_USERID, userId);
		
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		if(checkCode.length()>0 && !TabHostActivity.ifpostuserid && userId!=null)
		{
			InitData initData = new InitData(context, OpenHelperManager.getHelper(context, DatabaseHelper.class), null,"postBaiDuUserId",checkCode);
			initData.postBaiduUserId();
		}
		Log.d(TAG, "--------->baiduuserid:"+userId);
		
		// Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
	}
	
	@Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    }

	@Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

    }

	@Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "֪ͨ��� title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        Intent notificationIntent = new Intent(context, TabHostActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		notificationIntent.putExtra("tab", "2");
		context.startActivity(notificationIntent);
		
        // �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ֪ͨ����ʱ�Զ������������õļ���ֵ
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
    }

	@Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

      
    }
	
	@Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        // ��󶨳ɹ�������δ��flag��
        if (errorCode == 0) {
        	BaiduPushUtility.setBind(false);
        }
        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
        //updateContent(context, responseString);
    }
	@Override
    public void onNotificationArrived(Context context, String title,
            String description, String customContentString) {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        Log.d(TAG, notifyString);
        
		
        // �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ֪ͨ����ʱ�Զ������������õļ���ֵ
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       
    }

	@SuppressLint("NewApi")
	@Override
	public void onMessage(Context context, String message,
            String customContentString) {
		// TODO Auto-generated method stub
		if (AppUtility.isNotEmpty(message)) 
		{
			try {
				Log.d(TAG, "Chatmessage:"+message);
				if(database==null)
					database = OpenHelperManager.getHelper(context, DatabaseHelper.class);
				chatFriendDao = database.getChatFriendDao();
				JSONObject jo = new JSONObject(message);
				Log.d(TAG, "--------------->jo:"+jo.toString());
//					String from_datetime = jo.optString("FROM_DATETIME");
				//String from_timeline = jo.optString("FROM_TIMELINE");
				String from_userid_unique = jo.optString("FROM_USERID_UNIQUE"); //Ψһ�� toid
				String type = jo.optString("type");
				String description = jo.optString("description"); //��Ϣ���� 
				//String from_baidu_userid = jo.optString("FROM_BAIDU_USERID");
				String toid = from_userid_unique;
				String toname = jo.optString("FROM_USERID_NAME");
				String userImage = jo.optString("FROM_USERID_IMAGE");
				String msg_type = "";
				String content = description;
				String msg_id=jo.optString("MSG_ID");
				int unreadCnt = 0;
				//�ж��û��Ƿ��������б���
				chatFriendDao = database.getChatFriendDao();
				String hostid=PrefUtility.get(Constants.PREF_CHECK_HOSTID,"");
				ChatFriend chatFriend = chatFriendDao.queryBuilder().where().eq("toid", from_userid_unique).and().eq("hostid", hostid).queryForFirst();
				if(chatFriend!=null)
					chatFriend.setUnreadCnt(chatFriend.getUnreadCnt()+1);
				InitData initData = new InitData(context, database, null, null,null);
				initData.sendChatToDatabase(type,toid, toname, 0, content, chatFriend,msg_type,userImage,msg_id);
				Intent intentChat = new Intent("ChatInteract");
				context.sendBroadcast(intentChat);
				
				List<ChatFriend> chatFriendList = chatFriendDao.queryBuilder().where().eq("hostid", hostid).query();
				for (ChatFriend item:chatFriendList) { //�������б��У���������������ݣ��������ʱ��
					unreadCnt += item.getUnreadCnt();
				}
				User user=((CampusApplication)context.getApplicationContext()).getLoginUserObj();
				
				if (user==null || AppUtility.isApplicationBroughtToBackground(context) || AppUtility.isLockScreen(context)) {
					//��Ϣ֪ͨ��
					//����NotificationManager
					NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					//����֪ͨ��չ�ֵ�������Ϣ
					
					long when = System.currentTimeMillis();
					
					if(type.equals("img"))
					{
						content="[ͼƬ]";
					}
					
					//Notification notification = new Notification(icon, toname+":"+content, when);
					
					//��������֪ͨ��ʱҪչ�ֵ�������Ϣ
					//Context context = getApplicationContext();
					CharSequence contentText = String.valueOf(unreadCnt)+"��δ����Ϣ";
					
					Intent notificationIntent=null;
					if(user==null)
						notificationIntent = new Intent(context, LoginActivity.class);
					else
					{
						
						notificationIntent = new Intent(context, TabHostActivity.class);
						
					}
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					notificationIntent.putExtra("toid", toid);
					notificationIntent.putExtra("toname", toname);
					notificationIntent.putExtra("userImage", userImage);
					PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);
					
					Notification.Builder builder = new Notification.Builder(context);
					builder.setWhen(when);
			        builder.setAutoCancel(true);
			        builder.setTicker(toname+":"+content);
			        builder.setContentTitle(contentText);               
			        builder.setContentText(toname+":"+content);
			        builder.setSmallIcon(R.drawable.ic_logo1);
			        builder.setContentIntent(contentIntent);
			        builder.setOngoing(false);
			        //builder.setSubText("This is subtext...");   //API level 16
			        builder.setNumber(100);
			        builder.setDefaults(Notification.DEFAULT_SOUND| Notification.DEFAULT_VIBRATE);
			        builder.build();
			        Notification notification = builder.getNotification();
			        mNotificationManager.notify(1, notification);
			        
				}
				else
				{
					if(ChatMsgActivity.isruning && ChatMsgActivity.toid.equals(toid))
					{
						AppUtility.playSounds(R.raw.tw_touch, context);
					}
					else
						AppUtility.playSounds(R.raw.tweet_sent, context);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}