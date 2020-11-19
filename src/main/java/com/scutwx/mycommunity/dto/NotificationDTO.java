package com.scutwx.mycommunity.dto;

import lombok.Data;

/**
 * Created by Rabbit99  2020/11/18 20:02
 */
@Data
public class NotificationDTO {

    //比Notification少了个  private Long receiver; 多了个  private String typeName;
    private Long id;//
    private Long gmtCreate;//创建时间
    private Integer status;// 通知的状态（回复的内容是否已读）
    private Long notifier;//
    private String notifierName;//写了回复的那个user的名字
    private String outerTitle;//所回复的问题的名字
    private Long outerid;//所回复的问题的id
    private String typeName; //typeName的取值有两种：“回复了问题”，“回复了评论”
    private Integer type;//整形变量type表示对问题的回复还是对评论的回复，REPLY_COMMENT，REPLY_QUESTION

}
