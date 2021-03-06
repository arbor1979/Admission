package com.dandian.admission.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 问卷调查详情列表
 * 
 * <br/>
 * 创建说明: 2014-4-17 下午6:04:18 shengguo 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class QuestionnaireList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7775213654657975509L;
	private String title;
	private String submitTo;
	private String status;
	private ArrayList<Question> questions;

	public QuestionnaireList(JSONObject jo) {
		title = jo.optString("标题显示");
		submitTo = jo.optString("提交地址");
		status = jo.optString("调查问卷状态");
		questions = new ArrayList<Question>();
		JSONArray joq = jo.optJSONArray("调查问卷数值");
		for (int i = 0; i < joq.length(); i++) {
			Question q = new Question(joq.optJSONObject(i));
			questions.add(q);
		}
	}
	

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getSubmitTo() {
		return submitTo;
	}


	public void setSubmitTo(String submitTo) {
		this.submitTo = submitTo;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public ArrayList<Question> getQuestions() {
		return questions;
	}


	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}


	public class Question implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5740781476744525865L;
		private String title;
		private String status;
		private String usersAnswer;
		private String usersAnswerOne;
		private String remark;
		private int lines;
		private String options[];
		private JSONObject subOptions;
		public JSONObject getSubOptions() {
			return subOptions;
		}



		public void setSubOptions(JSONObject subOptions) {
			this.subOptions = subOptions;
		}

		private List<ImageItem> images; 
		private String isRequired;
		private String needCut;
		private String addcallback;
		private String delcallback;
		private int maxLetter;
		private String validate;
		public Question(JSONObject jo) {
			title = jo.optString("题目");
			status = jo.optString("类型");
			remark = jo.optString("备注");
			Log.d("-----", jo.toString());
			JSONArray ja = jo.optJSONArray("选项");
			if(ja!=null){
				options = new String[ja.length()];
				for (int i = 0; i < ja.length(); i++) {
					options[i] = ja.optString(i);
				}
			}
			subOptions= jo.optJSONObject("子选项");
			
			isRequired = jo.optString("是否必填");
			lines=jo.optInt("行数");
			needCut=jo.optString("剪裁");
			addcallback=jo.optString("addcallback");
			delcallback=jo.optString("delcallback");
			if(status.equals("图片")){
				JSONArray jaimages = jo.optJSONArray("用户答案");
				if(jaimages!=null){
					setImages(ImageItem.toList(jaimages));
				}else{
					setImages(new ArrayList<ImageItem>());
				}
			}else{
				usersAnswer = jo.optString("用户答案");
				usersAnswerOne=jo.optString("用户答案一级");
			}
			maxLetter=jo.optInt("字符数");
			validate=jo.optString("校验");
		}

		

		public int getMaxLetter() {
			return maxLetter;
		}



		public void setMaxLetter(int maxLetter) {
			this.maxLetter = maxLetter;
		}



		public String getValidate() {
			return validate;
		}



		public void setValidate(String validate) {
			this.validate = validate;
		}



		public String getUsersAnswerOne() {
			return usersAnswerOne;
		}



		public void setUsersAnswerOne(String usersAnswerOne) {
			this.usersAnswerOne = usersAnswerOne;
		}



		public String getAddcallback() {
			return addcallback;
		}



		public void setAddcallback(String addcallback) {
			this.addcallback = addcallback;
		}



		public String getDelcallback() {
			return delcallback;
		}



		public void setDelcallback(String delcallback) {
			this.delcallback = delcallback;
		}



		public String getNeedCut() {
			return needCut;
		}



		public void setNeedCut(String needCut) {
			this.needCut = needCut;
		}



		public int getLines() {
			return lines;
		}



		public void setLines(int lines) {
			this.lines = lines;
		}



		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUsersAnswer() {
			return usersAnswer;
		}

		public void setUsersAnswer(String usersAnswer) {
			this.usersAnswer = usersAnswer;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String[] getOptions() {
			return options;
		}

		public void setOptions(String[] options) {
			this.options = options;
		}

		public String getIsRequired() {
			return isRequired;
		}

		public void setIsRequired(String isRequired) {
			this.isRequired = isRequired;
		}

		public List<ImageItem> getImages() {
			return images;
		}

		public void setImages(List<ImageItem> images) {
			this.images = images;
		}
	}
}
