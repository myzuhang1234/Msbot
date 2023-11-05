package com.badeling.msbot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MsgDeleted {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String question;
	@Column(name="answer",columnDefinition="VARCHAR(1275)")
	private String answer;
	private String deletedId;
	@Column(name="link",columnDefinition="VARCHAR(1275)")
	private String link;
	
	public MsgDeleted() {
		
	}
		
	public MsgDeleted(Long id, String question, String answer, String deletedId, String link) {
		super();
		this.id = id;
		this.question = question;
		this.answer = answer;
		this.deletedId = deletedId;
		this.link = link;
	}

	@Override
	public String toString() {
		return "MsgDeleted [id=" + id + ", question=" + question + ", answer=" + answer + ", deletedId=" + deletedId
				+ ", link=" + link + "]";
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

	public String getDeletedId() {
		return deletedId;
	}

	public void setDeletedId(String deletedId) {
		this.deletedId = deletedId;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}	
}
