package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GroupInfo {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    //群号
    private String group_id;
    //群名称
    private String group_name;
    //群备注
    private String group_memo;
    //迎新语句
    private String welcome;
    public GroupInfo() {

    }
    public GroupInfo(Long id, String group_id, String group_name, String group_memo, String welcome) {
        super();
        this.id = id;
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_memo = group_memo;
        this.welcome = welcome;
    }
    @Override
    public String toString() {
        return "GroupInfo [id=" + id + ", group_id=" + group_id + ", group_name=" + group_name + ", group_memo="
                + group_memo + ", welcome=" + welcome + "]";
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
    public String getGroup_name() {
        return group_name;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
    public String getGroup_memo() {
        return group_memo;
    }
    public void setGroup_memo(String group_memo) {
        this.group_memo = group_memo;
    }
    public String getWelcome() {
        return welcome;
    }
    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }


}