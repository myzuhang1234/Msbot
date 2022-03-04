package com.badeling.msbot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MobInfo {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String mobId;
	private String level;
	private String maxHp;
	private String maxMp;
	private String speed;
	private String paDamage;
	private String maDamage;
	private String pdRate;
	private String mdRate;
	private String acc;
	private String eva;
	private String pushed;
	private String fs;
	private String exp;
	private String summerType;
	private String category;
	private String elemAttr;
	private String mobType;
	private String boss;
	private String link;
	
	@Override
	public String toString() {
		return "MobInfo [id=" + id + ", mobId=" + mobId + ", level=" + level + ", maxHp=" + maxHp + ", maxMp=" + maxMp
				+ ", speed=" + speed + ", paDamage=" + paDamage + ", maDamage=" + maDamage + ", pdRate=" + pdRate
				+ ", mdRate=" + mdRate + ", acc=" + acc + ", eva=" + eva + ", pushed=" + pushed + ", fs=" + fs
				+ ", exp=" + exp + ", summerType=" + summerType + ", category=" + category + ", elemAttr=" + elemAttr
				+ ", mobType=" + mobType + ", boss=" + boss + ", link=" + link + "]";
	}
	public MobInfo() {
		
	}
	public MobInfo(Long id, String mobId, String level, String maxHp, String maxMp, String speed, String paDamage,
			String maDamage, String pdRate, String mdRate, String acc, String eva, String pushed, String fs, String exp,
			String summerType, String category, String elemAttr, String mobType, String boss, String link) {
		super();
		this.id = id;
		this.mobId = mobId;
		this.level = level;
		this.maxHp = maxHp;
		this.maxMp = maxMp;
		this.speed = speed;
		this.paDamage = paDamage;
		this.maDamage = maDamage;
		this.pdRate = pdRate;
		this.mdRate = mdRate;
		this.acc = acc;
		this.eva = eva;
		this.pushed = pushed;
		this.fs = fs;
		this.exp = exp;
		this.summerType = summerType;
		this.category = category;
		this.elemAttr = elemAttr;
		this.mobType = mobType;
		this.boss = boss;
		this.link = link;
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
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getMaxHp() {
		return maxHp;
	}
	public void setMaxHp(String maxHp) {
		this.maxHp = maxHp;
	}
	public String getMaxMp() {
		return maxMp;
	}
	public void setMaxMp(String maxMp) {
		this.maxMp = maxMp;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getPaDamage() {
		return paDamage;
	}
	public void setPaDamage(String paDamage) {
		this.paDamage = paDamage;
	}
	public String getMaDamage() {
		return maDamage;
	}
	public void setMaDamage(String maDamage) {
		this.maDamage = maDamage;
	}
	public String getPdRate() {
		return pdRate;
	}
	public void setPdRate(String pdRate) {
		this.pdRate = pdRate;
	}
	public String getMdRate() {
		return mdRate;
	}
	public void setMdRate(String mdRate) {
		this.mdRate = mdRate;
	}
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public String getEva() {
		return eva;
	}
	public void setEva(String eva) {
		this.eva = eva;
	}
	public String getPushed() {
		return pushed;
	}
	public void setPushed(String pushed) {
		this.pushed = pushed;
	}
	public String getFs() {
		return fs;
	}
	public void setFs(String fs) {
		this.fs = fs;
	}
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	public String getSummerType() {
		return summerType;
	}
	public void setSummerType(String summerType) {
		this.summerType = summerType;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getElemAttr() {
		return elemAttr;
	}
	public void setElemAttr(String elemAttr) {
		this.elemAttr = elemAttr;
	}
	public String getMobType() {
		return mobType;
	}
	public void setMobType(String mobType) {
		this.mobType = mobType;
	}
	public String getBoss() {
		return boss;
	}
	public void setBoss(String boss) {
		this.boss = boss;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	
	
	
}
