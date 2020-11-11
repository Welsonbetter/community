package com.scutwx.mycommunity.controller;

import com.scutwx.mycommunity.dto.AccessTokenDTO;
import com.scutwx.mycommunity.dto.GithubUser;
import com.scutwx.mycommunity.mapper.UserMapper;
import com.scutwx.mycommunity.model.User;
import com.scutwx.mycommunity.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
//@PropertySource({"classpath:application.properties"})
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state") String state,
                          HttpServletRequest request , /*spring会自动把上下文中的request参数传入*/
                           HttpServletResponse response
                           ){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        System.out.println(githubUser.getName());
        System.out.println(githubUser.getId());

        if(githubUser != null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccount_id(String.valueOf(githubUser.getId()));//String.valueOf可强转为String类型
            user.setGmt_create(System.currentTimeMillis());
            user.setGmt_modified(user.getGmt_create());
            //user.setAvatar_url(githubUser.getAvatar_url());
            user.setAvatar_url(githubUser.getAvatar_url());
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token)); //手动添加cookie

            //登录成功，写cookie和session
//            request.getSession().setAttribute("user",githubUser);
//            System.out.println("GithubUser不为空");
//            System.out.println(githubUser);
            return "redirect:/";//此处若写成return "redirect:index";则页面跳转不成功

        }else{
            //登录失败，重新登录
            System.out.println("user是空的");
            return "redirect:index";
        }
       // return "index";
    }
}
