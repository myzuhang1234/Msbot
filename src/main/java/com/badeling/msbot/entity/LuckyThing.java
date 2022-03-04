package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LuckyThing {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String good;
	private String goodThing;
	private String bad;
	private String badThing;
	public LuckyThing() {
		
	}
	public LuckyThing(Long id, String good, String goodThing, String bad, String badThing) {
		super();
		this.id = id;
		this.good = good;
		this.goodThing = goodThing;
		this.bad = bad;
		this.badThing = badThing;
	}
	@Override
	public String toString() {
		return "LuckyThing [id=" + id + ", good=" + good + ", goodThing=" + goodThing + ", bad=" + bad + ", badThing="
				+ badThing + "]";
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGood() {
		return good;
	}
	public void setGood(String good) {
		this.good = good;
	}
	public String getGoodThing() {
		return goodThing;
	}
	public void setGoodThing(String goodThing) {
		this.goodThing = goodThing;
	}
	public String getBad() {
		return bad;
	}
	public void setBad(String bad) {
		this.bad = bad;
	}
	public String getBadThing() {
		return badThing;
	}
	public void setBadThing(String badThing) {
		this.badThing = badThing;
	}
	
	
	
	
}
