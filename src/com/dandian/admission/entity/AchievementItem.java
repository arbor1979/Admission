package com.dandian.admission.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  �汾˵��: $id:$ <br/>
 *
 *  ����˵��: �ɼ�
 * 
 *  <br/>����˵��: 2014-4-16 ����6:41:34 shengguo  �����ļ�<br/>
 * 
 *  �޸���ʷ:<br/>
 *
 */
public class AchievementItem {
	private String templateName;
	private String title;
	private List<Achievement> achievements;
	private String rightButton;
	private String rightButtonURL;

	public AchievementItem(JSONObject jo) {
		templateName = jo.optString("����ģ��");
		title = jo.optString("������ʾ");
		achievements = new ArrayList<Achievement>();
		JSONArray joa = jo.optJSONArray("�ɼ���ֵ");
		if(joa!=null)
		{
			for (int i = 0; i < joa.length(); i++) {
				Achievement a = new Achievement(joa.optJSONObject(i));
				achievements.add(a);
			}
		}
		rightButton=jo.optString("���ϰ�ť");
		rightButtonURL=jo.optString("���ϰ�ťURL");
	}

	public String getRightButton() {
		return rightButton;
	}

	public void setRightButton(String rightButton) {
		this.rightButton = rightButton;
	}

	public String getRightButtonURL() {
		return rightButtonURL;
	}

	public void setRightButtonURL(String rightButtonURL) {
		this.rightButtonURL = rightButtonURL;
	}

	public class Achievement {
		private String id;// ���
		private String icon;// ͼ��
		private String title;// ����
		private String total;// �ܷ�
		private String rank;// ����
		private String detailUrl;// �����ַ
	    private String templateName;
	    private String templateGrade;
	    private String theColor;
		public Achievement(JSONObject jo) {
			id = jo.optString("���");
			icon = jo.optString("ͼ��");
			title = jo.optString("��һ��");
			total = jo.optString("�ڶ�����");
			rank = jo.optString("�ڶ�����");
			detailUrl = jo.optString("������URL");
			templateName = jo.optString("ģ��");
			templateGrade = jo.optString("ģ�弶��");
			theColor= jo.optString("��ɫ");
		}

		public String getTheColor() {
			return theColor;
		}

		public void setTheColor(String theColor) {
			this.theColor = theColor;
		}

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getTemplateGrade() {
			return templateGrade;
		}

		public void setTemplateGrade(String templateGrade) {
			this.templateGrade = templateGrade;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTotal() {
			return total;
		}

		public void setTotal(String total) {
			this.total = total;
		}

		public String getRank() {
			return rank;
		}

		public void setRank(String rank) {
			this.rank = rank;
		}

		public String getDetailUrl() {
			return detailUrl;
		}

		public void setDetailUrl(String detailUrl) {
			this.detailUrl = detailUrl;
		}
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}
	
}