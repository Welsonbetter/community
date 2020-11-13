package com.scutwx.mycommunity.service;

import com.scutwx.mycommunity.mapper.UserMapper;
import com.scutwx.mycommunity.model.User;
import com.scutwx.mycommunity.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
         UserExample userExample= new UserExample();//创建userExample就是为了进行sql拼接
         userExample.createCriteria()
                 .andAccountIdEqualTo(user.getAccountId());//可以在sql语句后面进行拼接（限定条件）
         List<User> users = userMapper.selectByExample(userExample);
        if(users.size() == 0){
            //插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else{
            //更新
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();//创建userExample就是为了进行sql拼接
            example.createCriteria()
                    .andIdEqualTo(dbUser.getId());//可以在sql语句后面进行拼接（限定条件）
            userMapper.updateByExampleSelective(updateUser,example);
        }
    }

}
