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

    private Integer  prize_1;

    private Integer  prize_2;

    private Integer  prize_3;

    private Integer  prize_4;

    private Integer  prize_5;



    public MonvTime() {

    }

    @Override
    public String toString() {
        return "MonvTime [id=" + id + ", user_id=" + user_id + ", name=" + name + ", group_id=" + group_id
                + ", updated_at=" + updated_at +
                ", prize_1=" + prize_1 + ", prize_2=" + prize_2 + ", prize_3=" + prize_3 +
                ", prize_4=" + prize_4 +", prize_5=" + prize_5 +"]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrize(Integer prize_1,Integer prize_2,Integer prize_3,Integer prize_4,Integer prize_5){
        this.prize_1 = prize_1;
        this.prize_2 = prize_2;
        this.prize_3 = prize_3;
        this.prize_4 = prize_4;
        this.prize_5 = prize_5;
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

    public Integer getPrize_1() {
        return prize_1;
    }

    public Integer getPrize_2() {
        return prize_2;
    }

    public Integer getPrize_3() {
        return prize_3;
    }

    public Integer getPrize_4() {
        return prize_4;
    }

    public Integer getPrize_5() {
        return prize_5;
    }

    public void setPrize_1(Integer prize_1) {
        this.prize_1 = prize_1;
    }

    public void setPrize_2(Integer prize_2) {
        this.prize_2 = prize_2;
    }

    public void setPrize_3(Integer prize_3) {
        this.prize_3 = prize_3;
    }

    public void setPrize_4(Integer prize_4) {
        this.prize_4 = prize_4;
    }

    public void setPrize_5(Integer prize_5) {
        this.prize_5 = prize_5;
    }
}
