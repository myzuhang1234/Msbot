package com.badeling.msbot.controller;

import java.util.TreeSet;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.badeling.msbot.domain.GlobalVariable;

@Component
public class ApplicationRunnerImpl implements ApplicationRunner{
//public class ApplicationRunnerImpl{
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		TreeSet<String> msgList = new TreeSet<>();
		GlobalVariable.setMsgList(msgList);
		System.out.println("初始程序执行完毕");
	}

}
