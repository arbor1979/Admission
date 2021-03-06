package com.dandian.admission.fragment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dandian.admission.activity.CutImageActivity;
import com.dandian.admission.activity.ImagesActivity;
import com.dandian.admission.adapter.MyPictureAdapter;
import com.dandian.admission.api.CampusAPI;
import com.dandian.admission.api.CampusException;
import com.dandian.admission.api.CampusParameters;
import com.dandian.admission.api.RequestListener;
import com.dandian.admission.base.Constants;
import com.dandian.admission.entity.DownloadSubject;
import com.dandian.admission.entity.ImageItem;
import com.dandian.admission.entity.QuestionnaireList;
import com.dandian.admission.entity.QuestionnaireList.Question;
import com.dandian.admission.lib.DateTimePickDialogUtil;
import com.dandian.admission.util.AppUtility;
import com.dandian.admission.util.Base64;
import com.dandian.admission.util.DateHelper;
import com.dandian.admission.util.DialogUtility;
import com.dandian.admission.util.FileUtility;
import com.dandian.admission.util.ImageUtility;
import com.dandian.admission.util.PrefUtility;
import com.dandian.admission.widget.NonScrollableGridView;
import com.dandian.admission.widget.NonScrollableListView;
import com.dandian.admission.R;

/**
 * 调查问卷
 */
@SuppressLint("ValidFragment")
public class SchoolQuestionnaireDetailFragment extends Fragment {
	private final String TAG = "SchoolQuestionnaireDetailFragment";
	private ListView myListview;
	private Button btnLeft;
	private TextView tvTitle, tvRight;
	private LinearLayout lyLeft, lyRight,loadingLayout,contentLayout,failedLayout,emptyLayout;
	private QuestionnaireList questionnaireList;
	private String title,status, interfaceName,picturePath,delImagePath,autoClose;
	private LayoutInflater inflater;
	private QuestionAdapter adapter;
	private boolean isEnable = true;
	private Dialog dialog, getPictureDiaLog;
	private MyPictureAdapter myPictureAdapter;
	private List<String> picturePaths = new ArrayList<String>();// 选中的图片路径
	private ArrayList<Question> questions = new ArrayList<Question>();
	private List<ImageItem> images = new ArrayList<ImageItem>();
	private static final int REQUEST_CODE_TAKE_PICTURE = 2;// //设置图片操作的标志
	private static final int REQUEST_CODE_TAKE_CAMERA = 1;// //设置拍照操作的标志
	
