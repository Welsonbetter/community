package com.scutwx.mycommunity.enums;

/**
 * Created by Rabbit99  2020/11/18 17:34
 */
public enum NotificationStatusEnum {
    UNREAD(0),
    READ(1);

    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
