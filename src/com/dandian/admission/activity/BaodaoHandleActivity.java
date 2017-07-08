package com.dandian.admission.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.dandian.admission.CampusApplication;
import com.dandian.admission.api.CampusAPI;
import com.dandian.admission.api.CampusException;
import com.dandian.admission.api.CampusParameters;
import com.dandian.admission.api.RequestListener;
import com.dandian.admission.base.Constants;
import com.dandian.admission.entity.User;
import com.dandian.admission.util.AppUtility;
import com.dandian.admission.util.Base64;
import com.dandian.admission.util.DateHelper;
import com.dandian.admission.util.DialogUtility;
import com.dandian.admission.util.ExpressionUtil;
import com.dandian.admission.R;
import com.dandian.admission.R.drawable;
import com.dandian.admission.R.id;
import com.dandian.admission.R.layout;

public class BaodaoHandleActivity extends Activity {

	AQuery aq;
	private LinearLayout ll_studentinfo,loadingLayout;
	private JSONObject userObject,completeResult,otherObject;
	MyAdapter adapter;
	private List<String> groupkey=new ArrayList<String>(); 
	private List<String> aList = new ArrayList<String>();  
	private String ID;
	private ProgressDialog mypDialog;
	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baodao_handle);
		ll_studentinfo=(LinearLayout)this.findViewById(R.id.ll_studentinfo);
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		loadingLayout.setVisibility(View.GONE);
		
		aq = new AQuery(this);
		
		aq.id(R.id.back).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		aq.id(R.id.setting_tv_title).text("��������");		
		ID=getIntent().getStringExtra("ID");
		if(ID!=null || ID.length()>0)
			getUserInfo();
		else
			AppUtility.showErrorToast(this,"���֤�Ų���Ϊ��");
	}
	private void getUserInfo()
	{
		showProgress(true);
		String dataResult = "";
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("action", "getinfo");
			jsonObj.put("���", ID);
			jsonObj.put("userRole", user.getsStatus());
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
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	private void showProgress(boolean progress) {
		if (progress) {
			loadingLayout.setVisibility(View.VISIBLE);
			ll_studentinfo.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			ll_studentinfo.setVisibility(View.VISIBLE);
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showProgress(false);
				showProgress1(false);
				AppUtility.showErrorToast(BaodaoHandleActivity.this,
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
						AppUtility.showToastMsg(BaodaoHandleActivity.this, loginStatus,1);
					} else 
					{
						
						userObject=jo.optJSONObject("�û���Ϣ");
						String luqustr=jo.optString("������");
						completeResult=jo.optJSONObject("������");
						otherObject=jo.optJSONObject("��������");
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
						AppUtility.showToastMsg(BaodaoHandleActivity.this, loginStatus,1);
					} else 
					{
						
						String action=jo.optString("action");
						if(jo.optString("������")!=null && jo.optString("������").length()>0)
							completeResult.put(action, jo.optInt("������"));
						userObject.put(action, jo.optString("��ʾֵ"));
						if(action.equals("��������") && jo.optInt("������")==0)
						{
							userObject.put("ѧ������","");
							userObject.put("��λ��",0);
						}
							
					}
					adapter.notifyDataSetChanged();
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				break;
		
			}
		};
	};
	private void initContent() {
		
		ImageOptions options = new ImageOptions();
		//options.round=40;
		options.memCache=true;
		options.fileCache=true;
		
		String userImage=userObject.optString("��Ƭ");
		if(userImage!=null && userImage.length()>0)
		{
			aq.id(R.id.iv_pic).image(userImage,options);
		}
		
		
		aq.id(R.id.tv_name).text(userObject.optString("����"));
		aq.id(R.id.user_type).text(userObject.optString("ѧ��״̬"));
		
		aq.id(R.id.iv_pic).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtility.showImageDialog(BaodaoHandleActivity.this,userObject.optString("��Ƭ"));
				
			}
			
		});
		
		aq.id(R.id.btn_msg).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(BaodaoHandleActivity.this, ChatMsgActivity.class);
				intent.putExtra("toid", userObject.optString("�û�Ψһ��"));
				intent.putExtra("toname", userObject.optString("����"));
				intent.putExtra("type", "txt");
				intent.putExtra("userImage", userObject.optString("��Ƭ"));
				startActivity(intent);
			}
		});
		aq.id(R.id.btn_call).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ArrayList<String> userphones=new ArrayList<String>();
				userphones.add("ѧ���绰:"+userObject.optString("ѧ���绰"));
				userphones.add("���׵绰 :"+userObject.optString("���׵绰"));
				userphones.add("ĸ�׵绰 :"+userObject.optString("ĸ�׵绰"));
				if(userObject.optString("������ʦ")!=null && userObject.optString("������ʦ").length()>0 && ExpressionUtil.isMobileNO(userObject.optString("������ʦ")))
					userphones.add("���õ绰 :"+userObject.optString("������ʦ"));
				final String []  userStr=new String[userphones.size()];
				for(int i=0;i<userphones.size();i++)
				{
					userStr[i]=userphones.get(i);
				}
				new AlertDialog.Builder(BaodaoHandleActivity.this).setTitle("�����")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(userStr, 0, new DialogInterface.OnClickListener() 
				{ 
					public void onClick(DialogInterface dialog, int which) 
					{ 
						String[]telStr=userStr[which].split(":");
						if(telStr.length==2 && telStr[1]!=null)
						{
							String tel=telStr[1];
							if(tel.length()>=11)
							{
								if (ContextCompat.checkSelfPermission(BaodaoHandleActivity.this,  
						                Manifest.permission.CALL_PHONE)  
						        != PackageManager.PERMISSION_GRANTED) { 
									ActivityCompat.requestPermissions(BaodaoHandleActivity.this,  
							                new String[]{Manifest.permission.CALL_PHONE},  
							                1);
								}
								else
								{
									Intent phoneIntent = new Intent("android.intent.action.CALL",Uri.parse("tel:" + tel));
									startActivity(phoneIntent);
								}
							}
						}
						dialog.dismiss();
					} 
				} 
				).setNegativeButton("ȡ��", null) .show();
			}
			
		});
		adapter=new MyAdapter(this);
		aq.id(R.id.listView1).adapter(adapter);
		
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
        	final String key=aList.get(position);
            if(groupkey.contains(getItem(position))){  
                view=mInflater.inflate(R.layout.addexam_list_item_tag, null);  
                TextView text=(TextView) view.findViewById(R.id.addexam_list_item_text); 
                text.setText(key);
            }else{  
                view=mInflater.inflate(R.layout.list_left_right_image_check, null);  
                TextView title=(TextView)view.findViewById(R.id.left_title);
                TextView detail=(TextView)view.findViewById(R.id.right_detail);
                RadioGroup rg_jiudufangshi=(RadioGroup)view.findViewById(R.id.rg_jiudufangshi);
                rg_jiudufangshi.setVisibility(View.GONE);
                
                LinearLayout ll_checkbox=(LinearLayout)view.findViewById(R.id.ll_checkbox);
                CheckBox checkBox1=(CheckBox)view.findViewById(R.id.checkBox1);
                CheckBox checkBox2=(CheckBox)view.findViewById(R.id.checkBox2);
                CheckBox checkBox3=(CheckBox)view.findViewById(R.id.checkBox3);
                CheckBox checkBox4=(CheckBox)view.findViewById(R.id.checkBox4);
                CheckBox checkBox5=(CheckBox)view.findViewById(R.id.checkBox5);
                checkBox1.setEnabled(false);
                checkBox2.setEnabled(false);
                checkBox3.setEnabled(false);
                checkBox4.setEnabled(false);
                checkBox5.setEnabled(false);
                ImageView iv_complete=(ImageView)view.findViewById(R.id.iv_complete);
                title.setText(key);
                //detail.setText(userObject.optString(key));
                Button changeBtn=(Button)view.findViewById(R.id.bt_changeNumber);
                if(key.equals("�����֤"))
                {
                	detail.setVisibility(View.GONE);
                	ll_checkbox.setVisibility(View.VISIBLE);
                	checkBox1.setVisibility(View.VISIBLE);
                	checkBox2.setVisibility(View.GONE);
                	checkBox3.setVisibility(View.GONE);
                	checkBox4.setVisibility(View.GONE);
                	checkBox5.setVisibility(View.GONE);
                	if(user.getsStatus().equals("��������Ա"))
                	{
                		checkBox1.setEnabled(true);
                	}
                	checkBox1.setText(userObject.optString(key));
                	if(userObject.optString(key).equals("����֤"))
                		checkBox1.setChecked(true);
                	else
                		checkBox1.setChecked(false);
                	checkBox1.setTag(userObject.optString(key));
                	checkBox1.setOnClickListener(new OnClickListener(){
						@Override
						 public void onClick(final View v) {
			                final boolean checked = ((CheckBox) v).isChecked();
			                if(!checked)
			                {
			                	if(completeResult.optInt("��ȡУ԰��")==1 || completeResult.optInt("��������")==1 || completeResult.optInt("������Կ��")==1)
			                	{
			                		AppUtility.showErrorToast(BaodaoHandleActivity.this,
			            					"���к�����������ɣ��������޷�ȡ��");
			                		((CheckBox) v).setChecked(true);
			                		return;
			                	}
			                	new AlertDialog.Builder(BaodaoHandleActivity.this).setTitle("ȷ�ϳ�����") 
			                    .setIcon(android.R.drawable.ic_menu_info_details) 
			                    .setCancelable(false)
			                    .setPositiveButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	updateBaodao(key,checked,null);
			                        } 
			                    }) 
			                    .setNegativeButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	((CheckBox) v).setChecked(true);
			                        } 
			                    })
			                    .show(); 
			                }
			                else
			                	updateBaodao(key,checked,null);

						}
                	});
                }
                else if(key.equals("��ȡ����"))
                {
                	detail.setVisibility(View.GONE);
                	ll_checkbox.setVisibility(View.VISIBLE);
                	checkBox1.setVisibility(View.VISIBLE);
                	checkBox2.setVisibility(View.VISIBLE);
                	checkBox3.setVisibility(View.VISIBLE);
                	checkBox4.setVisibility(View.VISIBLE);
                	checkBox5.setVisibility(View.VISIBLE);
                	if(user.getsStatus().equals("��������Ա"))
                	{
                		checkBox1.setEnabled(true);
                		checkBox2.setEnabled(true);
                		checkBox3.setEnabled(true);
                		checkBox4.setEnabled(true);
                		checkBox5.setEnabled(true);
                	}
                	String[] ziliaoArray=userObject.optString(key).split("\n");
                	for(int i=0;i<ziliaoArray.length;i++)
                	{
                		CheckBox cb=(CheckBox)ll_checkbox.getChildAt(i);
                		final String[] itemArray=ziliaoArray[i].split(":");
                		cb.setText(itemArray[0]);
                		if(itemArray.length==2 && itemArray[1]!=null && itemArray[1].equals("���ύ"))
                			cb.setChecked(true);
                    	else
                    		cb.setChecked(false);
                		cb.setOnClickListener(new OnClickListener(){
    						@Override
    						 public void onClick(final View v) {
    			                final boolean checked = ((CheckBox) v).isChecked();
    			                if(checked && conditionVerify())
    			                {
    			                	((CheckBox) v).setChecked(false);
    			                	return;
    			                }
    			                if(!checked)
    			                {
    			                	new AlertDialog.Builder(BaodaoHandleActivity.this).setTitle("ȷ�ϳ�����") 
    			                    .setIcon(android.R.drawable.ic_menu_info_details) 
    			                    .setCancelable(false)
    			                    .setPositiveButton("��", new DialogInterface.OnClickListener() { 
    			                        @Override 
    			                        public void onClick(DialogInterface dialog, int which) { 
    			                        	updateBaodao(itemArray[0],checked,null);
    			                        } 
    			                    }) 
    			                    .setNegativeButton("��", new DialogInterface.OnClickListener() { 
    			                        @Override 
    			                        public void onClick(DialogInterface dialog, int which) { 
    			                        	((CheckBox) v).setChecked(true);
    			                        } 
    			                    })
    			                    .show(); 
    			                }
    			                else
    			                	updateBaodao(itemArray[0],checked,null);
    			                	
    						}
                    	});
                	}
                }
                else  if(key.equals("��ȡУ԰��"))
                {
                	detail.setVisibility(View.GONE);
                	ll_checkbox.setVisibility(View.VISIBLE);
                	checkBox1.setVisibility(View.VISIBLE);
                	checkBox2.setVisibility(View.GONE);
                	checkBox3.setVisibility(View.GONE);
                	checkBox4.setVisibility(View.GONE);
                	checkBox5.setVisibility(View.GONE);
                	if(user.getsStatus().equals("��������Ա"))
                	{
                		checkBox1.setEnabled(true);
                	}
                	checkBox1.setText(userObject.optString(key));
                	if(userObject.optString(key).equals("δ��ȡ"))
                		checkBox1.setChecked(false);
                	else
                		checkBox1.setChecked(true);
                	checkBox1.setTag(userObject.optString(key));
                	checkBox1.setOnClickListener(new OnClickListener(){
						@Override
						 public void onClick(final View v) {
			                final boolean checked = ((CheckBox) v).isChecked();
			                if(checked && conditionVerify())
			                {
			                	((CheckBox) v).setChecked(false);
			                	return;
			                }
			                if(!checked)
			                {
			                	new AlertDialog.Builder(BaodaoHandleActivity.this).setTitle("ȷ�ϳ�����") 
			                    .setIcon(android.R.drawable.ic_menu_info_details) 
			                    .setCancelable(false)
			                    .setPositiveButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	updateBaodao(key,checked,null);
			                        } 
			                    }) 
			                    .setNegativeButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	((CheckBox) v).setChecked(true);
			                        } 
			                    })
			                    .show(); 
			                }
			                else
			                	updateBaodao(key,checked,null);
			                	
						}
                	});
                }
                else  if(key.equals("��������"))
                {
                	detail.setVisibility(View.GONE);
                	ll_checkbox.setVisibility(View.VISIBLE);
                	checkBox1.setVisibility(View.VISIBLE);
                	checkBox2.setVisibility(View.GONE);
                	checkBox3.setVisibility(View.GONE);
                	checkBox4.setVisibility(View.GONE);
                	checkBox5.setVisibility(View.GONE);
                	if(user.getsStatus().equals("��������Ա"))
                	{
                		checkBox1.setEnabled(true);
                	}
                	checkBox1.setText(userObject.optString(key));
                	if(userObject.optString(key).equals("δ����"))
                		checkBox1.setChecked(false);
                	else
                		checkBox1.setChecked(true);
                	checkBox1.setTag(userObject.optString(key));
                	checkBox1.setOnClickListener(new OnClickListener(){
						@Override
						 public void onClick(final View v) {
			                final boolean checked = ((CheckBox) v).isChecked();
			                if(checked && conditionVerify())
			                {
			                	((CheckBox) v).setChecked(false);
			                	return;
			                }
			                if(!checked)
			                {
			                	if(completeResult.optInt("������Կ��")==1)
			                	{
			                		AppUtility.showErrorToast(BaodaoHandleActivity.this,
			            					"���к�����������ɣ��������޷�ȡ��");
			                		((CheckBox) v).setChecked(true);
			                		return;
			                	}
			                }
			                else
			                {
			                	if(userObject.optString("�Ͷ���ʽ").equals("�߶�"))
			                	{
			                		AppUtility.showErrorToast(BaodaoHandleActivity.this,
			            					"�߶��������������");
			                		((CheckBox) v).setChecked(false);
			                		return;
			                	}
			                }
			                if(!checked)
			                {
			                	new AlertDialog.Builder(BaodaoHandleActivity.this).setTitle("ȷ�ϳ�����") 
			                    .setIcon(android.R.drawable.ic_menu_info_details) 
			                    .setCancelable(false)
			                    .setPositiveButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	updateBaodao(key,checked,null);
			                        } 
			                    }) 
			                    .setNegativeButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	((CheckBox) v).setChecked(true);
			                        } 
			                    })
			                    .show(); 
			                }
			                else
			                {
			                	Intent intent=new Intent(BaodaoHandleActivity.this,DormBedActivity.class);
			                	intent.putExtra("���", ID);
			                	intent.putExtra("�Ա�", userObject.optString("�Ա�"));
			                	startActivityForResult(intent,101);
			                }
			                
			                
			                	
						}
                	});
                }
                else  if(key.equals("������Կ��"))
                {
                	detail.setVisibility(View.GONE);
                	ll_checkbox.setVisibility(View.VISIBLE);
                	checkBox1.setVisibility(View.VISIBLE);
                	checkBox2.setVisibility(View.GONE);
                	checkBox3.setVisibility(View.GONE);
                	checkBox4.setVisibility(View.GONE);
                	checkBox5.setVisibility(View.GONE);
                	if(user.getsStatus().equals("�������Ա"))
                		checkBox1.setEnabled(true);
                	checkBox1.setText(userObject.optString(key));
                	if(userObject.optString(key).equals("δ��ȡ"))
                		checkBox1.setChecked(false);
                	else
                		checkBox1.setChecked(true);
                	checkBox1.setTag(userObject.optString(key));
                	checkBox1.setOnClickListener(new OnClickListener(){
						@Override
						 public void onClick(final View v) {
			                final boolean checked = ((CheckBox) v).isChecked();
			                if(checked && conditionVerify())
			                {
			                	((CheckBox) v).setChecked(false);
			                	return;
			                }
			                if(checked && completeResult.optInt("��������")==0)
			                {
			                	AppUtility.showErrorToast(BaodaoHandleActivity.this,
		            					"���ȷ�������");
		                		((CheckBox) v).setChecked(false);
		                		return;
			                }
			                if(!checked)
			                {
			                	new AlertDialog.Builder(BaodaoHandleActivity.this).setTitle("ȷ�ϳ�����") 
			                    .setIcon(android.R.drawable.ic_menu_info_details) 
			                    .setCancelable(false)
			                    .setPositiveButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	updateBaodao(key,checked,null);
			                        } 
			                    }) 
			                    .setNegativeButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	((CheckBox) v).setChecked(true);
			                        } 
			                    })
			                    .show(); 
			                }
			                else
			                	updateBaodao(key,checked,null);
			                
			                	
						}
                	});
                }
                else  if(key.equals("�Ͷ���ʽ"))
                {
                	detail.setVisibility(View.GONE);
                	ll_checkbox.setVisibility(View.GONE);
                	if(user.getsStatus().equals("��������Ա"))
                	{
                		rg_jiudufangshi.setVisibility(View.VISIBLE);
                		
                		for(int i=0;i<rg_jiudufangshi.getChildCount();i++)
                		{
                			RadioButton item=(RadioButton)rg_jiudufangshi.getChildAt(i);
                			if(item.getText().equals(userObject.optString(key)))
                			{
                				item.setChecked(true);
                				break;
                			}
                		}

                		rg_jiudufangshi.setTag(userObject.optString(key));
                		rg_jiudufangshi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
    						@Override
    						public void onCheckedChanged(RadioGroup group, int checkedId){
    							
    							RadioButton selItem=(RadioButton)group.findViewById(checkedId);
    							if(selItem.isPressed())
        						{
	    							if(checkedId==R.id.radio2)
	    							{
	    								if(completeResult.optInt("��������")==1 || completeResult.optInt("������Կ��")==1)
	    								{
	    									AppUtility.showErrorToast(BaodaoHandleActivity.this,
	        		            					"�ѷ����������������Կ�ף����ȳ���");
	    									for(int i=0;i<group.getChildCount();i++)
	    			                		{
	    			                			RadioButton item=(RadioButton)group.getChildAt(i);
	    			                			if(item.getText().equals(userObject.optString(key)))
	    			                			{
	    			                				item.setChecked(true);
	    			                				break;
	    			                			}
	    			                		}
	        		                		return;
	    								}
	    								
	    							}
	    							
	    			                updateBaodao(selItem.getText().toString(),true,null);
    							}
    			                	
    						}
                    	});
                	}
                	else
                	{
                		detail.setVisibility(View.VISIBLE);
                		detail.setText(userObject.optString(key));
                	}
                	
                }
                else  if(key.equals("ѧ���ս�"))
                {
                	detail.setVisibility(View.GONE);
                	ll_checkbox.setVisibility(View.VISIBLE);
                	checkBox1.setVisibility(View.VISIBLE);
                	checkBox2.setVisibility(View.GONE);
                	checkBox3.setVisibility(View.GONE);
                	checkBox4.setVisibility(View.GONE);
                	checkBox5.setVisibility(View.GONE);
                	if(user.getsStatus().equals("������Ա") || user.getsStatus().equals("��ɫͨ�����Ա"))
                	{
                		checkBox1.setEnabled(true);
                	}
                	checkBox1.setText(userObject.optString(key));
                	if(userObject.optString(key).equals("��������") || userObject.optString(key).equals("��ɫͨ��"))
                		checkBox1.setChecked(true);
                	else
                		checkBox1.setChecked(false);
                	checkBox1.setTag(userObject.optString(key));
                	checkBox1.setOnClickListener(new OnClickListener(){
						@Override
						 public void onClick(final View v) {
			                final boolean checked = ((CheckBox) v).isChecked();
			                if(!checked)
			                {
			                	if(completeResult.optInt("��ȡУ԰��")==1 || completeResult.optInt("��������")==1 || completeResult.optInt("������Կ��")==1)
			                	{
			                		AppUtility.showErrorToast(BaodaoHandleActivity.this,
			            					"���к�����������ɣ��������޷�ȡ��");
			                		((CheckBox) v).setChecked(true);
			                		return;
			                	}
			                	new AlertDialog.Builder(BaodaoHandleActivity.this).setTitle("ȷ�ϳ�����") 
			                    .setIcon(android.R.drawable.ic_menu_info_details) 
			                    .setCancelable(false)
			                    .setPositiveButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	updateBaodao(key,checked,null);
			                        } 
			                    }) 
			                    .setNegativeButton("��", new DialogInterface.OnClickListener() { 
			                        @Override 
			                        public void onClick(DialogInterface dialog, int which) { 
			                        	((CheckBox) v).setChecked(true);
			                        } 
			                    })
			                    .show(); 
			                }
			                else
			                {
			                	if(user.getsStatus().equals("������Ա"))
			                		popPayDlg(key,v);
			                	else if(user.getsStatus().equals("��ɫͨ�����Ա"))
			                		popGreenDlg(key,v);
			                	//updateBaodao(key,checked,null);
			                }

						}
                	});
                	
                }
                else
                {
                	detail.setVisibility(View.VISIBLE);
                	ll_checkbox.setVisibility(View.GONE);
                	detail.setText(userObject.optString(key));
                }
                	
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
	private boolean conditionVerify()
	{
    	if(completeResult.optInt("�����֤")==0)
    	{
    		AppUtility.showErrorToast(BaodaoHandleActivity.this,
					"���Ƚ��������֤");
    		return true;
    	}
    	else if(completeResult.optInt("��ѧ��")==0)
    	{
    		AppUtility.showErrorToast(BaodaoHandleActivity.this,
					"���Ƚ�ѧ��");
    		return true;
    	}
    	return false;
	}
	private void  updateBaodao(String key, boolean checked,JSONObject otherParams)
	{
		showProgress1(true);
		String dataResult = "";
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("action", key);
			jsonObj.put("���", ID);
			jsonObj.put("checked", checked);
			jsonObj.put("userid", user.getUsername());
			jsonObj.put("language", language);
			jsonObj.put("client", "Android");
			jsonObj.put("otherParams",otherParams);
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
        mypDialog.setMessage("������..");
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
	private void popPayDlg(final String key,final View v)
	{
		String title="��������";
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.dialog_green_channel,
			    null);
		textEntryView.setOnTouchListener(touchListener);
		TextView tv_totalmoney=(TextView)textEntryView.findViewById(R.id.tv_totalmoney);
		tv_totalmoney.setText("Ӧ��ѧ�ѣ�"+userObject.optString("�շѱ�׼")+"Ԫ");
		final RadioGroup rg_ifpay=(RadioGroup)textEntryView.findViewById(R.id.rg_ifpay);
		rg_ifpay.setVisibility(View.GONE);
		final LinearLayout ll_delay=(LinearLayout)textEntryView.findViewById(R.id.ll_delay);
		final LinearLayout ll_reduce=(LinearLayout)textEntryView.findViewById(R.id.ll_reduce);
		ll_delay.setVisibility(View.GONE);
		ll_reduce.setVisibility(View.GONE);
		final EditText ed_fapiao=(EditText)textEntryView.findViewById(R.id.ed_fapiao);
		
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(title).setView(textEntryView)
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				JSONObject obj=new JSONObject();
				try {
						obj.put("��������","��������");
						obj.put("��Ʊ��",ed_fapiao.getText().toString());
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					AppUtility.showToastMsg(BaodaoHandleActivity.this, e.getMessage());
					((CheckBox) v).setChecked(false);
					return;
				}
				updateBaodao(key,true,obj);
			}
			
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((CheckBox) v).setChecked(false);
			}
			
		});
        Dialog searchDialog=builder.create();
        searchDialog.show();
	}
	private void popGreenDlg(final String key,final View v)
	{
		String title="��ɫͨ��";
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.dialog_green_channel,
			    null);
		textEntryView.setOnTouchListener(touchListener);
		TextView tv_totalmoney=(TextView)textEntryView.findViewById(R.id.tv_totalmoney);
		tv_totalmoney.setText("Ӧ��ѧ�ѣ�"+userObject.optString("�շѱ�׼")+"Ԫ");
		final RadioGroup rg_ifpay=(RadioGroup)textEntryView.findViewById(R.id.rg_ifpay);
		final LinearLayout ll_normal=(LinearLayout)textEntryView.findViewById(R.id.ll_normal);
		ll_normal.setVisibility(View.GONE);
		final LinearLayout ll_delay=(LinearLayout)textEntryView.findViewById(R.id.ll_delay);
		ll_delay.setVisibility(View.VISIBLE);
		final LinearLayout ll_reduce=(LinearLayout)textEntryView.findViewById(R.id.ll_reduce);
		ll_reduce.setVisibility(View.GONE);
		final EditText ed_other=(EditText)textEntryView.findViewById(R.id.ed_other);
		final EditText ed_cutoffdate=(EditText)textEntryView.findViewById(R.id.ed_cutoffdate);
		ed_other.setVisibility(View.GONE);
		final EditText ed_reduce_reason=(EditText)textEntryView.findViewById(R.id.ed_reduce_reason);
		
		// ��ʼ���ؼ�
		final Spinner spinner1 = (Spinner) textEntryView.findViewById(R.id.sp_delay_item);
		final Spinner spinner2 = (Spinner) textEntryView.findViewById(R.id.sp_delay_reason);
		// ��������Դ
		
		String[] mItems;
		final String[] mReasons;
		try {
			JSONArray delayItems=otherObject.getJSONArray("������Ŀ");
			JSONArray delayReasons=otherObject.getJSONArray("����ԭ��");
			if(delayItems==null || delayItems.length()==0)
			{
				AppUtility.showToastMsg(this, "������ĿΪ��");
				return;
			}
			if(delayReasons==null || delayReasons.length()==0)
			{
				AppUtility.showToastMsg(this, "����ԭ��Ϊ��");
				return;
			}
			mItems = new String[delayItems.length()];
			for(int i=0;i<delayItems.length();i++)
			{
				mItems[i]=delayItems.getString(i);
			}
			ed_cutoffdate.setText(otherObject.getString("������ֹ����"));
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//�� Adapter���ؼ�
			spinner1.setAdapter(adapter);
			mReasons=new String[delayReasons.length()];
			for(int i=0;i<delayReasons.length();i++)
			{
				mReasons[i]=delayReasons.getString(i);
			}
			ArrayAdapter<String> adapter2=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mReasons);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//�� Adapter���ؼ�
			spinner2.setAdapter(adapter2);
			spinner2.setOnItemSelectedListener(new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(mReasons[position].equals("����"))
						ed_other.setVisibility(View.VISIBLE);
					else
						ed_other.setVisibility(View.GONE);
					
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			AppUtility.showToastMsg(this, e.getLocalizedMessage());
			return;
		}
		
		final EditText ed_reduce_money=(EditText)textEntryView.findViewById(R.id.ed_reduce_money);
		final TextView tv_real_money=(TextView)textEntryView.findViewById(R.id.tv_real_money);
		ed_reduce_money.setOnFocusChangeListener(new OnFocusChangeListener(){
		       
	        @Override
	        public void onFocusChange(View arg0, boolean arg1) {
	            EditText et = (EditText) arg0;
	            if(arg1) {
	                //Log.e("", "��ý���"+detailItem.getId());
	            } else {
	            	updateYingJiao(et,tv_real_money);
	                //Log.e("", "ʧȥ����"+detailItem.getId());
	                
	            }
	        }
	         
	    });
		
		ed_reduce_money.setOnEditorActionListener(new EditText.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) { 
					updateYingJiao(ed_reduce_money,tv_real_money);
				}
				return false;
			}  
			  
		  
		      
		});
		rg_ifpay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
            	if(arg1==R.id.rb_ifpay1)
            	{
            		ll_delay.setVisibility(View.VISIBLE);
            		ll_reduce.setVisibility(View.GONE);
            	}
            	else
            	{
            		ll_delay.setVisibility(View.GONE);
            		ll_reduce.setVisibility(View.VISIBLE);
            	}
            }
        });
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(title).setView(textEntryView)
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				JSONObject obj=new JSONObject();
				try {
					
					if(rg_ifpay.getCheckedRadioButtonId()==R.id.rb_ifpay2)
					{
						int jianmian=0;
						if(ed_reduce_money.getText().toString()!=null && ed_reduce_money.getText().toString().length()>0)
							jianmian=Integer.parseInt(ed_reduce_money.getText().toString());
						if(jianmian<=0)
						{
							throw new Exception("������������0");
						}
						int yingjiao=userObject.optInt("�շѱ�׼");
						if(jianmian>yingjiao)
						{
							throw new Exception("������ܴ���Ӧ�����");
						}
						obj.put("��������","����");
						obj.put("������", jianmian);
						obj.put("����ԭ��", ed_reduce_reason.getText().toString());
					}
					else if(rg_ifpay.getCheckedRadioButtonId()==R.id.rb_ifpay1)
					{
						if(!DateHelper.valid(ed_cutoffdate.getText().toString()))
						{
							throw new Exception("���ڸ�ʽӦΪ:2018-01-01");
						}
						obj.put("��������","����");
						obj.put("������Ŀ", spinner1.getSelectedItem());
						if(ed_other.getVisibility()==View.VISIBLE && ed_other.getText().toString()!=null && ed_other.getText().toString().length()>0)
							obj.put("����ԭ��", ed_other.getText().toString());
						else
							obj.put("����ԭ��", spinner2.getSelectedItem());
						obj.put("������ֹ����", ed_cutoffdate.getText().toString());
					}
					
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					AppUtility.showToastMsg(BaodaoHandleActivity.this, e.getMessage());
					((CheckBox) v).setChecked(false);
					return;
				}
				updateBaodao(key,true,obj);
			}
			
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((CheckBox) v).setChecked(false);
			}
			
		});
        Dialog searchDialog=builder.create();
        searchDialog.show();
		
	}
	private void updateYingJiao(EditText et,TextView tv)
	{
		int jianmian=0;
    	int yingjiao=0;
    	if(et.getText().toString().length()>0)
    	{
	    	try
	        {
	    		jianmian=Integer.parseInt(et.getText().toString());
	    		yingjiao=userObject.optInt("�շѱ�׼");
	        }
	        catch(NumberFormatException e)
	        {
	        	AppUtility.showToastMsg(BaodaoHandleActivity.this, "����������");
	        	et.setText("");
	        	return;
	        }
    	}
    	tv.setText("Ӧ����"+(yingjiao-jianmian));
	}
	private OnTouchListener touchListener= new OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.setFocusable(true);
			v.setFocusableInTouchMode(true);
			v.requestFocus();
			closeInputMethod(v);
			return false;
		}
		
	};
	private void closeInputMethod(View v) {
	    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
	    boolean isOpen = imm.isActive();
	    if (isOpen) {
	        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//û����ʾ����ʾ
	    	imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCodeΪ�ش��ı�ǣ�����B�лش�����RESULT_OK
		   case RESULT_OK:
		    Bundle b=data.getExtras(); //dataΪB�лش���Intent
		    if(b!=null)
		    {
		    	String action=b.getString("action");//str��Ϊ�ش���ֵ
			    try {
					completeResult.put(action, b.getInt("������"));
					userObject.put(action, b.getString("��ʾֵ"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    adapter.notifyDataSetChanged();
			
		    break;
		default:
		    break;
		    }
		}
}
