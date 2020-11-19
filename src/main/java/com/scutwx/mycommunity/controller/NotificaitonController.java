package com.scutwx.mycommunity.controller;

import com.scutwx.mycommunity.dto.NotificationDTO;
import com.scutwx.mycommunity.enums.NotificationTypeEnum;
import com.scutwx.mycommunity.model.User;
import com.scutwx.mycommunity.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Rabbit99  2020/11/18 21:01
 */
@Controller
public class NotificaitonController {

    @Autowired
    private NotificationService notificationService;
    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id") Long id
                          ){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        //更新阅读状态为已读
        NotificationDTO notificationDTO = notificationService.read(id, user);

        //如果是对评论的回复或者是对问题的回复的通知类型，则返回到对应的问题页面
        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()) {
            return "redirect:/question/" + notificationDTO.getOuterid();
        } else {
            return "redirect:/";
        }
    }
}
