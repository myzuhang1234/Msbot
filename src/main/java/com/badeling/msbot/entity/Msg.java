package com.badeling.msbot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Msg {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String question;
	@Column(name="answer",columnDefinition="VARCHAR(1275)")
	private String answer;
	private String createId;
	@Column(name="link",columnDefinition="VARCHAR(1275)")
	private String link;
	public Msg() {
		
	}
	
	public Msg(Long id, String question, String answer, String createId, String link) {
		super();
		this.id = id;
		this.question = question;
		this.answer = answer;
		this.createId = createId;
		this.link = link;
	}
	
	public String getCreateId() {
		return createId;
	}
	public void setCreateId(String createId) {
		this.createId = createId;
	}
	public String getLink() {
		return link;
	}


	public void setLink(String link) {
		this.link = link;
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	
	
	
}
