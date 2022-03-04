package com.badeling.msbot.domain;


public class ReceiveMsg {
	
	String self_id;
	String sub_type;
	String message_id;
	String message_seq;
	String group_id;
	String user_id;
	String anonymous;
	String message;
	String raw_message;
	String font;
	String time;
	String message_type;
	String post_type;
	Sender sender;
	public ReceiveMsg() {
		
	}
	public ReceiveMsg(String self_id, String sub_type, String message_id, String message_seq, String group_id,
			String user_id, String anonymous, String message, String raw_message, String font, String time,
			String message_type, String post_type, Sender sender) {
		super();
		this.self_id = self_id;
		this.sub_type = sub_type;
		this.message_id = message_id;
		this.message_seq = message_seq;
		this.group_id = group_id;
		this.user_id = user_id;
		this.anonymous = anonymous;
		this.message = message;
		this.raw_message = raw_message;
		this.font = font;
		this.time = time;
		this.message_type = message_type;
		this.post_type = post_type;
		this.sender = sender;
	}
	
	@Override
	public String toString() {
		return "ReceiveMsg [self_id=" + self_id + ", sub_type=" + sub_type + ", message_id=" + message_id
				+ ", message_seq=" + message_seq + ", group_id=" + group_id + ", user_id=" + user_id + ", anonymous="
				+ anonymous + ", message=" + message + ", raw_message=" + raw_message + ", font=" + font + ", time="
				+ time + ", message_type=" + message_type + ", post_type=" + post_type + ", sender=" + sender + "]";
	}
	public String getMessage_seq() {
		return message_seq;
	}
	public void setMessage_seq(String message_seq) {
		this.message_seq = message_seq;
	}
	public String getPost_type() {
		return post_type;
	}
	public void setPost_type(String post_type) {
		this.post_type = post_type;
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
	public String getMessage_id() {
		return message_id;
	}
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getAnonymous() {
		return anonymous;
	}
	public void setAnonymous(String anonymous) {
		this.anonymous = anonymous;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRaw_message() {
		return raw_message;
	}
	public void setRaw_message(String raw_message) {
		this.raw_message = raw_message;
	}
	public String getFont() {
		return font;
	}
	public void setFont(String font) {
		this.font = font;
	}
	public Sender getSender() {
		return sender;
	}
	public void setSender(Sender sender) {
		this.sender = sender;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getMessage_type() {
		return message_type;
	}
	public void setMessage_type(String message_type) {
		this.message_type = message_type;
	}
	
}

