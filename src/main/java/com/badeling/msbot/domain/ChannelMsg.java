package com.badeling.msbot.domain;

public class ChannelMsg {
	String post_type;
	String message_type;
	String sub_type;
	Long guild_id;
	Long channel_id;
	Long user_id;
	String message_id;
	Sender sender;
	String message;
	Long self_id;
	Long self_tiny_id;
	String font;
	String time;
	
	public ChannelMsg(){
		
	}
	
	public ChannelMsg(String post_type, String message_type, String sub_type, Long guild_id, Long channel_id,
			Long user_id, String message_id, Sender sender, String message, Long self_id, Long self_tiny_id,
			String font, String time) {
		super();
		this.post_type = post_type;
		this.message_type = message_type;
		this.sub_type = sub_type;
		this.guild_id = guild_id;
		this.channel_id = channel_id;
		this.user_id = user_id;
		this.message_id = message_id;
		this.sender = sender;
		this.message = message;
		this.self_id = self_id;
		this.self_tiny_id = self_tiny_id;
		this.font = font;
		this.time = time;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
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
	public String getMessage_type() {
		return message_type;
	}
	public void setMessage_type(String message_type) {
		this.message_type = message_type;
	}
	public String getSub_type() {
		return sub_type;
	}
	public void setSub_type(String sub_type) {
		this.sub_type = sub_type;
	}
	public Long getGuild_id() {
		return guild_id;
	}
	public void setGuild_id(Long guild_id) {
		this.guild_id = guild_id;
	}
	public Long getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(Long channel_id) {
		this.channel_id = channel_id;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getMessage_id() {
		return message_id;
	}
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}
	public Sender getSender() {
		return sender;
	}
	public void setSender(Sender sender) {
		this.sender = sender;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getSelf_id() {
		return self_id;
	}
	public void setSelf_id(Long self_id) {
		this.self_id = self_id;
	}
	public Long getSelf_tiny_id() {
		return self_tiny_id;
	}
	public void setSelf_tiny_id(Long self_tiny_id) {
		this.self_tiny_id = self_tiny_id;
	}
	
	
	
}
