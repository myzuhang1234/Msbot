package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class MonvTime {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String user_id;
    private String name;
    private String group_id;

    private Timestamp updated_at;

    public MonvTime() {

    }

    @Override
    public String toString() {
        return "MonvTime [id=" + id + ", user_id=" + user_id + ", name=" + name + ", group_id=" + group_id
                + ", updated_at=" + updated_at + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public Timestamp getUpdateTime() {
        return updated_at;
    }

    public void setUpdateTime(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

}
