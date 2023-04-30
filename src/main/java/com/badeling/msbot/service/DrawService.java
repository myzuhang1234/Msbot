package com.badeling.msbot.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;



@Service
public interface DrawService {

	String startDrawMs() throws Exception;

	String kemomimiDraw() throws Exception;

	String throwSomeone(String headImg) throws Exception;

	String pouchSomeone(String saveTempImage) throws Exception;

	String testFont(String substring) throws Exception;

	String ignImage(String ignDate) throws Exception;

	String zbImage(String[] msg) throws Exception;

	String drawRankImage(Map<String, Object> mapler) throws Exception;

	String getRankList(List<String> richList, List<String> poorList) throws Exception;
}
