package com.badeling.msbot.domain;

public class ChannelReplyMsg {
	Long guild_id;
	Long channel_id;
	String message;
	public ChannelReplyMsg() {
		
	}
	public ChannelReplyMsg(Long guild_id, Long channel_id, String message) {
		super();
		this.guild_id = guild_id;
		this.channel_id = channel_id;
		this.message = message;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
