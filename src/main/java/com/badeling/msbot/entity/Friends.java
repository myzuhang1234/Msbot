package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Friends {
	@Id
	private String id;
	private String name;
	private String atr;
	private String star;
	private String starBlank;
	
	public Friends() {
		
	}
	
	public Friends(String id, String name, String atr, String star, String starBlank) {
		super();
		this.id = id;
		this.name = name;
		this.atr = atr;
		this.star = star;
		this.starBlank = starBlank;
	}

	@Override
	public String toString() {
		return "Friends [id=" + id + ", name=" + name + ", atr=" + atr + ", star=" + star + ", starBlank=" + starBlank
				+ "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAtr() {
		return atr;
	}

	public void setAtr(String atr) {
		this.atr = atr;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getStarBlank() {
		return starBlank;
	}

	public void setStarBlank(String starBlank) {
		this.starBlank = starBlank;
	}
	
	
	
	
	
	
	
}
