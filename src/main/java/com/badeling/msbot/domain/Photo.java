package com.badeling.msbot.domain;

public class Photo {
	private String id;
	private String name;
	private String type;
	private String rarity;
	private String head;
	private String level;
	public Photo() {
		
	}
	
	
	
	


	public Photo(String id, String name, String type, String rarity, String head, String level) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.rarity = rarity;
		this.head = head;
		this.level = level;
	}






	@Override
	public String toString() {
		return "Photo [id=" + id + ", name=" + name + ", type=" + type + ", rarity=" + rarity + ", head=" + head
				+ ", level=" + level + "]";
	}
	


	public String getHead() {
		return head;
	}



	public void setHead(String head) {
		this.head = head;
	}



	public String getLevel() {
		return level;
	}



	public void setLevel(String level) {
		this.level = level;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRarity() {
		return rarity;
	}
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
}
