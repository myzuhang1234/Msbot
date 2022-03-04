package com.badeling.msbot.domain;


public class ReReadMsg {
	//消息内容
	public String raw_message;
	//准备复读次数
	public int re_count;
	//已经复读次数
	public int count;
	
	//累计消息
	public int mes_count;
	//发起的人
	public String start_id;
	//复读的人
	public String reread_id;
	
	public ReReadMsg() {
		
	}

	

	public ReReadMsg(String raw_message, int re_count, int count, int mes_count, String start_id, String reread_id) {
		super();
		this.raw_message = raw_message;
		this.re_count = re_count;
		this.count = count;
		this.mes_count = mes_count;
		this.start_id = start_id;
		this.reread_id = reread_id;
	}

	@Override
	public String toString() {
		return "ReReadMsg [raw_message=" + raw_message + ", re_count=" + re_count + ", count=" + count + ", mes_count="
				+ mes_count + ", start_id=" + start_id + ", reread_id=" + reread_id + "]";
	}


	
	public String getReread_id() {
		return reread_id;
	}



	public void setReread_id(String reread_id) {
		this.reread_id = reread_id;
	}



	public int getMes_count() {
		return mes_count;
	}

	

	public String getStart_id() {
		return start_id;
	}

	public void setStart_id(String start_id) {
		this.start_id = start_id;
	}

	public void setMes_count(int mes_count) {
		this.mes_count = mes_count;
	}



	public String getRaw_message() {
		return raw_message;
	}

	public void setRaw_message(String raw_message) {
		this.raw_message = raw_message;
	}

	public int getRe_count() {
		return re_count;
	}

	public void setRe_count(int re_count) {
		this.re_count = re_count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	

	
	
	
}
