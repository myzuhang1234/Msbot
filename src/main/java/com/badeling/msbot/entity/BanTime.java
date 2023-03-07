package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class BanTime {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String user_id;
    private String name;
    private String group_id;
    private Timestamp updated_at;
    private Integer  ban_times;
    private  String date;

    public BanTime() {

    }

    public String toString() {
        return "BanTime [id=" + id + ", user_id=" + user_id + ", name=" + name + ", group_id=" + group_id
                + ", updated_at=" + updated_at + ", ban_times=" + ban_times +"]";
    }

    public Long getId() {
        return id;
    }

    public void setBan_times(Integer ban_times){
        this.ban_times = ban_times;
    }

    public Integer getBan_times(){
        return ban_times;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Timestamp getUpdateTime() {
        return updated_at;
    }
    public void setUpdateTime(Timestamp updated_at) {
        this.updated_at = updated_at;
    }
    public void setDate(String date) {
        this.date = date;
    }

}
