package com.scutwx.mycommunity.service;

import com.scutwx.mycommunity.dto.CommentDTO;
import com.scutwx.mycommunity.enums.CommentTypeEnum;
import com.scutwx.mycommunity.enums.NotificationStatusEnum;
import com.scutwx.mycommunity.enums.NotificationTypeEnum;
import com.scutwx.mycommunity.exception.CustomizeErrorCode;
import com.scutwx.mycommunity.exception.CustomizeException;
import com.scutwx.mycommunity.mapper.*;
import com.scutwx.mycommunity.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CommentExtMapper commentExtMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;

 //   @Transactional
    public void insert(Comment comment, User commentator) {
        //做一些校验
        if(comment.getParentId() == null|| comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
            // 回复评论，此处可以debug理解一下。（相当于一个二级评论，在一个评论下还有子评论 ）
            //无论一级评论还是二级评论，都存储在同一张表中
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                //想要回复的评论已经不存在了
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);

            //增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);

            // 创建通知  comment的commentator就是receiver
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());

        }else{
            // 回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            comment.setCommentCount(0);
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);

            // 创建通知
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }
    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
//        if (receiver == comment.getCommentator()) {
//            return;
//        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());//创建时间
        notification.setType(notificationType.getType());//通知类型
        notification.setOuterid(outerId);//对应外部的问题的id
        notification.setNotifier(comment.getCommentator());//这个评论本身的评论者的id
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());//默认为未读状态
        notification.setReceiver(receiver);  //receiver是创建问题的那个人，或者是一级评论的那个评论人
        notification.setNotifierName(notifierName);//回复者的名字
        notification.setOuterTitle(outerTitle);//对应外部的问题的标题
        notificationMapper.insert(notification);
    }


    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type){
        CommentExample commentExample = new CommentExample(); //用于拼接sql语句
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc"); //根据创建时间gmt_create设置评论的顺序,再sql语句中排序
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if(comments.size() == 0){
            return new ArrayList<>();  //返回一个空的列表对象
        }
        //获取去重的评论人  Set集合中元素不可重复？无序？  comments.stream().map()中的map方法意思是遍历，并应有返回的结果集
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        //用这种方式是为了降低后序过程中转换comment为commentDTO的时间复杂度，否则需要通过for嵌套for来遍历
        //根据评论人ID获取评论人并转换为Map,即转换为<key, value>的形式，这样可以很方便地根据key获取user
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long,User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(),user -> user));

        //转换comment为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser((userMap.get(comment.getCommentator())));//这样可以一次获取到user，降低时间复杂度
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }

}
