package com.badeling.msbot.service;

import com.badeling.msbot.entity.MonvTime;
import org.springframework.stereotype.Service;



@Service
public interface DrawService {

	String updateFriends();

	String updatePhoto();

	String startDraw() throws Exception;

	String startDrawMs() throws Exception;

	String startDrawMs(MonvTime monvTime) throws Exception;


	String kemomimiDraw() throws Exception;

	String throwSomeone(String headImg) throws Exception;

	String pouchSomeone(String saveTempImage) throws Exception;

	String testFont(String substring) throws Exception;

	String ignImage(String ignDate) throws Exception;

	String zbImage(String[] msg) throws Exception;
}
