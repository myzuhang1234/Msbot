package com.badeling.msbot.domain;

public class GroupMsg {
    /**
     * 群号
     */
    private Long group_id;

    /**
     * 消息
     */
    private String message;

    /**
     * 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 message 字段是字符串时有效
     */
    private boolean auto_escape;

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAuto_escape() {
        return auto_escape;
    }

    public void setAuto_escape(boolean auto_escape) {
        this.auto_escape = auto_escape;
    }

    @Override
    public String toString() {
        return "GroupMsg{" +
                "group_id=" + group_id +
                ", message='" + message + '\'' +
                ", auto_escape=" + auto_escape +
                '}';
    }
}
