package com.scutwx.mycommunity.advice;

import com.alibaba.fastjson.JSON;
import com.scutwx.mycommunity.dto.ResultDTO;
import com.scutwx.mycommunity.exception.CustomizeErrorCode;
import com.scutwx.mycommunity.exception.CustomizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
@Slf4j
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getContentType();

        //因为使用了API的方式，所以传递过来的请求的格式的ContentType会是 application/json 这种形式
        //此处分开两张情况进行响应，一种是请求接口的异常（通过postman输入资源路径），一种是请求页面的异常（通过浏览器输入资源路径）
        if ("application/json".equals(contentType)) {  //请求接口的时候发生异常，返回json信息
            // 返回 JSON
            ResultDTO resultDTO;
            if (e instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) e);
            } else {
                log.error("handle error", e);
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter(); //获取 PrintWriter 流
                writer.write(JSON.toJSONString(resultDTO)); //把resultDTO对象转换成JSONString，然后写出
                writer.close(); //关闭流
            } catch (IOException ioe) {
            }
            return null;
        } else {                                        //请求页面的时候发生异常，直接返回message
            // 错误页面跳转
            if (e instanceof CustomizeException) {
                model.addAttribute("message", e.getMessage());
            } else {
                log.error("handle error", e);
                model.addAttribute("message", CustomizeErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }
}