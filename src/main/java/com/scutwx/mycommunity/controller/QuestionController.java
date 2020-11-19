package com.scutwx.mycommunity.controller;

import com.scutwx.mycommunity.dto.CommentDTO;
import com.scutwx.mycommunity.dto.QuestionDTO;
import com.scutwx.mycommunity.enums.CommentTypeEnum;
import com.scutwx.mycommunity.exception.CustomizeErrorCode;
import com.scutwx.mycommunity.exception.CustomizeException;
import com.scutwx.mycommunity.mapper.QuestionMapper;
import com.scutwx.mycommunity.model.User;
import com.scutwx.mycommunity.service.CommentService;
import com.scutwx.mycommunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id")String id,
                           Model model
    ){
        Long questionId = null;
        try {
            questionId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
      //  User user = (User) request.getSession().getAttribute("user");

        QuestionDTO questionDTO = questionService.getById(questionId);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        List<CommentDTO> comments = commentService.listByTargetId(questionId, CommentTypeEnum.QUESTION);
        //累加阅读数
        questionService.incView(questionId);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
}
