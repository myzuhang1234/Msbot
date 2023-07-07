package com.badeling.msbot.service;

import org.springframework.stereotype.Service;

@Service
public interface WzXmlService {

    void updateMobInfo();

    String searchMob(String raw_message, String group_id, String user_id);

    String searchMob(Long mob_id, String group_id);

    String searchMobForChannel(Long mob_id, Long channel_id, Long guild_id);

    String searchMobForChannel(String raw_message, Long channel_id, Long guild_id);


}