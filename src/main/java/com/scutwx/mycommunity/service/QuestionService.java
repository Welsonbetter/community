package com.scutwx.mycommunity.service;

import com.scutwx.mycommunity.dto.PaginationDTO;
import com.scutwx.mycommunity.dto.QuestionDTO;
import com.scutwx.mycommunity.mapper.QuestionMapper;
import com.scutwx.mycommunity.mapper.UserMapper;
import com.scutwx.mycommunity.model.Question;
import com.scutwx.mycommunity.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 根据 page 和 size 查询相应相应的页面的数据
     * @param page  当前页面的页码
     * @param size  每页显示的数据条数
     * @return
     */
    public PaginationDTO list(Integer page, Integer size) {
        //新建PaginationDTO用以封装所查询到的相应页面的数据
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalcount = questionMapper.count(); //获取question表中数据的总条数
        paginationDTO.setPagination(totalcount, page, size);//设置页码显示的相关规则

        if (page < 1) {
            page = 1;
        }
        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        //偏移量 = 每页显示的数据条数 * （当前页面的页码 - 1）
        Integer offset = size * (page - 1); //计算出偏移量
        //根据偏移量 offset 和 每页显示数据条数size 查询出相应数据
        List<Question> questions = questionMapper.list(offset, size);
        //创建一个list对象用来存储questionDTO对象
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator()); //根据question数据查询相应的创建者user
            QuestionDTO questionDTO = new QuestionDTO();
            //从question拷贝数据到questionDTO对象，questionDTO是对question进行了一层包装的对象，多了个user属性
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user); //设置user
            questionDTOList.add(questionDTO); //存储questionDTO对象
        }
        paginationDTO.setQuestions(questionDTOList); //paginationDTO对象存储所查到的数据
        return paginationDTO; //返回paginationDTO对象
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        //新建PaginationDTO用以封装所查询到的相应页面的数据
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalcount = questionMapper.countByUserId(userId); //获取question表中数据的总条数
        paginationDTO.setPagination(totalcount, page, size);//设置页码显示的相关规则

//        if (page < 1) {
//            page = 1;
//        }
//        if (page > paginationDTO.getTotalPage()) {
//            page = paginationDTO.getTotalPage();
//        }
        //偏移量 = 每页显示的数据条数 * （当前页面的页码 - 1）
        Integer offset = size * (page - 1); //计算出偏移量
        //根据偏移量 offset 和 每页显示数据条数size 查询出相应数据
        List<Question> questions = questionMapper.listByUserId(userId,offset, size);
        //创建一个list对象用来存储questionDTO对象
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator()); //根据question数据查询相应的创建者user
            QuestionDTO questionDTO = new QuestionDTO();
            //从question拷贝数据到questionDTO对象，questionDTO是对question进行了一层包装的对象，多了个user属性
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user); //设置user
            questionDTOList.add(questionDTO); //存储questionDTO对象
        }
        paginationDTO.setQuestions(questionDTOList); //paginationDTO对象存储所查到的数据
        return paginationDTO; //返回paginationDTO对象
    }
}