	private String needCut;
	private String addcallback;
	private String delcallback;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String result = "";
			String resultStr = "";
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				if(myPictureAdapter!=null)
				{
					myPictureAdapter.setPicPaths(picturePaths);
					myPictureAdapter.notifyDataSetChanged();
				}
				break;
			case 0://获取数据
				showProgress(false);
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if (AppUtility.isNotEmpty(res)) {
							AppUtility.showToastMsg(getActivity(), res);
						} else {
							questionnaireList = new QuestionnaireList(jo);
							Log.d(TAG,
									"--------noticesItem.getNotices().size():"
											+ questionnaireList.getQuestions()
													.size());
							Log.d(TAG,
									"---------" + questionnaireList.getStatus());
							tvTitle.setText(questionnaireList.getTitle());
							questions = questionnaireList.getQuestions();
							status=questionnaireList.getStatus();
							if (status.equals("已结束") || status.equals("未开始")) {
								isEnable = false;
								lyRight.setVisibility(View.INVISIBLE);
							}
							else
							{
								isEnable=true;
								tvRight.setVisibility(View.VISIBLE);
							}
							
							adapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 1://保存成功
				
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if(!AppUtility.isNotEmpty(res))
							res=jo.optString("状态");
						if(res.equals("成功"))
						{
							AppUtility.showToastMsg(getActivity(), getString(R.string.savesuc));
							if(autoClose!=null && autoClose.equals("是"))
							{
								
								Intent aintent = new Intent();
								getActivity().setResult(1,aintent); 
								getActivity().finish();
							}
						}
						else
							AppUtility.showErrorToast(getActivity(), getString(R.string.failed)+":"+res);
							
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), getString(R.string.failed)+":"+e.getMessage());
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), getString(R.string.failed)+":"+e.getMessage());
						
					}
					
				}
				tvRight.setText(R.string.save);
				break;
			case 2://删除图片
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					try {
						JSONObject	jo = new JSONObject(resultStr);
						if("成功".equals(jo.optString("STATUS"))){
							for (int i = 0; i < images.size(); i++) {
								if(images.get(i).getDownAddress().equals(delImagePath)){
									images.remove(i);
								}
							}
							File cacheFile=FileUtility.getCacheFile(delImagePath);
							if(cacheFile.exists())
								cacheFile.delete();
							picturePaths.remove(delImagePath);
							myPictureAdapter.setPicPaths(picturePaths);
							myPictureAdapter.notifyDataSetChanged();
							if(delcallback!=null && delcallback.length()>0)
							{
								exeUrlRequest(delcallback,delImagePath);
							}
						}else{
							AppUtility.showToastMsg(getActivity(), jo.optString("STATUS"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case 3://图片上传
				result = msg.obj.toString();
				resultStr = "";
				Bundle data=msg.getData();
				String oldFileName=data.getString("oldFileName");
				
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				try {
					JSONObject jo = new JSONObject(resultStr);
					if("OK".equals(jo.optString("STATUS"))){
						
						String newFileName=jo.getString("文件名");
						FileUtility.fileRename(oldFileName, newFileName);
						ImageItem ds = new ImageItem(jo);
						images.add(ds);
						
						picturePaths.add(ds.getDownAddress());
						myPictureAdapter.setPicPaths(picturePaths);
						myPictureAdapter.notifyDataSetChanged();
						if(addcallback!=null && addcallback.length()>0)
						{
							exeUrlRequest(addcallback,ds.getDownAddress());
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};
	public SchoolQuestionnaireDetailFragment() {

	}
	public SchoolQuestionnaireDetailFragment(String title,String status,
			String iunterfaceName,String autoClose) {
		if (status.equals("已结束") || status.equals("未开始")) {
			isEnable = false;
		}
		this.title = title;
		this.status = status;
		this.interfaceName = iunterfaceName;
		this.autoClose=autoClose;
	}

	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerBroastcastReceiver();
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_listview_fragment,
				container, false);
		myListview = (ListView) view.findViewById(R.id.my_listview);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvRight = (TextView) view.findViewById(R.id.tv_right);
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);

		myListview.setEmptyView(emptyLayout);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		tvRight.setText(R.string.save);
		tvTitle.setText(title);
		if (status.equals("进行中")) {
			tvRight.setVisibility(View.VISIBLE);

		} else {
			lyRight.setVisibility(View.INVISIBLE);
		}
		adapter = new QuestionAdapter();
		myListview.setAdapter(adapter);
		getPictureDiaLog = new Dialog(getActivity(), R.style.dialog);
		//退出
		lyLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		//保存数据
		lyRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "-----保存");
				if (status.equals("已结束") || status.equals("未开始")) {
					return;
				} else {
					Log.d(TAG, "-----保存");
					saveQuestionAnswer();
				}
			}
		});
		// 重新加载
		failedLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getQuestionsItem();
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//AppUtility.showToastMsg(getActivity(), "正在获取数据");
		getQuestionsItem();
	}

	private void registerBroastcastReceiver() {
		IntentFilter mFilter = new IntentFilter(Constants.GET_PICTURE);
		mFilter.addAction(Constants.DEL_OR_LOOK_PICTURE);
		getActivity().registerReceiver(mBroadcastReceiver, mFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String fromTag=intent.getStringExtra("TAG");
			Log.d(TAG, "--------action:" + action);
			Log.d(TAG, "--------fromTag:" + fromTag);
			if (action.equals(Constants.GET_PICTURE)&&fromTag.equals(TAG)) {
				needCut=intent.getStringExtra("needCut");
				addcallback=intent.getStringExtra("addcallback");
				showGetPictureDiaLog();
			}else if(action.equals(Constants.DEL_OR_LOOK_PICTURE)&&fromTag.equals(TAG)){
				//查看详图或删除图片
				delImagePath = intent.getStringExtra("imagePath");
				delcallback=intent.getStringExtra("delcallback");
				showDelOrShowPictureDiaLog(delImagePath);
			}
		}
	};
	
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	};
	/**
	 * 功能描述:获取图片
	 * 
	 * @author shengguo 2014-5-5 下午3:45:04
	 * 
	 */
	private void showGetPictureDiaLog() {
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.view_get_picture, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView byCamera = (TextView) view.findViewById(R.id.tv_by_camera);
		TextView byLocation = (TextView) view.findViewById(R.id.tv_by_location);
		getPictureDiaLog.setContentView(view);
		getPictureDiaLog.show();
		Window window = getPictureDiaLog.getWindow();
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPictureDiaLog.dismiss();
			}
		});
		//调用系统相机拍照
		byCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPictureByCamera();
				getPictureDiaLog.dismiss();
			}
		});
		//选择本地图片
		byLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPictureFromLocation();
				getPictureDiaLog.dismiss();
			}
		});
	}
	/**
	 * 功能描述:删除或查看图片
	 *
	 * @author shengguo  2014-5-8 下午6:32:49
	 *
	 */
	private void showDelOrShowPictureDiaLog(final String imageName) {
		
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.view_show_or_del_picture, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView delPicture = (TextView) view.findViewById(R.id.tv_delete);
		TextView showPicture = (TextView) view.findViewById(R.id.tv_show);
		View v = view.findViewById(R.id.view_dividing_line);
		final AlertDialog ad=new AlertDialog.Builder(getActivity()).setView(view).create();
		if(isEnable){
			delPicture.setVisibility(View.VISIBLE);
			v.setVisibility(View.VISIBLE);
		}else{
			delPicture.setVisibility(View.GONE);
			v.setVisibility(View.GONE);
		}
		
		
		Window window = ad.getWindow();
		
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		ad.show();
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
		//删除图片
		delPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//String string=imageName.substring(0, imageName.indexOf("?"));
				String fileName=imageName.substring(imageName.lastIndexOf("/")+1, imageName.length());
				SubmitDeleteinfo(fileName);
				ad.dismiss();
			}
		});
		//显示大图
		showPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				picturePaths.remove("");
				Intent intent = new Intent(getActivity(), ImagesActivity.class);
				intent.putStringArrayListExtra("pics", (ArrayList<String>) picturePaths);
				
				for (int i = 0; i < picturePaths.size(); i++) {
					if(picturePaths.get(i).equals(imageName)){
						intent.putExtra("position", i);
					}
				}
				startActivity(intent);
				ad.dismiss();
			}
		});
	}


	/**
	 * 调用系统相机拍照获取图片
	 * 
	 * @param
	 */
	private void getPictureByCamera() {
		if (ContextCompat.checkSelfPermission(getActivity(),  
                Manifest.permission.CAMERA)  
        != PackageManager.PERMISSION_GRANTED) { 
			ActivityCompat.requestPermissions(getActivity(),  
	                new String[]{Manifest.permission.CAMERA},  
	                2); 
		}
		else
		{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				AppUtility.showToastMsg(getActivity(), getString(R.string.Commons_SDCardErrorTitle));
				return;
			}
			picturePath =FileUtility.getRandomSDFileName("jpg");
			
			File mCurrentPhotoFile = new File(picturePath);
			Uri uri = Uri.fromFile(mCurrentPhotoFile);
			
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
		}
	}

	/**
	 * 功能描述:从本地获取图片
	 * 
	 * @author shengguo 2014-5-8 上午10:58:45
	 * 
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void getPictureFromLocation() {
		/*
		picturePaths.remove("");
		Intent intent = new Intent(getActivity(),AlbumActivity.class);
		intent.putStringArrayListExtra("picturePaths",
				(ArrayList<String>) picturePaths);
		intent.putExtra("size", 5);
		startActivityForResult(intent,
				SchoolDetailActivity.REQUEST_CODE_TAKE_PICTURE);
		*/
		if (ContextCompat.checkSelfPermission(getActivity(),  
                Manifest.permission.READ_EXTERNAL_STORAGE)  
        != PackageManager.PERMISSION_GRANTED) { 
			ActivityCompat.requestPermissions(getActivity(),  
	                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},  
	                3); 
		}
		else
		{
			Intent intent; 
			intent = new Intent(Intent.ACTION_PICK, 
			                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
				
		}
		
	}
	/**
	 * 显示加载失败提示页
	 */
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		contentLayout.setVisibility(View.GONE);
		failedLayout.setVisibility(View.VISIBLE);
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

	/**
	 * 功能描述:获取问卷内容
	 * 
	 * @author shengguo 2014-4-16 上午11:12:43
	 * 
	 */
	public void getQuestionsItem() {
		showProgress(true);
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		Locale locale = getResources().getConfiguration().locale;
		String language = locale.getCountry();
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
			jo.put("language", language);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchoolItem(params, interfaceName, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	
	private void exeUrlRequest(String urlStr,String address)
	{
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject urlJson=null;
		JSONObject dataJson=null;
		try {
			urlJson = new JSONObject(urlStr);
			dataJson= new JSONObject();
			dataJson.put("用户较验码", checkCode);
			dataJson.put("picaddress", address);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToSchool(interfaceName,urlJson,dataJson, mHandler, 4);
	}
	
		
	/**
	 * 功能描述:保存问卷答案
	 * 
	 * @author shengguo 2014-5-5 下午5:29:30
	 * 
	 */
	private void saveQuestionAnswer() {
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		
		long datatime = System.currentTimeMillis();
		Locale locale = getResources().getConfiguration().locale;
		String language = locale.getCountry();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		// String userId = PrefUtility.get(Constants.PREF_USER_ID, "");
		// String fromId = PrefUtility.get(Constants.PREF_USER_NUNMBER, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		JSONArray joarr = getAnswers();
		if(joarr==null){
			return ;
		}
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("选项记录集", joarr);
			jo.put("DATETIME", datatime);
			jo.put("language", language);
			jo.put("client", "Android");
			// jo.put("USER_ID", userId);//可不传
			// jo.put("SCHOOLID", 0);//可不传
			// / jo.put("FROMID", fromId);//可不传
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		dialog = DialogUtility.createLoadingDialog(getActivity(),
				getString(R.string.saving));
		dialog.show();
		Log.d(TAG, "------->jo:" + jo.toString());
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		int pos=interfaceName.indexOf("?");
		String preUrl=interfaceName;
		if(pos>-1)
			preUrl=interfaceName.substring(0, pos);
		CampusAPI.getSchoolItem(params,
				preUrl + questionnaireList.getSubmitTo(),
				new RequestListener() {

					@Override
					public void onIOException(IOException e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(CampusException e) {
						Log.d(TAG, "----response" + e.getMessage());
						if(dialog != null){
							dialog.dismiss();
						}
						Message msg = new Message();
						msg.what = -1;
						msg.obj = e.getMessage();
						mHandler.sendMessage(msg);
					}

					@Override
					public void onComplete(String response) {
						Log.d(TAG, "----response" + response);
						if(dialog != null){
							dialog.dismiss();
						}
						Message msg = new Message();
						msg.what = 1;
						msg.obj = response;
						mHandler.sendMessage(msg);
					}
				});
	}
	
	/**
	 * 功能描述:处理文件上传
	 * 
	 * @author shengguo 2013-12-26 下午4:36:51
	 * 
	 * @param mCurrentFile
	 *
	 */
	public void uploadFile(File file)  {
		if(!file.exists()) return;
        try
        {
			DownloadSubject	downloadSubject = new DownloadSubject();
			String filebase64Str = FileUtility.fileupload(file);
			downloadSubject.setFilecontent(filebase64Str);
			String filename = file.getName();
			downloadSubject.setFileName(filename);
			downloadSubject.setLocalfile(file.getAbsolutePath());
			downloadSubject.setFilesize(file.length());
			SubmitUploadFile(downloadSubject);
			
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
	}
	/**
	 * 功能描述:上传文件
	 *
	 * @author shengguo  2013-12-18 上午11:48:59
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitUploadFile(DownloadSubject downloadSubject){
		
		final CampusParameters params = new CampusParameters();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// 获取用户校验码
		params.add("用户较验码", checkCode);
		params.add("课程名称", questionnaireList.getTitle());
		params.add("老师上课记录编号", questionnaireList.getTitle());
		params.add("图片类别", "问卷调查");
		params.add("文件名称", downloadSubject.getFileName());
		params.add("文件内容", downloadSubject.getFilecontent());
		picturePaths.remove("");
		picturePaths.add("loading");
		myPictureAdapter.setPicPaths(picturePaths);
		myPictureAdapter.notifyDataSetChanged();
		
		CampusAPI.uploadFiles(params, new RequestListener(){

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "------------------response"+response);
				picturePaths.remove("loading");
				Message msg = new Message();
				msg.what = 3;
				msg.obj = response;
				Bundle data=new Bundle();
				data.putString("oldFileName", params.getValue("文件名称"));
				msg.setData(data);
				mHandler.sendMessage(msg);	
				
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "图片上传失败");
				if(dialog != null){
					dialog.dismiss();
				}
				picturePaths.remove("loading");
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);	
			}
		});
	}
	/**
	 * 功能描述:获取答案
	 * 
	 * @author shengguo 2014-5-5 下午5:37:56
	 * 
	 * @return
	 */
	private JSONArray getAnswers() {
		JSONArray joarr = new JSONArray();
		for (int i = 0; i < questions.size(); i++) {
			String mStatus = questions.get(i).getStatus();
			if(!mStatus.equals("图片")){
				String title=questions.get(i).getTitle();
				String usersAnswer = questions.get(i).getUsersAnswer();
				String isRequired = questions.get(i).getIsRequired();//是否必填
				String validate=questions.get(i).getValidate();
				if(AppUtility.isNotEmpty(isRequired)){
					if(isRequired.equals("是")){
						if(AppUtility.isNotEmpty(usersAnswer)){
							joarr.put(usersAnswer);
						}else{
							AppUtility.showToastMsg(getActivity(),getString(R.string.pleasefinishform));
							myListview.setSelection(i);
							
							return null;
						}
					}else{
						joarr.put(usersAnswer);
					}
				}else{
					if(AppUtility.isNotEmpty(usersAnswer)){
						joarr.put(usersAnswer);
					}else{
						Log.d(TAG, "222"+questions.get(i).getTitle());
						AppUtility.showToastMsg(getActivity(),getString(R.string.pleasefinishform));
						myListview.setSelection(i);
						return null;
					}
				}
				if(AppUtility.isNotEmpty(validate)){
					if(validate.equals("手机号") && !AppUtility.checkPhone(usersAnswer))
					{
						AppUtility.showToastMsg(getActivity(),title+",格式不正确");
						myListview.setSelection(i);
						return null;
					}
					else if(validate.equals("浮点型") && !AppUtility.isDecimal(usersAnswer))
					{
						AppUtility.showToastMsg(getActivity(),title+",必须是浮点型数字,如:99.9");
						myListview.setSelection(i);
						return null;
					}
					else if(validate.equals("整型") && !AppUtility.isInteger(usersAnswer))
					{
						AppUtility.showToastMsg(getActivity(),title+",必须整形数字,如:99");
						myListview.setSelection(i);
						return null;
					}
					else if(validate.equals("邮箱") && !AppUtility.checkEmail(usersAnswer))
					{
						AppUtility.showToastMsg(getActivity(),title+",邮箱格式不正确");
						myListview.setSelection(i);
						return null;
					}
				}
			}else{
				JSONArray joimages = new JSONArray();
				for (ImageItem imageItem :images) {
					JSONObject joimgs = new JSONObject();
					try {
						joimgs.put("文件名", imageItem.getFileName());
						joimgs.put("文件地址", imageItem.getDownAddress());
						joimgs.put("课程名称", imageItem.getCurriculumName());
						joimgs.put("下载次数", imageItem.getLoadCount());
						joimgs.put("上课记录编号", imageItem.getSubjectId());
						joimgs.put("最后一次下载", imageItem.getLastDown());
						joimgs.put("名称", imageItem.getName());
						joimgs.put("STATUS", "OK");
						joimages.put(joimgs);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				joarr.put(joimages);
			}
		}
		Log.d(TAG, joarr.toString());
		return joarr;
	}
	/**
	 * 功能描述:删除图片
	 *
	 * @author shengguo  2014-5-9 下午12:05:03
	 * 
	 * @param fileName
	 */
	public void SubmitDeleteinfo(String fileName) {
		JSONObject jo = new JSONObject();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "--------------filename----------" + fileName);
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", String.valueOf(new Date().getTime()));
			jo.put("课件名称", fileName);
			jo.put("图片类别", "问卷调查");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString()
				.getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.sendDownloadDeleteData(params, new RequestListener() {

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
	public List<String> getPicturePaths() {
		return picturePaths;
	}
	private void rotateAndCutImage(final File file) {
		if(!file.exists()) return;
		if(AppUtility.formetFileSize(file.length()) > 5242880*2){
			AppUtility.showToastMsg(getActivity(), "对不起，您上传的文件太大了，请选择小于10M的文件！");
		}else{
			
			ImageUtility.rotatingImageIfNeed(file.getAbsolutePath());
			Intent intent=new Intent(getActivity(),CutImageActivity.class);
			intent.putExtra("picPath", file.getAbsolutePath());
			startActivityForResult(intent,3);
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_TAKE_CAMERA: // 拍照返回
				//Bundle bundle = data.getExtras();
				//Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
				//ImageUtility.writeTofiles(bitmap, picturePath);
				if (resultCode == getActivity().RESULT_OK) {
					if(needCut!=null && needCut.equals("是"))
						rotateAndCutImage(new File(picturePath));
					else
						uploadFile(new File(picturePath));
							
				}
				
				break;
			case REQUEST_CODE_TAKE_PICTURE:
				
				if (data != null) {
					Uri uri = data.getData();
					String[] pojo  = { MediaStore.Images.Media.DATA };
					CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, pojo, null,null, null); 
					Cursor cursor = cursorLoader.loadInBackground();
					cursor.moveToFirst(); 
				    picturePath = cursor.getString(cursor.getColumnIndex(pojo[0])); 
					
					String tempPath =FileUtility.getRandomSDFileName("jpg");
					if(FileUtility.copyFile(picturePath,tempPath))
					{
						if(needCut!=null && needCut.equals("是"))
							rotateAndCutImage(new File(tempPath));
						else
							uploadFile(new File(tempPath));
					}
					else
						AppUtility.showErrorToast(getActivity(), getString(R.string.failedcopytosdcard));
				}
				break;
			case 3:
				if (resultCode == 200 && data != null) {
					
					String picPath = data.getStringExtra("picPath");
					uploadFile(new File(picPath));
				}
		}
	};

	

	class QuestionAdapter extends BaseAdapter {
	
		int mFocusPosition = -1;
		OnFocusChangeListener mListener = new OnFocusChangeListener() {

	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	            int position = (Integer) v.getTag();
	            if (hasFocus) {
	                mFocusPosition = position;
	            }
	            Log.e("test", "onFocusChange:" + position + " " + hasFocus);
	        }
	    };
		@Override
		public int getCount() {
			return questions.size();
		}

		@Override
		public Object getItem(int position) {
			return questions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	
		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
						
			final Question question = (Question) getItem(position);
			convertView = inflater.inflate(R.layout.school_questionnaire_item, parent, false);
			final ViewHolder holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_questionnaire_name);
			holder.radioGroup = (RadioGroup) convertView.findViewById(R.id.rg_choose);
			holder.multipleChoice = (NonScrollableListView) convertView.findViewById(R.id.lv_choose);
			holder.etAnswer = (EditText) convertView.findViewById(R.id.et_answer);
			holder.tvAnswer = (TextView) convertView.findViewById(R.id.tv_answer);
			holder.imageGridView = (NonScrollableGridView) convertView.findViewById(R.id.grid_picture);
			holder.tvRemark = (TextView) convertView.findViewById(R.id.tv_remark);
			holder.bt_date=(Button)convertView.findViewById(R.id.bt_date);
			holder.bt_datetime=(Button)convertView.findViewById(R.id.bt_datetime);
			holder.sp_select=(Spinner)convertView.findViewById(R.id.sp_select);
			holder.sp_select1=(Spinner)convertView.findViewById(R.id.sp_select1);
			holder.etAnswer.setOnFocusChangeListener(mListener);
			holder.etAnswer.setTag(position);
			
			String mStatus = question.getStatus();
			
			holder.title.setText(position+1+"."+question.getTitle());
			String remark = question.getRemark();
			if(AppUtility.isNotEmpty(remark)){
				holder.tvRemark.setText(remark);
				holder.tvRemark.setVisibility(View.VISIBLE);
				if(remark!=null && remark.length()>=2)
				{
					if(remark.substring(0, 2).equals("通过")){
						holder.tvRemark.setTextColor(getActivity().getResources().getColor(R.color.title_nor));
					}else if(remark.substring(0, 2).equals("拒绝")){
						holder.tvRemark.setTextColor(Color.RED);
					}
				}
				else
					holder.tvRemark.setTextColor(Color.BLUE);
			}
			else
				holder.tvRemark.setVisibility(View.GONE);
			if (mStatus.equals("单选")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.VISIBLE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				holder.sp_select1.setVisibility(View.GONE);
				final String[] answers = question.getOptions();
				holder.radioGroup.removeAllViews();
				int checkIndex = -1;
				holder.radioGroup.setOnCheckedChangeListener(null);
				for (int i = 0; i < answers.length; i++) {
					View v= inflater.inflate(R.layout.my_radiobutton, parent, false);
					RadioButton radioButton = (RadioButton) v.findViewById(R.id.rb_chenck);
					radioButton.setText(answers[i].toString());
					radioButton.setTextSize(12.0f);
					radioButton.setId(i);
					radioButton.setEnabled(isEnable);
					if (answers[i].equals(question.getUsersAnswer())) {
						checkIndex = i;
					}
					holder.radioGroup.addView(radioButton);
				}
				if (checkIndex != -1) {
					holder.radioGroup.clearCheck();
					holder.radioGroup.check(checkIndex);
				} else {
					holder.radioGroup.clearCheck();
				}
				holder.radioGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								Log.d(TAG, "选中了" + answers[checkedId]);
								question.setUsersAnswer(answers[checkedId]);
								questions.set(position, question);
								questionnaireList.setQuestions(questions);
							}
						});
			} else if (mStatus.equals("多选")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.VISIBLE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				holder.sp_select1.setVisibility(View.GONE);
				CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(
						getActivity(), position, question);
				holder.multipleChoice.setAdapter(checkBoxAdapter);
				
			} else if (mStatus.equals("单行文本输入框")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				
				if (status.equals("已结束") || status.equals("未开始")) {
					holder.etAnswer.setVisibility(View.GONE);
					holder.tvAnswer.setVisibility(View.VISIBLE);
					holder.tvAnswer.setText(question.getUsersAnswer());
				} else {
					holder.etAnswer.setVisibility(View.VISIBLE);
					holder.tvAnswer.setVisibility(View.GONE);
					holder.etAnswer.setText(question.getUsersAnswer());
					if(question.getLines()>0)
						holder.etAnswer.setLines(question.getLines());
					if (mFocusPosition == position) {
						holder.etAnswer.requestFocus();
			        } else {
			        	holder.etAnswer.clearFocus();
			        }
					if(question.getMaxLetter()>0)
						holder.etAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(question.getMaxLetter())}); 
					holder.etAnswer.addTextChangedListener(new TextWatcher() {
						
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						Log.d(TAG, "-------------" + s);
						
					}
	
					@Override
					public void beforeTextChanged(CharSequence s,
							int start, int count, int after) {
						// TODO Auto-generated method stub
	
					}
	
					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
						question.setUsersAnswer(s.toString());
						questions.set(position, question);
						questionnaireList.setQuestions(questions);
					}
				});
					
				}
			} else if (mStatus.equals("图片")) {
				holder.imageGridView.setVisibility(View.VISIBLE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				holder.sp_select1.setVisibility(View.GONE);
				Log.d(TAG, "-----------------" + picturePaths.size());
				List<ImageItem> images = question.getImages();
				if(images != null){
					for (int i = 0; i < images.size(); i++) {
						String imagePath = images.get(i).getDownAddress();
						if(!picturePaths.contains(imagePath)){
							picturePaths.add(images.get(i).getDownAddress());
						}
					}
				}
				int size=5;
				if(question.getLines()>0)
					size=question.getLines();
				if(question.getRemark().equals("通过"))
					myPictureAdapter = new MyPictureAdapter(getActivity(),false,picturePaths,size);
				else
					myPictureAdapter = new MyPictureAdapter(getActivity(),isEnable,picturePaths,size);
				if(question.getRemark()!=null && question.getRemark().length()>0)
				{
					
					holder.tvRemark.setVisibility(View.VISIBLE);
					if(question.getRemark().equals("通过"))
					{
						holder.tvRemark.setText("图片已通过审核，不能再修改");
						holder.tvRemark.setTextColor(getActivity().getResources().getColor(R.color.title_nor));
					}
					else
					{
						holder.tvRemark.setText(question.getRemark()+" 请删除后重新上传");
						holder.tvRemark.setTextColor(getActivity().getResources().getColor(R.color.red_color));
					}
				}
				myPictureAdapter.setFrom(TAG);
				myPictureAdapter.setNeedCut(question.getNeedCut());
				myPictureAdapter.setAddcallback(question.getAddcallback());
				myPictureAdapter.setDelcallback(question.getDelcallback());
				holder.imageGridView.setAdapter(myPictureAdapter);
			}
			else if (mStatus.equals("日期")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.VISIBLE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				holder.sp_select1.setVisibility(View.GONE);
				if(!AppUtility.isNotEmpty(question.getUsersAnswer()))
				{
					question.setUsersAnswer(DateHelper.getToday());
				}
				holder.bt_date.setText(question.getUsersAnswer());

				holder.bt_date.setOnClickListener(new OnClickListener(){
					
					private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){  //
						@Override
						public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
						
							question.setUsersAnswer(DateHelper.getDateString(new Date(arg1-1900,arg2,arg3), "yyyy-MM-dd"));
							Button bt=(Button)arg0.getTag();
							bt.setText(question.getUsersAnswer());
						}
					};
						
					@Override
					public void onClick(View v) {
						Date dt=DateHelper.getStringDate(question.getUsersAnswer(), "yyyy-MM-dd");
						Calendar cal=Calendar.getInstance();
						cal.setTime(dt);
						DatePickerDialog dialog = new DatePickerDialog(getActivity(),listener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
						dialog.getDatePicker().setTag(v);
						if(question.getOptions().length==2)
						{
							Date minDt=DateHelper.getStringDate(question.getOptions()[0], "yyyy-MM-dd");
							Date maxDt=DateHelper.getStringDate(question.getOptions()[1], "yyyy-MM-dd");
							dialog.getDatePicker().setMinDate(minDt.getTime());
							dialog.getDatePicker().setMaxDate(maxDt.getTime());
						}
						dialog.setButton2("取消", new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						dialog.show();
					}
					
				});
	
			}
			else if (mStatus.equals("日期时间")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.VISIBLE);
				holder.sp_select.setVisibility(View.GONE);
				holder.sp_select1.setVisibility(View.GONE);
				if(!AppUtility.isNotEmpty(question.getUsersAnswer()))
				{
					question.setUsersAnswer(DateHelper.getToday());
				}
				holder.bt_datetime.setText(question.getUsersAnswer());

				holder.bt_datetime.setOnClickListener(new OnClickListener(){
				
					@Override
					public void onClick(View v) {
						
						DateTimePickDialogUtil dialog = new DateTimePickDialogUtil(getActivity(),question.getUsersAnswer(),"yyyy-MM-dd HH:mm");
						Button bt=(Button)v;
						bt.setTag(question);
						dialog.dateTimePicKDialog(bt);
						
					}
					
				});
	
			}
			else if (mStatus.equals("下拉")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.VISIBLE);
				holder.sp_select1.setVisibility(View.GONE);
				holder.sp_select.setEnabled(isEnable);
				ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,question.getOptions());
				holder.sp_select.setAdapter(aa);
				int pos=0;
				for(int i=0;i<question.getOptions().length;i++)
				{
					if(question.getOptions()[i].equalsIgnoreCase(question.getUsersAnswer()))
						pos=i;
				}
				holder.sp_select.setSelection(pos);
				holder.sp_select.setOnItemSelectedListener(new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						question.setUsersAnswer(question.getOptions()[position]);
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
			else if (mStatus.equals("二级下拉")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.VISIBLE);
				holder.sp_select1.setVisibility(View.VISIBLE);
				holder.sp_select.setEnabled(isEnable);
				holder.sp_select1.setEnabled(isEnable);
				ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,question.getOptions());
				holder.sp_select.setAdapter(aa);
				int pos=0;
				for(int i=0;i<question.getOptions().length;i++)
				{
					if(question.getOptions()[i].equalsIgnoreCase(question.getUsersAnswerOne()))
						pos=i;
				}
				holder.sp_select.setSelection(pos);
				holder.sp_select.setOnItemSelectedListener(new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						question.setUsersAnswerOne(question.getOptions()[position]);
						reloadSpinner2(holder.sp_select1,question);
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						
					}
				});
				reloadSpinner2(holder.sp_select1,question);
				
				holder.sp_select1.setOnItemSelectedListener(new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						question.setUsersAnswer(holder.sp_select1.getSelectedItem().toString());
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
			return convertView;
		}

		class ViewHolder {
			TextView title;
			EditText etAnswer;
			TextView remark;
			TextView tvAnswer;
			TextView tvRemark;
			RadioGroup radioGroup;
			NonScrollableListView multipleChoice;
			NonScrollableGridView imageGridView;
			Spinner sp_select;
			Spinner sp_select1;
			Button bt_date;
			Button bt_datetime;
		}
		
	}
	private void reloadSpinner2(Spinner sp,Question question)
	{
		JSONArray subOptionsJson=question.getSubOptions().optJSONArray(question.getUsersAnswerOne());
		if(subOptionsJson!=null && subOptionsJson.length()>0)
		{
			String [] subOptions=new String[subOptionsJson.length()];
			int pos=0;
			for(int i=0;i<subOptionsJson.length();i++)
			{
				try {
					subOptions[i]=subOptionsJson.getString(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(question.getUsersAnswer().equals(subOptions[i]))
					pos=i;
					
			}
			ArrayAdapter<String> bb = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,subOptions);
			sp.setAdapter(bb);
			sp.setSelection(pos);
		}
	}
	/**
	 * 
	 *  #(c) ruanyun PocketCampus <br/>
	 *
	 *  版本说明: $id:$ <br/>
	 *
	 *  功能说明: 加载多选列表
	 * 
	 *  <br/>创建说明: 2014-5-17 上午9:44:52 shengguo  创建文件<br/>
	 * 
	 *  修改历史:<br/>
	 *
	 */
	@SuppressWarnings("unused")
	private class CheckBoxAdapter extends BaseAdapter {

		private Context context;
		private String[] anwsers;
		private String anwser;
		private int questionIndex;// question 在list中的下标
		private Question question;
		public Map<String, Boolean> isChecked = new HashMap<String, Boolean>();

		public CheckBoxAdapter(Context context, int questionIndex,
				Question question) {
			super();
			this.context = context;
			this.questionIndex = questionIndex;
			this.question = question;
			anwsers = question.getOptions();
			anwser = question.getUsersAnswer();
			initDate();
		}

		private void initDate() {

			String[] arr = anwser.split("@");
			List<String> list = Arrays.asList(arr);
			for (int i = 0; i < anwsers.length; i++) {
				if (list.contains(anwsers[i])) {
					isChecked.put(anwsers[i], true);
				} else {
					isChecked.put(anwsers[i], false);
				}
			}
		}

		public String getAnwser() {
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < anwsers.length; i++) {
				if (isChecked.get(anwsers[i])) {
					str.append(anwsers[i]).append("@");
				}
			}
			if (str.indexOf(",") > -1) {
				str.deleteCharAt(str.lastIndexOf("@"));
			}
			return str.toString();
		}

		public void setAnwser(String anwser) {
			this.anwser = anwser;
		}

		@Override
		public int getCount() {
			return anwsers.length;
		}

		@Override
		public Object getItem(int position) {
			return anwsers[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			view = inflater.inflate(R.layout.checkbox_item, parent, false);
			final CheckBox cb = (CheckBox) view.findViewById(R.id.cb_chenck);

			cb.setText(anwsers[position]);
			if (isChecked.get(anwsers[position])) {
				cb.setChecked(true);
			} else {
				cb.setChecked(false);
			}
			cb.setEnabled(isEnable);
			cb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Boolean flag = cb.isChecked();
					isChecked.put(anwsers[position], flag);
					String answer = getAnwser();
					Log.d(TAG, "---------" + answer +question.getStatus()+"ss" +question.getTitle());
					question.setUsersAnswer(answer);
					questions.set(questionIndex, question);
					questionnaireList.setQuestions(questions);
				}
			});
			return view;
		}
	}
	

}
