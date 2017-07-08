package com.dandian.admission.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "User")
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9203715820163398998L;
	/**
	 * 
	 */
	
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String username;
	@DatabaseField
	private String name;
	@DatabaseField
	private String nickname;
	@DatabaseField
	private String department;
	@DatabaseField
	private String gender;
	@DatabaseField
	private String birthday;
	@DatabaseField
	private String phone;
	@DatabaseField
	private String email;
	@DatabaseField
	private String withClass;
	@DatabaseField
	private String withCourse;
	@DatabaseField
	private String companyName;
	@DatabaseField
	private String loginStatus;
	@DatabaseField
	private String loginTime;
	@DatabaseField
	private int isModify;
	@DatabaseField
	private String allowModifyField;
	@DatabaseField
	private String checkCode;
	@DatabaseField
	private String userImage;
	@DatabaseField
	private String virtualClass;
	@DatabaseField
	private String userNumber;
	@DatabaseField
	private String domain;
	@DatabaseField
	private String userType;
	@DatabaseField
	private String recentlyUsedEquipment;
	@DatabaseField
	private String iosDeviceToken;
	@DatabaseField
	private String certificationPath;
	@DatabaseField
	private String userRating;
	@DatabaseField
	private String mainRole;
	@DatabaseField
	private String secondaryRole;
	@DatabaseField
	private String sortNumber;
	@DatabaseField
	private String banLogin;
	@DatabaseField
	private String password;
	@DatabaseField
	private String sClass;
	@DatabaseField
	private String sPhone;
	@DatabaseField
	private String sDormitory;
	@DatabaseField
	private String sEmail;
	@DatabaseField
	private String sStatus;
	@DatabaseField
	private String pName;
	@DatabaseField
	private String pPhone;
	@DatabaseField
	private String homeAddress;
	@DatabaseField
	private String remark;
	@DatabaseField
	private String rootDomain;
	@DatabaseField
	private String company;
	@DatabaseField
	private String idCard;
	@DatabaseField
	private String latestAddress;
	

	public String getLatestAddress() {
		return latestAddress;
	}

	public void setLatestAddress(String latestAddress) {
		this.latestAddress = latestAddress;
	}

	// private List<Equipment> loginEquipments;
	@ForeignCollectionField
	/** 
	 * ������Ҫע����ǣ���������ֻ����ForeignCollection<T>����Collection<T> 
	 * �����Ҫ�����أ��ӳټ��أ�������@ForeignCollectionField���ϲ���eager=false 
	 * �������Ҳ��˵��һ���û���Ӧ�Ŷ���豸
	 */
	private Collection<Equipment> loginEquipments;

	public User() {
		userType="";
		userNumber="";
		username="";
		name="";
		userImage="";
		
	}

	public User(JSONObject jo) {
		
		id = jo.optString("���");
		username = jo.optString("ѧ��");
		name = jo.optString("����");
		sClass = jo.optString("���");
		birthday = jo.optString("��������");
		gender = jo.optString("�Ա�");
		sPhone = jo.optString("ѧ���绰");
		sDormitory= jo.optString("��ͥסַ");
		homeAddress = jo.optString("��ͥסַ");
		idCard = jo.optString("���֤��");
		userNumber = jo.optString("�û�Ψһ��");
		sortNumber=jo.optString("������");
		userType = jo.optString("�û�����");
		loginStatus= jo.optString("�Ƿ�¼ȡ");
		loginTime= jo.optString("��ѧ���");
		sStatus= jo.optString("ѧ��״̬");
		userImage = jo.optString("��Ƭ");
		domain = jo.optString("רҵ����");
		rootDomain = jo.optString("Ժϵ����");
		userRating = jo.optString("�շѱ�׼");
		pName = jo.optString("��������");
		pPhone = jo.optString("���׵绰");
		department = jo.optString("��Դ����");
		remark = jo.optString("��ѧ��ʽ");
		checkCode= jo.optString("�û�������");
		
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public User(net.minidev.json.JSONObject jo) {
		
		id = String.valueOf(jo.get("���"));
		username = String.valueOf(jo.get("ѧ��"));
		name = String.valueOf(jo.get("����"));

		birthday = String.valueOf(jo.get("��������"));
		gender = String.valueOf(jo.get("�Ա�"));
		
		
		isModify = Integer.parseInt(String.valueOf(jo.get("�Ƿ��޸�")==null?"0":jo.get("�Ƿ��޸�")));
		allowModifyField = String.valueOf(jo.get("�����û��޸������ֶ��б�"));
		checkCode = String.valueOf(jo.get("�û�������"));
		userImage = String.valueOf(jo.get("�û�ͷ��"));
		virtualClass = String.valueOf(jo.get("����༶"));
		userNumber = String.valueOf(jo.get("�û�Ψһ��"));
		domain = String.valueOf(jo.get("����"));
		banLogin = String.valueOf(jo.get("��ֹ��¼"));
		sortNumber = String.valueOf(jo.get("�����"));
		mainRole = String.valueOf(jo.get("��Ҫ��ɫ"));
		secondaryRole = String.valueOf(jo.get("������ɫ"));
		userRating = String.valueOf(jo.get("�û�����"));
		certificationPath = String.valueOf(jo.get("��֤·��"));
		iosDeviceToken = String.valueOf(jo.get("IosDeviceToken"));
		recentlyUsedEquipment = String.valueOf(jo.get("���ʹ���豸"));
		loginEquipments = new ArrayList<Equipment>();
		net.minidev.json.JSONObject jole = (net.minidev.json.JSONObject)jo.get("�û�ʹ���豸");
		if (jole != null) {
			Set<String> keyset=jole.keySet();
			Iterator<String> keys = keyset.iterator();
			while (keys.hasNext()) {
				String str = keys.next();
				Equipment eq = new Equipment((net.minidev.json.JSONObject)jole.get(str));
				eq.setUser(this);
				loginEquipments.add(eq);
			}
		}

		companyName = String.valueOf(jo.get("��λ����"));
		company = String.valueOf(jo.get("��λ"));
		sClass = String.valueOf(jo.get("�༶"));
		sPhone = String.valueOf(jo.get("ѧ���绰"));
		sDormitory = String.valueOf(jo.get("ѧ������"));
		sEmail = String.valueOf(jo.get("ѧ������"));
		sStatus = String.valueOf(jo.get("ѧ��״̬"));
		pName = String.valueOf(jo.get("�ҳ�����"));
		pPhone = String.valueOf(jo.get("�ҳ��绰"));
		homeAddress = String.valueOf(jo.get("��ͥסַ"));
		remark = String.valueOf(jo.get("��ע"));
		rootDomain = String.valueOf(jo.get("������"));
		
	}
	/**
	 * ���
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
	 * �û���
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * ����
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
	 * �ǳ�
	 * 
	 * @return
	 */
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * �Ա�
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
	 * ��������
	 * 
	 * @return
	 */
	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * �ֻ�
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
	 * ����
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
	 * �����༶
	 * 
	 * @return
	 */
	public String getWithClass() {
		return withClass;
	}

	public void setWithClass(String withClass) {
		this.withClass = withClass;
	}

	/**
	 * �����γ�
	 * 
	 * @return
	 */
	public String getWithCourse() {
		return withCourse;
	}

	public void setWithCourse(String withCourse) {
		this.withCourse = withCourse;
	}

	/**
	 * ��λ����
	 * 
	 * @return
	 */
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * ��¼״̬
	 * 
	 * @return
	 */
	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	/**
	 * ��¼ʱ��
	 * 
	 * @return
	 */
	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	/**
	 * �Ƿ��޸�
	 * 
	 * @return
	 */
	public int getIsModify() {
		return isModify;
	}

	public void setIsModify(int isModify) {
		this.isModify = isModify;
	}

	/**
	 * �����޸��ֶ�
	 * 
	 * @return
	 */
	public String getAllowModifyField() {
		return allowModifyField;
	}

	public void setAllowModifyField(String allowModifyField) {
		this.allowModifyField = allowModifyField;
	}

	/**
	 * �û�У����
	 * 
	 * @return
	 */
	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	/**
	 * �û�ͷ��
	 * 
	 * @return
	 */
	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	/**
	 * ����༶
	 * 
	 * @return
	 */
	public String getVirtualClass() {
		return virtualClass;
	}

	public void setVirtualClass(String virtualClass) {
		this.virtualClass = virtualClass;
	}

	/**
	 * �û�Ψһ��
	 * 
	 * @return
	 */
	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * �û�����
	 * 
	 * @return
	 */
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * �û�ʹ���豸
	 * 
	 * @return
	 */
	public Collection<Equipment> getLoginEquipments() {
		return loginEquipments;
	}

	public void setLoginEquipments(List<Equipment> loginEquipments) {
		this.loginEquipments = loginEquipments;
	}

	/**
	 * �û��ʹ���豸
	 * 
	 * @return
	 */
	public String getRecentlyUsedEquipment() {
		return recentlyUsedEquipment;
	}

	public void setRecentlyUsedEquipment(String recentlyUsedEquipment) {
		this.recentlyUsedEquipment = recentlyUsedEquipment;
	}

	/**
	 * ��������:
	 * 
	 * @return
	 */
	public String getIosDeviceToken() {
		return iosDeviceToken;
	}

	public void setIosDeviceToken(String iosDeviceToken) {
		this.iosDeviceToken = iosDeviceToken;
	}

	/**
	 * ��֤·��
	 * 
	 * @return
	 */
	public String getCertificationPath() {
		return certificationPath;
	}

	public void setCertificationPath(String certificationPath) {
		this.certificationPath = certificationPath;
	}

	/**
	 * �û�����
	 * 
	 * @return
	 */
	public String getUserRating() {
		return userRating;
	}

	public void setUserRating(String userRating) {
		this.userRating = userRating;
	}

	/**
	 * ��Ҫ��ɫ
	 * 
	 * @return
	 */
	public String getMainRole() {
		return mainRole;
	}

	public void setMainRole(String mainRole) {
		this.mainRole = mainRole;
	}

	/**
	 * ������ɫ
	 * 
	 * @return
	 */
	public String getSecondaryRole() {
		return secondaryRole;
	}

	public void setSecondaryRole(String secondaryRole) {
		this.secondaryRole = secondaryRole;
	}

	/**
	 * �����
	 * 
	 * @return
	 */
	public String getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(String sortNumber) {
		this.sortNumber = sortNumber;
	}

	/**
	 * ��ֹ��¼
	 * 
	 * @return
	 */
	public String getBanLogin() {
		return banLogin;
	}

	public void setBanLogin(String banLogin) {
		this.banLogin = banLogin;
	}

	/**
	 * ����
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
	 * ѧ���༶
	 * 
	 * @return
	 */
	public String getsClass() {
		return sClass;
	}

	public void setsClass(String sClass) {
		this.sClass = sClass;
	}

	/**
	 * ѧ���绰
	 * 
	 * @return
	 */
	public String getsPhone() {
		return sPhone;
	}

	public void setsPhone(String sPhone) {
		this.sPhone = sPhone;
	}

	/**
	 * ѧ������
	 * 
	 * @return
	 */
	public String getsDormitory() {
		return sDormitory;
	}

	public void setsDormitory(String sDormitory) {
		this.sDormitory = sDormitory;
	}

	/**
	 * ѧ������
	 * 
	 * @return
	 */
	public String getsEmail() {
		return sEmail;
	}

	public void setsEmail(String sEmail) {
		this.sEmail = sEmail;
	}

	/**
	 * ѧ��״̬
	 * 
	 * @return
	 */
	public String getsStatus() {
		return sStatus;
	}

	public void setsStatus(String sStatus) {
		this.sStatus = sStatus;
	}

	/**
	 * �ҳ�����
	 * 
	 * @return
	 */
	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	/**
	 * �ҳ��绰
	 * 
	 * @return
	 */
	public String getpPhone() {
		return pPhone;
	}

	public void setpPhone(String pPhone) {
		this.pPhone = pPhone;
	}

	/**
	 * ��ͥסַ
	 * 
	 * @return
	 */
	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	/**
	 * ��ע
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
	 * ������
	 * 
	 * @return
	 */
	public String getRootDomain() {
		return rootDomain;
	}

	public void setRootDomain(String rootDomain) {
		this.rootDomain = rootDomain;
	}

	/**
	 * ��λ
	 * 
	 * @return
	 */
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}
