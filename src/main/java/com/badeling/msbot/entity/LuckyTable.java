package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LuckyTable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String luckyStar;
	private String LuckyTable;
	private String LuckyThing;
	public LuckyTable() {
		
	}
	
	

	public LuckyTable(Long id, String luckyStar, String luckyTable, String luckyThing) {
		super();
		this.id = id;
		this.luckyStar = luckyStar;
		LuckyTable = luckyTable;
		LuckyThing = luckyThing;
	}



	@Override
	public String toString() {
		return "LuckyTable [id=" + id + ", luckyStar=" + luckyStar + ", LuckyTable=" + LuckyTable + ", LuckyThing="
				+ LuckyThing + "]";
	}



	public String getLuckyThing() {
		return LuckyThing;
	}



	public void setLuckyThing(String luckyThing) {
		LuckyThing = luckyThing;
	}



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLuckyStar() {
		return luckyStar;
	}
	public void setLuckyStar(String luckyStar) {
		this.luckyStar = luckyStar;
	}
	public String getLuckyTable() {
		return LuckyTable;
	}
	public void setLuckyTable(String luckyTable) {
		LuckyTable = luckyTable;
	}
	
	
	
	
}
