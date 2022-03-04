package com.badeling.msbot.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
@Entity
public class QuizOzQuestion {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String question;
	@OneToMany(targetEntity = QuizOzAnswer.class,fetch = FetchType.EAGER)
	@JoinColumn(name="answer_id")
	private Set<QuizOzAnswer> answers = new HashSet<>();
	public QuizOzQuestion() {
		
	}
	public QuizOzQuestion(Long id, String question, Set<QuizOzAnswer> answers) {
		super();
		this.id = id;
		this.question = question;
		this.answers = answers;
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
	public Set<QuizOzAnswer> getAnswers() {
		return answers;
	}
	public void setAnswers(Set<QuizOzAnswer> answers) {
		this.answers = answers;
	}
	
}
