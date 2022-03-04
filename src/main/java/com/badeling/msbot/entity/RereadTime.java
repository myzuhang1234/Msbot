package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RereadTime {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	//复读的人
	private String user_id;
	//复读的群
	private String group_id;
	//复读的次数
	private int count;
	public RereadTime() {
		
	}
	public RereadTime(Long id, String user_id, String group_id, int count) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.group_id = group_id;
		this.count = count;
	}
	@Override
	public String toString() {
		return "RereadTime [id=" + id + ", user_id=" + user_id + ", group_id=" + group_id + ", count=" + count + "]";
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
