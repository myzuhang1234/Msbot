package com.badeling.msbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.QuizOzQuestion;

public interface QuizOzQuestionRepository extends CrudRepository<QuizOzQuestion, Long>{
	@Query(value = "select * from quiz_oz_question",nativeQuery=true)
	List<QuizOzQuestion> findAllQoz();

}
