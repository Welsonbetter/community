package com.scutwx.mycommunity.service;

import com.scutwx.mycommunity.dto.NotificationDTO;
import com.scutwx.mycommunity.dto.PaginationDTO;
import com.scutwx.mycommunity.enums.NotificationStatusEnum;
import com.scutwx.mycommunity.enums.NotificationTypeEnum;
import com.scutwx.mycommunity.exception.CustomizeErrorCode;
import com.scutwx.mycommunity.exception.CustomizeException;
import com.scutwx.mycommunity.mapper.NotificationMapper;
import com.scutwx.mycommunity.mapper.UserMapper;
import com.scutwx.mycommunity.model.Notification;
import com.scutwx.mycommunity.model.NotificationExample;
import com.scutwx.mycommunity.model.User;
import com.scutwx.mycommunity.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Rabbit99  2020/11/18 20:11
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

    //查询并分页
    public PaginationDTO list(Long userId, Integer page, Integer size){

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        Integer totalPage;

        //计算得到总记录条数
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        //设置页面应该显示什么样的分页样式
        paginationDTO.setPagination(totalPage, page);

        //size*(page-1)
        Integer offset = size * (page - 1);
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        if (notifications.size() == 0) {
            return paginationDTO;
        }

//        Set<Long> disUserIds = notifications.stream().map(notify -> notify.getNotifier()).collect(Collectors.toSet());
//        List<NotificationDTO> usersIds = new ArrayList<>();
//        UserExample userExample = new UserExample();
//        userExample.createCriteria().andIdIn(usersIds);
//        List<User> users = userMapper.selectByExample(userExample);
//        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(u -> u.getId(), u -> u));

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;

    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    //查找并更新相应的通知，返回一个通知的包装对象
    public NotificationDTO read(Long id, User user){
        //查找对应id的通知
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            //不存在该通知
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        //如果notification.getReceiver()与user.getId()不相等
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        //设置为已读状态，并更新到数据库
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);//拷贝数据
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }




}
