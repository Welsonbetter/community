package com.scutwx.mycommunity.controller;

import com.scutwx.mycommunity.dto.QuestionDTO;
import com.scutwx.mycommunity.exception.CustomizeErrorCode;
import com.scutwx.mycommunity.exception.CustomizeException;
import com.scutwx.mycommunity.mapper.QuestionMapper;
import com.scutwx.mycommunity.model.User;
import com.scutwx.mycommunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

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
        questionService.incView(questionId);
        model.addAttribute("question",questionDTO);
        return "question";
    }
}
