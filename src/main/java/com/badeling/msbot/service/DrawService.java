package com.badeling.msbot.service;

import org.springframework.stereotype.Service;



@Service
public interface DrawService {

	String updateFriends();

	String updatePhoto();

	String startDraw() throws Exception;

	String kemomimiDraw() throws Exception;

	String throwSomeone(String headImg) throws Exception;

	String pouchSomeone(String saveTempImage) throws Exception;

	String testFont(String substring) throws Exception;

	String ignImage(String ignDate) throws Exception;


}
