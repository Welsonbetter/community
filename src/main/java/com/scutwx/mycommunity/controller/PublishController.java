package com.scutwx.mycommunity.controller;

import com.scutwx.mycommunity.dto.QuestionDTO;
import com.scutwx.mycommunity.exception.CustomizeErrorCode;
import com.scutwx.mycommunity.exception.CustomizeException;
import com.scutwx.mycommunity.mapper.QuestionMapper;
import com.scutwx.mycommunity.mapper.UserMapper;
import com.scutwx.mycommunity.model.Question;
import com.scutwx.mycommunity.model.User;
import com.scutwx.mycommunity.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") String id, Model model){
        Long questionId = null;
        try {
            questionId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        QuestionDTO question = questionService.getById(questionId);
        model.addAttribute("title",question.getTitle());  //将查到的数据回显在页面中
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());  //
        model.addAttribute("id",question.getId());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
           @RequestParam(value = "title",required = false) String title,
           @RequestParam(value = "description",required = false) String description,
           @RequestParam(value = "tag",required = false) String tag,
           @RequestParam(value = "id",required = false) Long id,
           HttpServletRequest request,
           Model model
    )
    {


        model.addAttribute("title",title);  //用于将传入的数据回显在页面中
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);  //model将数据存储到request域中？还是session域？

        if (StringUtils.isBlank(title)) {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if (StringUtils.isBlank(description)) {
            model.addAttribute("error", "问题补充不能为空");
            return "publish";
        }
        if (StringUtils.isBlank(tag)) {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }

        User user = (User) request.getSession().getAttribute("user");

        if(user == null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }

}
