package com.badeling.msbot.domain;

public class Sender {
	private String age;
	private String area;
	private String card;
	private String level;
	private String nickname;
	private String role;
	private String sex;
	private String title;
	private String user_id;
	private String tiny_id;
	public Sender() {
		
	}
	
	

	@Override
	public String toString() {
		return "Sender [age=" + age + ", area=" + area + ", card=" + card + ", level=" + level + ", nickname="
				+ nickname + ", role=" + role + ", sex=" + sex + ", title=" + title + ", user_id=" + user_id
				+ ", tiny_id=" + tiny_id + "]";
	}

	
	public String getTiny_id() {
		return tiny_id;
	}



	public void setTiny_id(String tiny_id) {
		this.tiny_id = tiny_id;
	}



	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
}
