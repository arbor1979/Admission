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
 * �汾˵��: $id:$ <br/>
 * 
 * ����˵��: �ʾ���������б�
 * 
 * <br/>
 * ����˵��: 2014-4-17 ����6:04:18 shengguo �����ļ�<br/>
 * 
 * �޸���ʷ:<br/>
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
		title = jo.optString("������ʾ");
		submitTo = jo.optString("�ύ��ַ");
		status = jo.optString("�����ʾ�״̬");
		questions = new ArrayList<Question>();
		JSONArray joq = jo.optJSONArray("�����ʾ���ֵ");
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
			title = jo.optString("��Ŀ");
			status = jo.optString("����");
			remark = jo.optString("��ע");
			Log.d("-----", jo.toString());
			JSONArray ja = jo.optJSONArray("ѡ��");
			if(ja!=null){
				options = new String[ja.length()];
				for (int i = 0; i < ja.length(); i++) {
					options[i] = ja.optString(i);
				}
			}
			subOptions= jo.optJSONObject("��ѡ��");
			
			isRequired = jo.optString("�Ƿ����");
			lines=jo.optInt("����");
			needCut=jo.optString("����");
			addcallback=jo.optString("addcallback");
			delcallback=jo.optString("delcallback");
			if(status.equals("ͼƬ")){
				JSONArray jaimages = jo.optJSONArray("�û���");
				if(jaimages!=null){
					setImages(ImageItem.toList(jaimages));
				}else{
					setImages(new ArrayList<ImageItem>());
				}
			}else{
				usersAnswer = jo.optString("�û���");
				usersAnswerOne=jo.optString("�û���һ��");
			}
			maxLetter=jo.optInt("�ַ���");
			validate=jo.optString("У��");
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
