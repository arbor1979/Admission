package com.dandian.admission.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="Student")
public class NewStudent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String studentID;
	@DatabaseField
	private String password;
	@DatabaseField
	private String name;
	@DatabaseField
	private String className;
	@DatabaseField
	private String gender;
	@DatabaseField
	private String phone;
	@DatabaseField
	private String email;
	@DatabaseField
	private String dormitory;
	@DatabaseField
	private String parentName;
	@DatabaseField
	private String parentPhone;
	@DatabaseField
	private String homeAddress;
	@DatabaseField
	private String remark;
	@DatabaseField
	private String status;
	@DatabaseField
	private int isModify=0;

	private String stuLetter;
	private String onecard;
	private String collect;
	private String payment;
	@DatabaseField
	private String picImage;
	public NewStudent() {

	}
	
	
	public NewStudent(JSONObject jo) {
		id = jo.optString("���");
		studentID = jo.optString("���֤��");
		name = jo.optString("����");
		className = jo.optString("���");
		gender = jo.optString("�Ա�");
		picImage = jo.optString("��Ƭ");
		status= jo.optString("�Ƿ񱨵�");
		dormitory= jo.optString("ѧ������");
		onecard= jo.optString("һ��ͨ����");
		collect= jo.optString("��ȡ����");
		payment= jo.optString("Ԥ����");
	}
	
	
	public String getPayment() {
		return payment;
	}


	public void setPayment(String payment) {
		this.payment = payment;
	}


	public String getOnecard() {
		return onecard;
	}


	public void setOnecard(String onecard) {
		this.onecard = onecard;
	}


	public String getCollect() {
		return collect;
	}


	public void setCollect(String collect) {
		this.collect = collect;
	}


	/**
	 * 编号
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 学号
	 * 
	 * @return
	 */
	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	/**
	 * 密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 姓名
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 班级
	 * 
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * 性别
	 * 
	 * @return
	 */
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * 学生电话
	 * 
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 学生邮箱
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 学生宿舍
	 * 
	 * @return
	 */
	public String getDormitory() {
		return dormitory;
	}

	public void setDormitory(String dormitory) {
		this.dormitory = dormitory;
	}

	/**
	 * 家长姓名
	 * 
	 * @return
	 */
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		if(parentName!=null)
			this.parentName = parentName;
	}

	/**
	 * 家长电话
	 * 
	 * @return
	 */
	public String getParentPhone() {
		return parentPhone;
	}

	public void setParentPhone(String parentPhone) {
		if(parentPhone!=null)
			this.parentPhone = parentPhone;
	}

	/**
	 * 家庭住址
	 * 
	 * @return
	 */
	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		if(homeAddress!=null)
			this.homeAddress = homeAddress;
	}

	/**
	 * 备注
	 * 
	 * @return
	 */
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 学生状�??
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 是否修改
	 * 
	 * @return
	 */
	public int getIsModify() {
		return isModify;
	}

	public void setIsModify(int isModify) {
		this.isModify = isModify;
	}
	
	public String getStuLetter() {
		return stuLetter;
	}
	public void setStuLetter(String stuLetter) {
		this.stuLetter = stuLetter;
	}
	
	public String getPicImage() {
		return picImage;
	}
	public void setPicImage(String picImage) {
		this.picImage = picImage;
	}

	
}
