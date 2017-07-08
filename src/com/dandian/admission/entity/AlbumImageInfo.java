package com.dandian.admission.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class AlbumImageInfo implements Serializable {
	public int getPraiseCount() {
		return praiseCount;
	}
	public void setPraiseCount(int praiseCount) {
		this.praiseCount = praiseCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -393029336801316019L;
	private String id;
	private String url;
	private String localPath;
	private String name;
	private String hostName;
	private String hostId;
	
	private String hostBanji;
	private String headUrl;
	private int browerCount;
	private int praiseCount;
	private int commentCount;
	private String address;
	private String time;
	
	private String description;
	private int filesize;
	private String showLimit;
	private String device;
	public AlbumImageInfo() {
		
	}
	public AlbumImageInfo(JSONObject jo) {
		this.name = jo.optString("�ļ���");
		this.url = jo.optString("�ļ���ַ");
		this.hostName = jo.optString("������");
		this.filesize = jo.optInt("�ļ���С");
		this.hostId = jo.optString("������Ψһ��");
		this.hostBanji = jo.optString("�༶");
		this.browerCount = jo.optInt("�������");
		this.address = jo.optString("λ��");
		this.time = jo.optString("ʱ��");
		this.description = jo.optString("����");
		if(this.hostBanji.equals("null"))
			this.hostBanji="δ֪";
		if(this.hostName.equals("null"))
			this.hostName="δ֪";
		this.headUrl=jo.optString("������ͷ��");
		this.showLimit=jo.optString("�ɼ���Χ");
		this.praiseCount=jo.optInt("���޴���");
		this.commentCount=jo.optInt("���۴���");
		this.praiseList=new ArrayList<AlbumMsgInfo>();
		this.commentsList=new ArrayList<AlbumMsgInfo>();
		this.device=jo.optString("��ǰ�豸");
	}
	
	public AlbumImageInfo(net.minidev.json.JSONObject jo) {
		this.name = String.valueOf(jo.get("�ļ���"));
		this.url = String.valueOf(jo.get("�ļ���ַ"));
		this.hostName = String.valueOf(jo.get("������"));
		this.filesize = Integer.parseInt(jo.get("�ļ���С").toString());
		this.hostId = String.valueOf(jo.get("������Ψһ��"));
		this.hostBanji = String.valueOf(jo.get("�༶"));
		this.browerCount =  Integer.parseInt(jo.get("�������").toString());
		this.address = String.valueOf(jo.get("λ��"));
		this.time = String.valueOf(jo.get("ʱ��"));
		this.description = String.valueOf(jo.get("����"));
		if(this.hostBanji.equals("null"))
			this.hostBanji="δ֪";
		if(this.hostName.equals("null"))
			this.hostName="δ֪";
		this.headUrl=String.valueOf(jo.get("������ͷ��"));
		this.showLimit=String.valueOf(jo.get("�ɼ���Χ"));
		this.praiseCount=Integer.parseInt(jo.get("���޴���").toString());
		this.commentCount=Integer.parseInt(jo.get("���۴���").toString());
		this.praiseList=new ArrayList<AlbumMsgInfo>();
		this.commentsList=new ArrayList<AlbumMsgInfo>();
		this.device=String.valueOf(jo.get("��ǰ�豸"));
	}
	
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getShowLimit() {
		return showLimit;
	}
	public void setShowLimit(String showLimit) {
		this.showLimit = showLimit;
	}
	public static List<AlbumImageInfo> toList(JSONArray ja) {
		List<AlbumImageInfo> result = new ArrayList<AlbumImageInfo>();
		AlbumImageInfo info = null;
		if (ja.length() == 0) {
			return result;
		} else {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				if(jo != null){
					info = new AlbumImageInfo(jo);
					result.add(info);
				}	
			}
			return result;
		}
	}
	public static List<AlbumImageInfo> toList(net.minidev.json.JSONArray ja) {
		List<AlbumImageInfo> result = new ArrayList<AlbumImageInfo>();
		AlbumImageInfo info = null;
		if (ja.size() == 0) {
			return result;
		} else {
			for (int i = 0; i < ja.size(); i++) {
				net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject)ja.get(i);
				if(jo != null){
					info = new AlbumImageInfo(jo);
					result.add(info);
				}	
			}
			return result;
		}
	}
	
	public int getFilesize() {
		return filesize;
	}

	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getHostBanji() {
		return hostBanji;
	}
	public void setHostBanji(String hostBanji) {
		this.hostBanji = hostBanji;
	}
	private ArrayList<AlbumMsgInfo> praiseList;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHeadUrl() {
		return headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	public int getBrowerCount() {
		return browerCount;
	}
	public void setBrowerCount(int browerCount) {
		this.browerCount = browerCount;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<AlbumMsgInfo> getPraiseList() {
		return praiseList;
	}
	public void setPraiseList(ArrayList<AlbumMsgInfo> praiseList) {
		this.praiseList = praiseList;
	}
	public ArrayList<AlbumMsgInfo> getCommentsList() {
		return commentsList;
	}
	public void setCommentsList(ArrayList<AlbumMsgInfo> commentsList) {
		this.commentsList = commentsList;
	}
	private ArrayList<AlbumMsgInfo> commentsList;
}
