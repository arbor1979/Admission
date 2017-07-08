package com.dandian.admission.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.dandian.admission.CampusApplication;
import com.dandian.admission.api.CampusAPI;
import com.dandian.admission.api.CampusException;
import com.dandian.admission.api.CampusParameters;
import com.dandian.admission.api.RequestListener;
import com.dandian.admission.base.Constants;
import com.dandian.admission.db.DatabaseHelper;
import com.dandian.admission.entity.AlbumImageInfo;
import com.dandian.admission.entity.ContactsMember;
import com.dandian.admission.entity.User;
import com.dandian.admission.util.AppUtility;
import com.dandian.admission.util.Base64;
import com.dandian.admission.util.DialogUtility;
import com.dandian.admission.util.FileUtility;
import com.dandian.admission.util.HttpMultipartPost;
import com.dandian.admission.util.ImageUtility;
import com.dandian.admission.util.PrefUtility;
import com.dandian.admission.util.TimeUtility;
import com.dandian.admission.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShowPersonInfo extends Activity {

	public static final int REQUEST_CODE_TAKE_PICTURE = 2;// //����ͼƬ�����ı�־
	public static final int REQUEST_CODE_TAKE_CAMERA = 1;// //�������ղ����ı�־
	private String picturePath;
	private String studentId;
	private String userImage;
	private int picCount=0;
	private ArrayList<AlbumImageInfo> picList=new ArrayList<AlbumImageInfo>();
	AQuery aq;
	ContactsMember memberInfo;
	DatabaseHelper database;
	List<Map<String, Object>> list;
	User user;
	MyAdapter adapter;
	Button changeheader;
	private Dao<User, Integer> userDao;
	ImageView headImgView;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_info);
        headImgView=(ImageView)findViewById(R.id.iv_pic);
		
		studentId = getIntent().getStringExtra("studentId");
		userImage = getIntent().getStringExtra("userImage");
		try {
			userDao = getHelper().getUserDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		/*
		if(studentId.equals(user.getUserNumber()))
		{
			changeheader= (Button) findViewById(R.id.bt_changeHeader);
			changeheader.setVisibility(View.VISIBLE);
			changeheader.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					showGetPictureDiaLog();
				}
				
			});
		}
		*/
		aq = new AQuery(this);
		query();
		getPrivateAlbum();
		initContent();
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		}
		return database;
	}
	private void query() {
	
		if(memberInfo==null)
		{
			memberInfo=new ContactsMember();
			if(studentId.equals(user.getUserNumber()))
			{
				
				memberInfo.setName(user.getName());
				memberInfo.setGender(user.getGender());
				memberInfo.setStudentID(studentId.split("_")[2]);
				if(user.getUserType().equals("��ʦ"))
				{
					memberInfo.setClassName(user.getDepartment());
					memberInfo.setChargeClass(user.getWithClass());
					memberInfo.setChargeKeCheng(user.getWithCourse());
					memberInfo.setStuPhone(user.getPhone());
				}
				else
				{
					memberInfo.setClassName(user.getsClass());
					memberInfo.setStuPhone(user.getsPhone());
				}
				
				memberInfo.setLoginTime(user.getLoginTime());
				memberInfo.setUserType(user.getUserType());
				memberInfo.setSchoolName(user.getCompanyName());
				memberInfo.setUserImage(user.getUserImage());
			}
			else
			{
				//memberInfo=((CampusApplication)getApplicationContext()).getLinkManDic().get(studentId);
				AppUtility.showToastMsg(this,"����ˢ�¸�������");
				memberInfo.setUserType("");
			}
		}
		else
			memberInfo.setSchoolName(user.getCompanyName());
	}
	
	private void initContent() {
		
		
		if(userImage==null || userImage.equals("null"))
			userImage=memberInfo.getUserImage();

        ImageOptions options = new ImageOptions();
        options.memCache=false;
        options.fileCache=true;

		options.targetWidth=150;
		options.round = 75;
		aq.id(R.id.iv_pic).image(userImage,options);
        /*
		final Bitmap bmold=aq.getCachedImage(userImage);
		if(flag && bmold!=null)
		{


		}
		else
		{
			BitmapAjaxCallback cb = new BitmapAjaxCallback(){
				 public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
		                if(bm==null) {
		                	bm=bmold;
		                } 
		                super.callback(url, iv, bm,status);
	                    Log.d("ShowPersonInfo", "status:"+status.getCode()+"url:"+url);
		            }       
	                
	        };
	        cb.url(userImage).memCache(flag).fileCache(flag).targetWidth(800).fallback(0).animation(0).round(400).timeout(30000);
	        aq.id(R.id.iv_pic).image(cb);
		}
		*/
        //ImageLoader.getInstance().displayImage(userImage,headImgView,TabHostActivity.headOptions);

		//aq.id(R.id.iv_pic).image(userImage,flag,flag,800,R.drawable.ic_launcher);
		aq.id(R.id.tv_name).text(memberInfo.getName());
		aq.id(R.id.user_type).text(memberInfo.getUserType());
		aq.id(R.id.setting_tv_title).text("�û���Ϣ");
		aq.id(R.id.back).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		aq.id(R.id.iv_pic).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtility.showImageDialog(ShowPersonInfo.this,userImage);
				
			}
			
		});
		String userType =user.getUserType();
		list = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "�Ա�");
		map.put("info", memberInfo.getGender());
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", "��λ");
		map.put("info", memberInfo.getSchoolName());
		list.add(map);
		
		if(memberInfo.getUserType().equals("��ʦ"))
		{
			
			map = new HashMap<String, Object>();
			map.put("title", "����");
			map.put("info", memberInfo.getClassName());
			list.add(map);

		}
		else
		{

			map = new HashMap<String, Object>();
			map.put("title", "Ժϵ");
			map.put("info", memberInfo.getDormitory());
			list.add(map);
			map = new HashMap<String, Object>();
			map.put("title", "ѧ��״̬");
			map.put("info", memberInfo.getStuStatus());
			list.add(map);
			
		}
		
		/*
		SimpleAdapter adapter = new SimpleAdapter(this,list,R.layout.list_left_right,
				new String[]{"title","info"},
				new int[]{R.id.left_title,R.id.right_detail});
		*/
		adapter=new MyAdapter(this);
		aq.id(R.id.listView1).adapter(adapter);
		
	}
	
	public class MyAdapter extends BaseAdapter{
		 
        private LayoutInflater mInflater;
        private Context context;
        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
           this.context=context;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }
 
        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }
 
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
             
            ViewHolder holder = null;
            
            if (convertView == null) {
                 
                holder=new ViewHolder();  
                 
                convertView = mInflater.inflate(R.layout.list_left_right, null);
                holder.title = (TextView)convertView.findViewById(R.id.left_title);
                holder.info = (TextView)convertView.findViewById(R.id.right_detail);
                holder.private_album = (LinearLayout)convertView.findViewById(R.id.private_album);
                holder.imageViews=new ImageView[4];
                holder.imageViews[0]= (ImageView)convertView.findViewById(R.id.theImage);
                holder.imageViews[1] = (ImageView)convertView.findViewById(R.id.imageView2);
                holder.imageViews[2] = (ImageView)convertView.findViewById(R.id.imageView3);
                holder.imageViews[3] = (ImageView)convertView.findViewById(R.id.imageView4);
                holder.bt_changeNumber= (Button)convertView.findViewById(R.id.bt_changeNumber);
                convertView.setTag(holder);
                 
            }else {
                 
                holder = (ViewHolder)convertView.getTag();
            }
            
            holder.title.setText((String)list.get(position).get("title"));
            holder.info.setText((String)list.get(position).get("info"));
            /*
            if(holder.title.getText().equals("�ֻ�") && studentId.equals(user.getUserNumber()))
            {
            	holder.bt_changeNumber.setVisibility(View.VISIBLE);
            	holder.bt_changeNumber.setOnClickListener(new OnClickListener(){

        			@Override
        			public void onClick(View v) {
        				final EditText et=new EditText(ShowPersonInfo.this);
        				et.setInputType(InputType.TYPE_CLASS_PHONE);
        				new AlertDialog.Builder(ShowPersonInfo.this).setTitle("�������µ���ϵ��ʽ").setView(et)
        				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener()
        				{

        					@Override
        					public void onClick(DialogInterface dialog, int which) {
        						// TODO Auto-generated method stub
        						String newphone=et.getText().toString().trim();
        						if(et.length()!=11)
        						{
        							AppUtility.showToastMsg(ShowPersonInfo.this, "Ҫ��11λ�ֻ����룡");
        						}
        						else
        							updateUserPhone(newphone);
        					}
        					
        				}).setNegativeButton("ȡ��", null).show();
        				TimeUtility.popSoftKeyBoard(ShowPersonInfo.this,et);
        			}
        			
        		});
            }
            else
            */
            	holder.bt_changeNumber.setVisibility(View.GONE);
            	
            if(holder.title.getText().equals("�������"))
            {
            	if(picCount==0)
            	{
            		holder.info.setText("��ʱû���ϴ���Ƭ");
            		holder.private_album.setVisibility(View.GONE);
            	}
            	else
            	{
            		holder.info.setText("���ϴ���"+picCount+"����Ƭ");
            		
            		holder.private_album.setVisibility(View.VISIBLE);
            		AQuery aq = new AQuery(convertView);
            		for(int i=0;i<picList.size();i++)
            		{
            			AlbumImageInfo image=picList.get(i);
            			//aq.id(holder.imageViews[i]).image(image.getUrl(),false,true,120,R.drawable.empty_photo);
                        ImageLoader.getInstance().displayImage(image.getUrl(),holder.imageViews[i]);
            			holder.imageViews[i].setOnClickListener(new View.OnClickListener() {
                            
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(context,AlbumPersonalActivity.class);
                                intent.putExtra("hostId", studentId);
                                intent.putExtra("hostName", memberInfo.getName());
                                context.startActivity(intent);
                            }
                        });
            		}
            		
            	}
            }
            else
            	holder.private_album.setVisibility(View.GONE);
            
             
             
            return convertView;
        }
        public final class ViewHolder{
          
            public TextView title;
            public TextView info;
            public LinearLayout private_album;
            public ImageView[] imageViews;
            public Button bt_changeNumber;
        }
         
    }
	private void updateUserPhone(String newphone)
	{
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("action", "������ϵ��ʽ");
			jo.put("�º���", newphone);
			jo.put("�û�������", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, "AlbumPraise.php", new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
				
			}

			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 2;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
	}
	private void getPrivateAlbum() {
		

		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("action", "���������");
			jo.put("hostId", studentId);
			jo.put("�û�������", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, "AlbumPraise.php", new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
				
			}

			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message msg) {
			
			String result = "";
			String resultStr = "";
			switch (msg.what) {
			case -1:// ����ʧ��
				
				AppUtility.showErrorToast(ShowPersonInfo.this,
						msg.obj.toString());
				break;
			case 1:
				
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				try {
					JSONObject jo = new JSONObject(resultStr);
					picCount=jo.optInt("����");
					JSONArray ja=jo.getJSONArray("���");
					if(ja!=null)
					{
						picList.clear();
						for(int i=0;i<ja.length();i++)
						{
							AlbumImageInfo image=new AlbumImageInfo((JSONObject)ja.get(i));
							if(image!=null)
								picList.add(image);
						}
					}
					
						memberInfo=new ContactsMember(jo.getJSONObject("��������"));
						userImage=memberInfo.getUserImage();
						initContent();

						
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				try {
					JSONObject jo = new JSONObject(resultStr);
					if(jo.optString("���").equals("�ɹ�"))
					{
						AppUtility.showToastMsg(ShowPersonInfo.this, "���³ɹ���");
						memberInfo.setStuPhone(jo.optString("�º���"));
						if(user.getUserType().equals("��ʦ"))
							user.setPhone(memberInfo.getStuPhone());
						else
							user.setsPhone(memberInfo.getStuPhone());
						try {
							userDao.update(user);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						initContent();
					}
					else
						AppUtility.showToastMsg(ShowPersonInfo.this, "����ʧ��:"+jo.optString("���"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 5:
				
				Bundle	upLoadbundle = (Bundle) msg.obj;
				result = upLoadbundle.getString("result");
				
				try {
					resultStr = new String(Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				
				try {
					JSONObject jo = new JSONObject(resultStr);
					
					if("OK".equals(jo.optString("STATUS"))){
						DialogUtility.showMsg(ShowPersonInfo.this, "�ϴ��ɹ���");
						userImage=jo.optString("��ͷ��");
						user.setUserImage(userImage);
						userDao.update(user);
						initContent();
						Intent intent = new Intent("ChangeHead");
						intent.putExtra("newhead", userImage);
						sendBroadcast(intent);
						
					}else{
						DialogUtility.showMsg(ShowPersonInfo.this, "�ϴ�ʧ��:"+jo.optString("STATUS"));
					}
				}catch (Exception e) {
					AppUtility.showToastMsg(ShowPersonInfo.this, e.getMessage());
					e.printStackTrace();
				}	
				break;
			}
		}
	};
	private void showGetPictureDiaLog() {
		View view = getLayoutInflater()
				.inflate(R.layout.view_get_picture, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView byCamera = (TextView) view.findViewById(R.id.tv_by_camera);
		TextView byLocation = (TextView) view.findViewById(R.id.tv_by_location);

		final AlertDialog ad = new AlertDialog.Builder(this).setView(view)
				.create();

		Window window = ad.getWindow();
		window.setGravity(Gravity.BOTTOM);// �ڵײ�����
		window.setWindowAnimations(R.style.CustomDialog);
		ad.show();
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
		byCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPictureByCamera();
				ad.dismiss();
			}
		});
		byLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPictureFromLocation();
				ad.dismiss();
			}
		});
	}
	private synchronized void getPictureByCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// ����android�Դ��������
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
			AppUtility.showToastMsg(this, "û�а�װSD�����޷�ʹ���������");
			return;
		}
		picturePath = FileUtility.getRandomSDFileName("jpg");
		
		File mCurrentPhotoFile = new File(picturePath);

		Uri uri = Uri.fromFile(mCurrentPhotoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
	}
	
	public void getPictureFromLocation() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
			/*
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
			*/
			Intent intent; 
			intent = new Intent(Intent.ACTION_PICK, 
			                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
		} else {
			AppUtility.showToastMsg(this, "SD��������");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_TAKE_CAMERA: // ���շ���
			if (resultCode == RESULT_OK) {
				rotateAndCutImage(new File(picturePath));
			}
			break;
		case REQUEST_CODE_TAKE_PICTURE:
			if (data != null) {
				
				//picturePath = data.getStringExtra("filepath");
				//String myImageUrl = data.getDataString();
				//Uri uri = Uri.parse(myImageUrl);
				Uri uri = data.getData();
				String[] pojo  = { MediaStore.Images.Media.DATA };
				CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null,null, null); 
				Cursor cursor = cursorLoader.loadInBackground();
				if(cursor!=null)
				{
					cursor.moveToFirst(); 
					picturePath = cursor.getString(cursor.getColumnIndex(pojo[0]));
				}
				else
				{
					if(uri.toString().startsWith("file://"))
					{
						picturePath=uri.toString().replace("file://", "");
					}
					else
					{
						AppUtility.showErrorToast(this, "��ȡ���ͼƬʧ��");
						return;
					}
				}
			     
				
				String tempPath =FileUtility.getRandomSDFileName("jpg");
				if(FileUtility.copyFile(picturePath,tempPath))
					rotateAndCutImage(new File(tempPath));
				else
					AppUtility.showErrorToast(this, "��SD�������ļ�����");
			}
			break;
		case 3:
			if (resultCode == 200 && data != null) {
				
				String picPath = data.getStringExtra("picPath");
				SubmitUploadFile(picPath);
			}
		default:
			break;
		}
	}
	private void rotateAndCutImage(final File file) {
		if(!file.exists()) return;
		if(AppUtility.formetFileSize(file.length()) > 5242880*2){
			AppUtility.showToastMsg(this, "�Բ������ϴ����ļ�̫���ˣ���ѡ��С��10M���ļ���");
		}else{
			
			ImageUtility.rotatingImageIfNeed(file.getAbsolutePath());
			Intent intent=new Intent(this,CutImageActivity.class);
			intent.putExtra("picPath", file.getAbsolutePath());
			startActivityForResult(intent,3);
		}
	}
	
	public void SubmitUploadFile(String picPath){
		CampusParameters params = new CampusParameters();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// ��ȡ�û�У����
		/*
		params.add("�û�������", checkCode);
		params.add("�γ�����", downloadSubject.getCourseName());
		params.add("��ʦ����", downloadSubject.getUserName());
		params.add("�ļ���", downloadSubject.getFileName());
		*/
		params.add("JiaoYanMa", checkCode);
		params.add("pic", picPath);
		params.add("TuPianLeiBie", "ͷ��");
		HttpMultipartPost post = new HttpMultipartPost(this, params){
			@Override  
		    protected void onPostExecute(String result) {  
				Bundle bundle = new Bundle();
				bundle.putString("result", result);
				Message msg = new Message();
				msg.what = 5;
				msg.obj = bundle; 
				mHandler.sendMessage(msg);	
				this.pd.dismiss();
		    }
		};  
        post.execute();
	}
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("memberInfo",memberInfo);
		savedInstanceState.putString("picturePath", picturePath);
		
		
	}
	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        memberInfo = (ContactsMember) savedInstanceState.getSerializable("memberInfo");
        picturePath=savedInstanceState.getString("picturePath");
    }
}
