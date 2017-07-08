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
import com.dandian.admission.util.DialogUtility;
import com.dandian.admission.util.PrefUtility;
import com.dandian.admission.util.TimeUtility;
import com.dandian.admission.R;



public class MyStatusActivity extends Activity {
	public static final int REQUEST_CODE_TAKE_PICTURE = 2;// //设置图片操作的标志
	public static final int REQUEST_CODE_TAKE_CAMERA = 1;// //设置拍照操作的标志
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
	private TextView tv_already_confirmed;
	private EditText et_shenfenzheng;
	private ProgressDialog mypDialog;
	private String ID;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);
		TextView title = (TextView) findViewById(R.id.tv_title);
		bt_changepwd=(Button)findViewById(R.id.bt_changepwd);
		ll_baodaoinput=(LinearLayout)findViewById(R.id.ll_baodaoinput);
		tv_already_confirmed=(TextView)findViewById(R.id.tv_already_confirmed);
		bt_notice_confirm=(Button)findViewById(R.id.bt_notice_confirm);
		bt_changepwd.setVisibility(View.GONE);
		bt_notice_confirm.setVisibility(View.GONE);
		tv_already_confirmed.setVisibility(View.GONE);
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
				Intent intent = new Intent();
				intent.setClass(MyStatusActivity.this, CaptureActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
			jsonObj.put("编号", user.getId());
			jsonObj.put("用户类型", user.getUserType());
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
					String loginStatus = jo.optString("结果");
					
					if (!loginStatus.equals("成功")) {
						AppUtility.showToastMsg(MyStatusActivity.this, loginStatus,1);
					} else 
					{
						
						PrefUtility.put(Constants.PREF_INIT_DATA_STR, jo.optJSONObject("用户信息").toString());
						userObject=jo.optJSONObject("用户信息");
						String luqustr=jo.optString("表格分组");
						completeResult=jo.optJSONObject("完成情况");
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
					String loginStatus = jo.optString("结果");
					
					if (!loginStatus.equals("成功")) {
						AppUtility.showToastMsg(MyStatusActivity.this, loginStatus,1);
					} else 
					{
						final JSONArray userArray=jo.optJSONArray("用户数组");
						ID="";
						if(userArray.length()>1)
						{
							String [] userStr=new String[userArray.length()];
							for(int i=0;i<userArray.length();i++)
							{
								JSONObject item=userArray.getJSONObject(i);
								userStr[i]=item.optString("身份证号")+" "+item.optString("姓名");
							}
							new AlertDialog.Builder(MyStatusActivity.this).setTitle("请选择一个学生")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setSingleChoiceItems(userStr, 0, new DialogInterface.OnClickListener() 
							{ 
								public void onClick(DialogInterface dialog, int which) 
								{ 
									try {
										ID=userArray.getJSONObject(which).getString("编号");
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
							).setNegativeButton("取消", null) .show();
						}
						else
						{
							ID=userArray.getJSONObject(0).getString("编号");
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
					String loginStatus = jo.optString("结果");
					
					if (!loginStatus.equals("成功")) {
						AppUtility.showToastMsg(MyStatusActivity.this, loginStatus,1);
					} else 
					{
						userObject.put("通知书确认",jo.optString("通知书确认"));
						initContent();
						
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
		if(user.getUserType().equals("学生"))
		{
			bt_changepwd.setVisibility(View.VISIBLE);
			if(userObject.optString("通知书确认").length()==0)
			{
				bt_notice_confirm.setVisibility(View.VISIBLE);
				if(!userObject.optString("通知书EMS").equals("未发出"))
				{
					new AlertDialog.Builder(this)  
					 .setIcon(android.R.drawable.ic_dialog_email)
					 .setTitle(R.string.dialog_pleaseconfirmyournotice)
					 .setCancelable(false)
				     //.setMessage(R.string.dialog_pleaseconfirmyournotice)//设置显示的内容  
				     .setPositiveButton(R.string.hasreceived,new DialogInterface.OnClickListener() {//添加确定按钮  
				         @Override  
				         public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件  
				  
				        	 confirmLetterOfAdmiss();
				         }  
				  
				     }).setNegativeButton(R.string.notreceive,null).show();//在按键响应事件中显示此对话框 
				}
			}
			else
			{
				tv_already_confirmed.setVisibility(View.VISIBLE);
				bt_notice_confirm.setVisibility(View.GONE);
			}
		}
		else
		{
			if(!user.getsStatus().equals("接站员"))
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
		final EditText editText = new EditText(this);
	    AlertDialog.Builder inputDialog = 
	        new AlertDialog.Builder(this);
	    inputDialog.setTitle(R.string.dialog_pleaseinputyourletterno)
	    .setView(editText)
	    .setIcon(android.R.drawable.ic_dialog_info)
	    .setCancelable(false);
	    inputDialog.setPositiveButton(R.string.go, 
	        new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            
	            String letterNo=editText.getText().toString();
	            if(letterNo==null || letterNo.trim().length()==0)
	            	AppUtility.showToastMsg(MyStatusActivity.this, getString(R.string.nocannotbeempty));
	            else
	            	letterConfirm(letterNo);
	            
	        }
	    }).setNegativeButton(R.string.cancel,null)
	    .show();
	    TimeUtility.popSoftKeyBoard(this, editText);
	}
	private void letterConfirm(String letterNo)
	{
		String dataResult = "";
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("action", "letterConfirm");
			jsonObj.put("编号", user.getId());
			jsonObj.put("通知书编号", letterNo);
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
                if(key.equals("通知书EMS") && userObject.optString(key)!=null && userObject.optString(key).length()>0 && !userObject.optString(key).equals("未发出"))
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
                else if(key.equals("需接站人数") && userObject.optString(key)!=null && userObject.optInt(key)>0)
                {
                	changeBtn.setText("查看");
                	changeBtn.setVisibility(View.VISIBLE);
                	changeBtn.setTag(userObject.optString(key));
                	changeBtn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent=new Intent(MyStatusActivity.this,SchoolActivity.class);
							intent.putExtra("templateName", "成绩");
							intent.putExtra("interfaceName", "XUESHENG-CHENGJI-JieZhan.php");
							intent.putExtra("title", "需接站人数");
							intent.putExtra("display", "需接站人数");
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
			et_shenfenzheng.setError("请至少输入两个字符");
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
			jsonObj.put("查询参数", et_shenfenzheng.getText().toString().trim());
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
        //实例化
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //设置ProgressDialog 标题
        mypDialog.setMessage("查询中..");
        //设置ProgressDialog 提示信息
        //设置ProgressDialog 的一个Button
        mypDialog.setIndeterminate(false);
        //设置ProgressDialog 的进度条是否不明确
        mypDialog.setCancelable(false);
        //设置ProgressDialog 是否可以按退回按键取消
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
				//显示扫描到的内容
				
				String result = bundle.getString("result");
				try {
					result = new String(Base64.decode(result.getBytes("GBK")));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				String[] resultStr=result.split("&");
				if(resultStr.length>1)
				{
					String shenfenzheng=resultStr[0];
					et_shenfenzheng.setText(shenfenzheng);
					searchStudent();
					
				}
				
				//显示
				//mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
			}
			break;
		}
    }
	
	
}
