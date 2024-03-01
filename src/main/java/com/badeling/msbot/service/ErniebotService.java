package com.badeling.msbot.service;

import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public interface ErniebotService {
    String sendErnieBotMsg(String receiveMsg) throws IOException;
}
