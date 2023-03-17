package com.badeling.msbot.service;

import org.springframework.stereotype.Service;

@Service
public interface BanService {
    String getCheckResult(String word);

    String getCheckResultImage(String word);

}
