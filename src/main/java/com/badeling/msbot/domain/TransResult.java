package com.badeling.msbot.domain;

import java.util.List;

public class TransResult {
	/**
     *翻译源语言
     */
    private String from;
    /**
     *译文语言
     */
    private String to;
    /**
     *翻译结果
     */
    private List<TransData> trans_result;
    
    public TransResult() {
    	
    }
    
	public TransResult(String from, String to, List<TransData> trans_result) {
		super();
		this.from = from;
		this.to = to;
		this.trans_result = trans_result;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public List<TransData> getTrans_result() {
		return trans_result;
	}

	public void setTrans_result(List<TransData> trans_result) {
		this.trans_result = trans_result;
	}
    
    
    
    
    
}
