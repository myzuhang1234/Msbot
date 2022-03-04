package com.badeling.msbot.domain;

public class PrivateMsg {
	/**
     * QQ号
     */
    private Long user_id;

    /**
     * 消息
     */
    private String message;

    /**
     * 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 message 字段是字符串时有效
     */
    private boolean auto_escape;

    
    public PrivateMsg() {
    	
    }
  
	public PrivateMsg(Long user_id, String message, boolean auto_escape) {
		super();
		this.user_id = user_id;
		this.message = message;
		this.auto_escape = auto_escape;
	}

	@Override
	public String toString() {
		return "PrivateMsg [user_id=" + user_id + ", message=" + message + ", auto_escape=" + auto_escape + "]";
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isAuto_escape() {
		return auto_escape;
	}

	public void setAuto_escape(boolean auto_escape) {
		this.auto_escape = auto_escape;
	}
    
    
    
    
}
