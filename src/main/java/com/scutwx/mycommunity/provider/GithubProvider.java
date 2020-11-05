package com.scutwx.mycommunity.provider;

import com.alibaba.fastjson.JSON;
import com.scutwx.mycommunity.dto.AccessTokenDTO;
import com.scutwx.mycommunity.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {

    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType midiaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(midiaType,JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
//            String[] split = string.split("&");
//            String tokenStr = split[0];
//            String token = tokenStr.split("=")[1];
            System.out.println(token);
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser getUser(String accessToken){
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.github.com/user?access_token="+"e35ab40e1963ec458b33afb442d0ebbfd5e3f8e8")
                    .build();
            //正确token: e35ab40e1963ec458b33afb442d0ebbfd5e3f8e8
            //所获token：d7fe70c9e38adf5b5357de8e28151023d31c7108
            //所获token：fa0a7c1f99c460753b8ddfb91256b7fe440b9e31
            try {
                Response response = client.newCall(request).execute();
                String string = response.body().string();
                GithubUser githubUser = JSON.parseObject(string, GithubUser.class);//自动把string对象解析成JSON对象
                return githubUser;
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
    }
}
