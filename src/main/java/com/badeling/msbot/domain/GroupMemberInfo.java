package com.badeling.msbot.domain;

public class GroupMemberInfo {
	Long group_id;
	Long user_id;
	String nickname;
	String card;
	String sex;
	Long age;
	String area;
	Long join_time;
	Long last_sent_time;
	String level;
	String role;
	boolean unfriendly;
	String title;
	Long title_expire_time;
	boolean card_changeable;
	public GroupMemberInfo(){
		
	}		
	@Override
	public String toString() {
		return "GroupMemberInfo [group_id=" + group_id + ", user_id=" + user_id + ", nickname=" + nickname + ", card="
				+ card + ", sex=" + sex + ", age=" + age + ", area=" + area + ", join_time=" + join_time
				+ ", last_sent_time=" + last_sent_time + ", level=" + level + ", role=" + role + ", unfriendly="
				+ unfriendly + ", title=" + title + ", title_expire_time=" + title_expire_time + ", card_changeable="
				+ card_changeable + "]";
	}
	public Long getGroup_id() {
		return group_id;
	}
	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Long getAge() {
		return age;
	}
	public void setAge(Long age) {
		this.age = age;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Long getJoin_time() {
		return join_time;
	}
	public void setJoin_time(Long join_time) {
		this.join_time = join_time;
	}
	public Long getLast_sent_time() {
		return last_sent_time;
	}
	public void setLast_sent_time(Long last_sent_time) {
		this.last_sent_time = last_sent_time;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isUnfriendly() {
		return unfriendly;
	}
	public void setUnfriendly(boolean unfriendly) {
		this.unfriendly = unfriendly;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getTitle_expire_time() {
		return title_expire_time;
	}
	public void setTitle_expire_time(Long title_expire_time) {
		this.title_expire_time = title_expire_time;
	}
	public boolean isCard_changeable() {
		return card_changeable;
	}
	public void setCard_changeable(boolean card_changeable) {
		this.card_changeable = card_changeable;
	}
	
	
}
