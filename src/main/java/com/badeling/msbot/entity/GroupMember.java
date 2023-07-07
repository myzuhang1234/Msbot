package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class GroupMember {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String group_id;
    private String user_id;
    private String nickname;
    private String card;
    private String join_time;
    private String leave_time;

    public GroupMember() {

    }



    @Override
    public String toString() {
        return "GroupMember [group_id=" + group_id + ", user_id=" + user_id + ", nickname=" + nickname + ", card="
                + card + ", join_time=" + join_time + ", leave_time=" + leave_time + "]";
    }



    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getGroup_id() {
        return group_id;
    }
    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getCard() {
        return card;
    }
    public void setCard(String card) {
        this.card = card;
    }
    public String getJoin_time() {
        return join_time;
    }
    public void setJoin_time(String join_time) {
        this.join_time = join_time;
    }
    public String getLeave_time() {
        return leave_time;
    }
    public void setLeave_time(String leave_time) {
        this.leave_time = leave_time;
    }


}