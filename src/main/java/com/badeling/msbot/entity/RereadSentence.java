package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RereadSentence {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	//复读的信息
	private String message;
	//发起人
	private String user_id;
	//复读的次数
	private int ReadTime;
	//复读的群
	private String group_id;
	public RereadSentence(){
		
	}
	
	public RereadSentence(Long id, String message, String user_id, int readTime, String group_id) {
		super();
		this.id = id;
		this.message = message;
		this.user_id = user_id;
		ReadTime = readTime;
		this.group_id = group_id;
	}

	@Override
	public String toString() {
		return "RereadSentence [id=" + id + ", message=" + message + ", user_id=" + user_id + ", ReadTime=" + ReadTime
				+ ", group_id=" + group_id + "]";
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public int getReadTime() {
		return ReadTime;
	}
	public void setReadTime(int readTime) {
		ReadTime = readTime;
	}
	
	
	
}
