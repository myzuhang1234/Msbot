package com.badeling.msbot.domain;

public class NoticeMsg {
//{"post_type":"notice","notice_type":"group_increase"}
	String type;
	String self_id;
	String sub_type;
	String group_id;
	String operator_id;
	String user_id;
	String time;
	String post_type;
	String notice_type;
	String duration;
	public NoticeMsg() {
		
	}
	
	@Override
	public String toString() {
		return "NoticeMsg [type=" + type + ", self_id=" + self_id + ", sub_type=" + sub_type + ", group_id=" + group_id
				+ ", operator_id=" + operator_id + ", user_id=" + user_id + ", time=" + time + ", post_type="
				+ post_type + ", notice_type=" + notice_type + "]";
	}
	
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSelf_id() {
		return self_id;
	}
	public void setSelf_id(String self_id) {
		this.self_id = self_id;
	}
	public String getSub_type() {
		return sub_type;
	}
	public void setSub_type(String sub_type) {
		this.sub_type = sub_type;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPost_type() {
		return post_type;
	}
	public void setPost_type(String post_type) {
		this.post_type = post_type;
	}
	public String getNotice_type() {
		return notice_type;
	}
	public void setNotice_type(String notice_type) {
		this.notice_type = notice_type;
	}
	
	
}
