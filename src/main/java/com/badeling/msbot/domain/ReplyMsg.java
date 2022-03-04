package com.badeling.msbot.domain;

public class ReplyMsg {
    /**
     * 要回复的内容
     */
    private String reply;

    /**
     * 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 reply 字段是字符串时有效
     * 默认不转义
     */
    private boolean auto_escape;

    /**
     * 是否要在回复开头 at 发送者（自动添加），发送者是匿名用户时无效
     * 默认at发送者
     */
    private boolean at_sender;

    /**
     * 撤回该条消息
     * 默认不撤回
     */
    private boolean delete;

    /**
     * 把发送者踢出群组（需要登录号权限足够），不拒绝此人后续加群请求，发送者是匿名用户时无效
     * 默认不踢
     */
    private boolean kick;

    /**
     * 把发送者禁言 ban_duration 指定时长，对匿名用户也有效
     * 默认不禁言
     */
    private boolean ban;

    /**
     * 禁言时长
     * 默认30分钟
     */
    private Integer ban_duration;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public boolean isAuto_escape() {
        return auto_escape;
    }

    public void setAuto_escape(boolean auto_escape) {
        this.auto_escape = auto_escape;
    }

    public boolean isAt_sender() {
        return at_sender;
    }

    public void setAt_sender(boolean at_sender) {
        this.at_sender = at_sender;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isKick() {
        return kick;
    }

    public void setKick(boolean kick) {
        this.kick = kick;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }

    public Integer getBan_duration() {
        return ban_duration;
    }

    public void setBan_duration(Integer ban_duration) {
        this.ban_duration = ban_duration;
    }

    @Override
    public String toString() {
        return "ReplyMsg{" +
                "reply='" + reply + '\'' +
                ", auto_escape=" + auto_escape +
                ", at_sender=" + at_sender +
                ", delete=" + delete +
                ", kick=" + kick +
                ", ban=" + ban +
                ", ban_duration=" + ban_duration +
                '}';
    }
}
