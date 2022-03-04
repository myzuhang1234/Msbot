package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LuckyMap {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String map;
	private String mapUrl;
	public LuckyMap() {
		
	}
	
	public LuckyMap(Long id, String map, String mapUrl) {
		super();
		this.id = id;
		this.map = map;
		this.mapUrl = mapUrl;
	}

	
	@Override
	public String toString() {
		return "LuckyMap [id=" + id + ", map=" + map + ", mapUrl=" + mapUrl + "]";
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getMap() {
		return map;
	}


	public void setMap(String map) {
		this.map = map;
	}


	public String getMapUrl() {
		return mapUrl;
	}


	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}
	
}
