package com.dandian.admission.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 问卷图片
 * 
 *  <br/>创建说明: 2014-5-6 上午10:37:37 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class ImageItem {
	private String name;
	private String loadCount;
	private String lastDown;
	private String downAddress;
	private String fileName;
	private long filesize;
	private String curriculumName;
	private String subjectId;
	
	
	public ImageItem() {

	}

	public ImageItem(JSONObject jo) {
		name = jo.optString("名称");
		loadCount = jo.optString("下载次数");
		lastDown = jo.optString("�?后一次下�?");
		curriculumName = jo.optString("课程名称");
		subjectId = jo.optString("上课记录编号");
		downAddress = jo.optString("文件地址");
		fileName = jo.optString("文件�?");
		filesize = jo.optLong("文件大小");
	}

	public static List<ImageItem> toList(JSONArray ja) {
		List<ImageItem> result = new ArrayList<ImageItem>();
		ImageItem info = null;
		if (ja.length() == 0) {
			Log.d("DownloadSubject","没有DownloadSubject数据");
			return null;
		} else {
			Log.d("ImageItem","ja.length" + ja.length()+ "List<ImageItem> toList");
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				if(jo != null){
					info = new ImageItem(jo);
					result.add(info);
				}	
			}
			return result;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoadCount() {
		return loadCount;
	}

	public void setLoadCount(String loadCount) {
		this.loadCount = loadCount;
	}

	public String getLastDown() {
		return lastDown;
	}

	public void setLastDown(String lastDown) {
		this.lastDown = lastDown;
	}

	public String getDownAddress() {
		return downAddress;
	}

	public void setDownAddress(String downAddress) {
		this.downAddress = downAddress;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	public String getCurriculumName() {
		return curriculumName;
	}

	public void setCurriculumName(String curriculumName) {
		this.curriculumName = curriculumName;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
}
