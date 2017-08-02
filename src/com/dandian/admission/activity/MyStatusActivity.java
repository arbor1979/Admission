package com.dandian.admission.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.dandian.admission.entity.User;
import com.dandian.admission.util.AppUtility;
import com.dandian.admission.util.Base64;
import com.dandian.admission.util.DateHelper;
import com.dandian.admission.util.DialogUtility;
import com.dandian.admission.util.PrefUtility;
import com.dandian.admission.util.TimeUtility;
import com.dandian.admission.R;



public class MyStatusActivity extends Activity {
	public static final int REQUEST_CODE_TAKE_PICTURE = 2;// //����ͼƬ�����ı�־
	public static final int REQUEST_CODE_TAKE_CAMERA = 1;// //�������ղ����ı�־
	private final static int SCANNIN_GREQUEST_CODE = 1;
	
	AQuery aq;
	DatabaseHelper database;
	MyAdapter adapter;

	private JSONObject userObject;
	
	private List<String> groupkey=new ArrayList<String>(); 
	private List<String> aList = new ArrayList<String>();  
	private JSONObject completeResult; 
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout,ll_baodaoinput;
	private User user;
	private Button bt_changepwd,bt_notice_confirm;
	
	private EditText et_shenfenzheng;
	private ProgressDialog mypDialog;
	private String ID;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);
		TextView title = (TextView) findViewById(R.id.tv_title);
		bt_changepwd=(Button)findViewById(R.id.bt_changepwd);
		ll_baodaoinput=(LinearLayout)findViewById(R.id.ll_baodaoinput);
		
		bt_notice_confirm=(Button)findViewById(R.id.bt_notice_confirm);
		bt_changepwd.setVisibility(View.GONE);
		bt_notice_confirm.setVisibility(View.GONE);
		title.setVisibility(View.VISIBLE);
		title.setText(getString(R.string.mystatus));
		
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		contentLayout = (LinearLayout) findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) findViewById(R.id.empty_error);
		
		LinearLayout relogin = (LinearLayout) findViewById(R.id.layout_back);
		relogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((CampusApplication)getApplicationContext()).reLogin();
			}
			
		});
		bt_changepwd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyStatusActivity.this,ChangePwdActivity.class);
				startActivity(intent);
			}
			
		});
		bt_notice_confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				confirmLetterOfAdmiss();
			}
			
		});
		
		Button bt_search=(Button)findViewById(R.id.bt_search);
		bt_search.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				searchStudent();
			}
			
		});
		Button mButton = (Button) findViewById(R.id.button3);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyStatusActivity.this, SelectStudentActivity.class);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});
		et_shenfenzheng= (EditText) findViewById(R.id.et_shenfenzheng);
		aq = new AQuery(this);
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		if(user.getUserType()==null || user.getUserType().length()==0)
		{
			((CampusApplication)getApplicationContext()).reLogin();
		}
		getStatus();
	}
	
	private void showProgress(boolean progress) {
		if (progress) {
			loadingLayout.setVisibility(View.VISIBLE);
			contentLayout.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
			failedLayout.setVisibility(View.GONE);
		}
	}
	private void getStatus() {
		showProgress(true);
		
		String dataResult = "";
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("action", "status");
			jsonObj.put("���", user.getId());
			jsonObj.put("�û�����", user.getUserType());
			jsonObj.put("language", language);
			dataResult = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, dataResult);
		CampusAPI.loginCheck(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {

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
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showProgress(false);
				showProgress1(false);
				AppUtility.showErrorToast(MyStatusActivity.this,
						msg.obj.toString());
				break;
			case 0:
				showProgress(false);
				String result = msg.obj.toString();
				try {
					result = new String(Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				try 
				{
					JSONObject jo = new JSONObject(result);
					String loginStatus = jo.optString("���");
					
					if (!loginStatus.equals("�ɹ�")) {
						AppUtility.showToastMsg(MyStatusActivity.this, loginStatus,1);
					} else 
					{
						
						PrefUtility.put(Constants.PREF_INIT_DATA_STR, jo.optJSONObject("�û���Ϣ").toString());
						userObject=jo.optJSONObject("�û���Ϣ");
						String luqustr=jo.optString("������");
						completeResult=jo.optJSONObject("������");
						groupkey.clear();
						aList.clear();
						String[] headstr=luqustr.split(",");
						for(int i=0;i<headstr.length;i++)
						{
							groupkey.add(headstr[i]);
							aList.add(headstr[i]);
							String[] fields=jo.optString(headstr[i]).split(",");
							for(int j=0;j<fields.length;j++)
							{
								aList.add(fields[j]);
								
							}
						}
						initContent();
					}
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				break;
			case 1:
				showProgress1(false);
				result = msg.obj.toString();
				try {
					result = new String(Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				try 
				{
					JSONObject jo = new JSONObject(result);
					String loginStatus = jo.optString("���");
					
					if (!loginStatus.equals("�ɹ�")) {
						AppUtility.showToastMsg(MyStatusActivity.this, loginStatus,1);
					} else 
					{
						final JSONArray userArray=jo.optJSONArray("�û�����");
						ID="";
						if(userArray.length()>1)
						{
							String [] userStr=new String[userArray.length()];
							for(int i=0;i<userArray.length();i++)
							{
								JSONObject item=userArray.getJSONObject(i);
								userStr[i]=item.optString("���֤��")+" "+item.optString("����");
							}
							new AlertDialog.Builder(MyStatusActivity.this).setTitle("��ѡ��һ��ѧ��")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setSingleChoiceItems(userStr, 0, new DialogInterface.OnClickListener() 
							{ 
								public void onClick(DialogInterface dialog, int which) 
								{ 
									try {
										ID=userArray.getJSONObject(which).getString("���");
										Intent intent=new Intent(MyStatusActivity.this,BaodaoHandleActivity.class);
										intent.putExtra("ID", ID);
										startActivity(intent);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									dialog.dismiss();
								} 
							} 
							).setNegativeButton("ȡ��", null) .show();
						}
						else
						{
							ID=userArray.getJSONObject(0).getString("���");
							Intent intent=new Intent(MyStatusActivity.this,BaodaoHandleActivity.class);
							intent.putExtra("ID", ID);
							startActivity(intent);
						}
						
						
					}
				} catch (Exception e) {
				
					e.printStackTrace();
				}
			
				break;
			case 2:
				showProgress1(false);
				result = msg.obj.toString();
				try {
					result = new String(Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				try 
				{
					JSONObject jo = new JSONObject(result);
					String loginStatus = jo.optString("���");
					
					if (!loginStatus.equals("�ɹ�")) {
						AppUtility.showToastMsg(MyStatusActivity.this, loginStatus,1);
					} else 
					{
						userObject.put("Ԥ����",jo.optString("Ԥ����"));
						userObject.put("ȷ�����",jo.optString("ȷ�����"));
						initContent();
						if(jo.optString("Ԥ����").equals("��"))
						{
							Intent intent = new Intent(MyStatusActivity.this, SchoolDetailActivity.class);
							intent.putExtra("templateName", "�����ʾ�");
							intent.putExtra("interfaceName", jo.optString("�ӿڵ�ַ"));
							intent.putExtra("title", jo.optString("����"));
							intent.putExtra("display", jo.optString("����"));
							intent.putExtra("autoClose", "��");
							intent.putExtra("status", "������");
							startActivity(intent);
						}
						
					}
				} catch (Exception e) {
				
					e.printStackTrace();
				}
				break;
			}
		}
	};
	private void initContent() {
		

		ImageOptions options = new ImageOptions();
		//options.round=40;
		options.memCache=true;
		options.fileCache=true;
		
		String userImage=user.getUserImage();
		if(userImage!=null && userImage.length()>0)
		{
			aq.id(R.id.iv_pic).image(userImage,options);
		}
		
		
		aq.id(R.id.tv_name).text(user.getName());
		aq.id(R.id.user_type).text(user.getsStatus());
		if(user.getUserType().equals("ѧ��"))
		{
			bt_changepwd.setVisibility(View.VISIBLE);
			bt_notice_confirm.setVisibility(View.VISIBLE);
			if(userObject.optString("Ԥ����").equals("��"))
			{
				bt_notice_confirm.setTextColor(getResources().getColor(R.color.green_dark));
				bt_notice_confirm.setText("��ȷ�����");
			}
			else if(userObject.optString("Ԥ����").equals("��"))
			{
				bt_notice_confirm.setTextColor(Color.RED);
				bt_notice_confirm.setText("�ѷ������");
			}
			else
				bt_notice_confirm.setTextColor(Color.BLACK);
				
			String mPassword=PrefUtility.get(Constants.PREF_LOGIN_PASS,"");
			String tipDate=PrefUtility.get(Constants.PREF_CHANGEPWD_TIP_DATE, "");
			String shenfenzheng=userObject.optString("Ĭ�Ͽ���");
			boolean bneedTip=false;
			if(tipDate.length()==0 || !tipDate.equals(DateHelper.getToday()))
				bneedTip=true;
			if(bneedTip && (mPassword.length()==0 || shenfenzheng.equals(mPassword)))
			{
				new AlertDialog.Builder(this)  
				 .setIcon(android.R.drawable.ic_menu_info_details)
				 .setTitle("����Ϊ��ʼ���룬�Ƿ������޸�����?")
				 .setCancelable(false)
			     //.setMessage(R.string.dialog_pleaseconfirmyournotice)//������ʾ������  
			     .setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {//���ȷ����ť  
			         @Override  
			         public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  
			  
			        	 bt_changepwd.performClick();
			         }  
			  
			     }).setNegativeButton("ȡ��",null).show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի��� 
				PrefUtility.put(Constants.PREF_CHANGEPWD_TIP_DATE, DateHelper.getToday());
			}
		}
		else
		{
			if(!user.getsStatus().equals("��վԱ"))
				ll_baodaoinput.setVisibility(View.VISIBLE);
		}
		aq.id(R.id.iv_pic).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtility.showImageDialog(MyStatusActivity.this,user.getUserImage());
				
			}
			
		});
		
		
		adapter=new MyAdapter(this);
		aq.id(R.id.listView1).adapter(adapter);
		
	}
	private void confirmLetterOfAdmiss()
	{
		if(userObject.optString("Ԥ����").length()>0 && userObject.optString("�鳤���ͨ��").equals("�����"))
		{
			AlertDialog.Builder inputDialog = 
			        new AlertDialog.Builder(this);
			    inputDialog.setTitle("��ʾ")
			    .setMessage("������������ˣ��޷����޸Ĵ���")
			    .setIcon(android.R.drawable.ic_dialog_info);
			    inputDialog.setPositiveButton("ȷ��",null)
			    .show();
			return;
		}
			
	    AlertDialog.Builder inputDialog = 
	        new AlertDialog.Builder(this);
	    inputDialog.setTitle(R.string.enter_confirm)
	    .setMessage(R.string.dialog_pleaseinputyourletterno)
	    .setIcon(android.R.drawable.ic_dialog_info);
	    inputDialog.setPositiveButton(R.string.enter, 
	        new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	if(!userObject.optString("Ԥ����").equals("��"))
	        		letterConfirm("��");
	        }
	    }).setNegativeButton(R.string.giveup,
	    	new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	if(!userObject.optString("Ԥ����").equals("��"))
	        		letterConfirm("��");
	        }
	    })
	    .show();
	    
	}
	private void letterConfirm(String letterNo)
	{
		String dataResult = "";
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("action", "enterConfirm");
			jsonObj.put("���", user.getId());
			jsonObj.put("���ȷ��", letterNo);
			jsonObj.put("language", language);
			jsonObj.put("client", "Android");
			dataResult = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, dataResult);
		CampusAPI.loginCheck(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {

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
	public class MyAdapter extends BaseAdapter{
		 
        private LayoutInflater mInflater;
        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
          
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return aList.size();
        }
 
        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
        	return aList.get(arg0); 
        }
 
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
        @Override  
        public boolean isEnabled(int position) {  
            // TODO Auto-generated method stub  
             if(groupkey.contains(getItem(position))){  
                 return false;  
             }  
             return super.isEnabled(position);  
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
             
        	View view=convertView;  
        	String key=aList.get(position);
            if(groupkey.contains(getItem(position))){  
                view=mInflater.inflate(R.layout.addexam_list_item_tag, null);  
                TextView text=(TextView) view.findViewById(R.id.addexam_list_item_text); 
                text.setText(key);
            }else{  
                view=mInflater.inflate(R.layout.list_left_right_image, null);  
                TextView title=(TextView)view.findViewById(R.id.left_title);
                TextView detail=(TextView)view.findViewById(R.id.right_detail);
                ImageView iv_complete=(ImageView)view.findViewById(R.id.iv_complete);
                title.setText(key);
                detail.setText(userObject.optString(key));
                Button changeBtn=(Button)view.findViewById(R.id.bt_changeNumber);
                if(key.equals("֪ͨ��EMS") && userObject.optString(key)!=null && userObject.optString(key).length()>0 && !userObject.optString(key).equals("δ����"))
                {
                	changeBtn.setText(R.string.trace);
                	changeBtn.setVisibility(View.VISIBLE);
                	changeBtn.setTag(userObject.optString(key));
                	changeBtn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent=new Intent(MyStatusActivity.this,EmsTraceActivity.class);
							intent.putExtra("emsno", v.getTag().toString());
							startActivity(intent);
						}
                		
                	});
                }
                else if(key.equals("���վ����") && userObject.optString(key)!=null && userObject.optInt(key)>0)
                {
                	changeBtn.setText("�鿴");
                	changeBtn.setVisibility(View.VISIBLE);
                	changeBtn.setTag(userObject.optString(key));
                	changeBtn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent=new Intent(MyStatusActivity.this,SchoolActivity.class);
							intent.putExtra("templateName", "�ɼ�");
							intent.putExtra("interfaceName", "XUESHENG-CHENGJI-JieZhan.php");
							intent.putExtra("title", "���վ����");
							intent.putExtra("display", "���վ����");
							startActivity(intent);
						}
                		
                	});
                }
                else
                	changeBtn.setVisibility(View.GONE);
                if(completeResult!=null && completeResult.optString(key)!=null && completeResult.optString(key).length()>0)
                {
                	iv_complete.setVisibility(View.VISIBLE);
                	if(completeResult.optInt(key)==1)
                		iv_complete.setImageResource(R.drawable.complete);
                	else
                		iv_complete.setImageResource(R.drawable.uncomplete);
                }
                else
                	iv_complete.setVisibility(View.GONE);
                	
            } 
            
            return view;
        }
       
         
    }
	
	private void searchStudent()
	{
		if(et_shenfenzheng.getText().toString().trim().length()<2)
		{
			et_shenfenzheng.requestFocus();
			et_shenfenzheng.setError("���������������ַ�");
			return;
		}
		et_shenfenzheng.setError(null);
		showProgress1(true);
		String dataResult = "";
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("action", "search");
			jsonObj.put("userid", user.getUsername());
			jsonObj.put("��ѯ����", et_shenfenzheng.getText().toString().trim());
			jsonObj.put("language", language);
			dataResult = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, dataResult);
		CampusAPI.baodaoHandle(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {

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
	private void showProgress1(final boolean show) {
		
		if(show)
		{
		if(mypDialog==null)
			mypDialog=new ProgressDialog(this);
        //ʵ����
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //����ProgressDialog ����
        mypDialog.setMessage("��ѯ��..");
        //����ProgressDialog ��ʾ��Ϣ
        //����ProgressDialog ��һ��Button
        mypDialog.setIndeterminate(false);
        //����ProgressDialog �Ľ������Ƿ���ȷ
        mypDialog.setCancelable(false);
        //����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
        mypDialog.show();
		}
		else
		{
			if(mypDialog!=null)
				mypDialog.cancel();
		}
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				//��ʾɨ�赽������
				if(bundle!=null)
				{
					String result = bundle.getString("result");
					if(result.length()>1)
					{
						Intent intent=new Intent(MyStatusActivity.this,BaodaoHandleActivity.class);
						intent.putExtra("ID", result);
						startActivity(intent);
						
					}
				}
				//��ʾ
				//mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
			}
			break;
		}
    }
	
	
}
