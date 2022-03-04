package com.badeling.msbot.entity;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@Entity
public class QuizOzAnswer {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String answer;
	@ManyToOne(targetEntity = QuizOzQuestion.class,fetch = FetchType.EAGER)
	@JoinColumn(name="answer_id")
	private QuizOzQuestion quizOzQuestion;
	public QuizOzAnswer() {
		
	}
	public QuizOzAnswer(Long id, String answer, QuizOzQuestion quizOzQuestion) {
		super();
		this.id = id;
		this.answer = answer;
		this.quizOzQuestion = quizOzQuestion;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public QuizOzQuestion getQuizOzQuestion() {
		return quizOzQuestion;
	}
	public void setQuizOzQuestion(QuizOzQuestion quizOzQuestion) {
		this.quizOzQuestion = quizOzQuestion;
	}
	
}
