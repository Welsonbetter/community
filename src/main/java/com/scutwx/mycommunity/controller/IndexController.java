package com.scutwx.mycommunity.controller;

import com.scutwx.mycommunity.dto.PaginationDTO;
import com.scutwx.mycommunity.dto.QuestionDTO;
import com.scutwx.mycommunity.dto.ResultDTO;
import com.scutwx.mycommunity.exception.CustomizeErrorCode;
import com.scutwx.mycommunity.mapper.QuestionMapper;
import com.scutwx.mycommunity.mapper.UserMapper;
import com.scutwx.mycommunity.model.Question;
import com.scutwx.mycommunity.model.User;
import com.scutwx.mycommunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {


    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page, //第几页
                        @RequestParam(name = "size",defaultValue = "2") Integer size  //每页多少个
    ){

        User user = (User) request.getSession().getAttribute("user");

        //调用service层方法查询数据，封装到PaginationDTO中
        PaginationDTO pagination= questionService.list(user.getId(),page,size);
        model.addAttribute("pagination",pagination);//存储数据，使其能在页面被调用
        return "index";
    }

}
