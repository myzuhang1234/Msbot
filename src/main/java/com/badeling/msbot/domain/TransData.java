package com.badeling.msbot.domain;

public class TransData {
	//翻译接受实体
	/**
     * 原文
     */
    private String src;
    /**
     * 译文
     */
    private String dst;
    
    public TransData() {
    	
    }
    
	public TransData(String src, String dst) {
		super();
		this.src = src;
		this.dst = dst;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
    
    
    
    
	
	
}
