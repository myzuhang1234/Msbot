package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MobName {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String mobId;
    private String name;
    public MobName() {
    }
    public MobName(Long id, String mobId, String name) {
        super();
        this.id = id;
        this.mobId = mobId;
        this.name = name;
    }


    @Override
    public String toString() {
        return "MobName [id=" + id + ", mobId=" + mobId + ", name=" + name + "]";
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMobId() {
        return mobId;
    }
    public void setMobId(String mobId) {
        this.mobId = mobId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



}