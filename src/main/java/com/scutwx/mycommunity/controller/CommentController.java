package com.scutwx.mycommunity.controller;

import com.scutwx.mycommunity.dto.CommentCreateDTO;
import com.scutwx.mycommunity.dto.CommentDTO;
import com.scutwx.mycommunity.dto.ResultDTO;
import com.scutwx.mycommunity.enums.CommentTypeEnum;
import com.scutwx.mycommunity.exception.CustomizeErrorCode;
import com.scutwx.mycommunity.exception.CustomizeException;
import com.scutwx.mycommunity.mapper.CommentMapper;
import com.scutwx.mycommunity.model.Comment;
import com.scutwx.mycommunity.model.User;
import com.scutwx.mycommunity.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request
                       ){

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) { //检查是否登录
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        //StringUtils.isBlank(a)等同于 a == null || a == "" || a == "  "
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment,user);
        return ResultDTO.okOf();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }

}
