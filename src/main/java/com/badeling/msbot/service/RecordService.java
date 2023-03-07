package com.badeling.msbot.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface RecordService {
    String sendRecordMsg(String receiveMsg) throws IOException;

}
