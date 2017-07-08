package com.dandian.admission.entity;

import java.io.Serializable;

import org.json.JSONObject;



public class Blog implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4143006483867293979L;

	private int id;
	private String posterId;
	private String posttime;
	private String avater;
	private String poster;
	private String content;
	

	private String answer;
	private String answerContent;
	private String answerTime;
	private String answerAvater;
	
	
	public Blog() {
		super();
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getAnswerContent() {
		return answerContent;
	}
	public void setAnswerContent(String answerContent) {
		this.answerContent = answerContent;
	}
	public String getAnswerTime() {
		return answerTime;
	}
	public void setAnswerTime(String answerTime) {
		this.answerTime = answerTime;
	}
	public String getAnswerAvater() {
		return answerAvater;
	}
	public void setAnswerAvater(String answerAvater) {
		this.answerAvater = answerAvater;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getPosttime() {
		return posttime;
	}
	public void setPosttime(String posttime) {
		this.posttime = posttime;
	}
	public String getAvater() {
		return avater;
	}
	public void setAvater(String avater) {
		this.avater = avater;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}

	public Blog(JSONObject jo) {
		
		this.id = jo.optInt("���");
		this.posterId = jo.optString("������ID");
		this.poster = jo.optString("������");
		this.avater = jo.optString("������ͷ��");
		this.posttime = jo.optString("����ʱ��");
		this.content = jo.optString("����");
		
		this.answer = jo.optString("�ش���");
		this.answerAvater=jo.optString("�ش���ͷ��");
		this.answerTime=jo.optString("�ش�ʱ��");
		this.answerContent=jo.optString("�ش�����");
		
	}
	public String getPosterId() {
		return posterId;
	}
	public void setPosterId(String posterId) {
		this.posterId = posterId;
	}
	

	
}