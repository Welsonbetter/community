package com.scutwx.mycommunity.service;

import com.scutwx.mycommunity.dto.PaginationDTO;
import com.scutwx.mycommunity.dto.QuestionDTO;
import com.scutwx.mycommunity.exception.CustomizeErrorCode;
import com.scutwx.mycommunity.exception.CustomizeException;
import com.scutwx.mycommunity.mapper.QuestionExtMapper;
import com.scutwx.mycommunity.mapper.QuestionMapper;
import com.scutwx.mycommunity.mapper.UserMapper;
import com.scutwx.mycommunity.model.Question;
import com.scutwx.mycommunity.model.QuestionExample;
import com.scutwx.mycommunity.model.User;
import org.apache.ibatis.session.RowBounds;
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
    private QuestionExtMapper questionExtMapper;

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
        Integer totalcount = (int)questionMapper.countByExample(new QuestionExample()); //获取question表中数据的总条数
        paginationDTO.setPagination(totalcount, page, size);//设置页码显示的相关规则

        if (page < 1) {
            page = 1;
        }
        if (page > paginationDTO.getTotalPage() && paginationDTO.getTotalPage()>0) {
            page = paginationDTO.getTotalPage();
        }

        //偏移量 = 每页显示的数据条数 * （当前页面的页码 - 1）
        Integer offset = size * (page - 1); //计算出偏移量
        QuestionExample example = new QuestionExample();

        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));
        //创建一个list对象用来存储questionDTO对象
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator()); //根据question数据查询相应的创建者user
            QuestionDTO questionDTO = new QuestionDTO();
            //从question拷贝数据到questionDTO对象，questionDTO是对question进行了一层包装的对象，多了个user属性
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user); //设置user
            questionDTOList.add(questionDTO); //存储questionDTO对象
        }
        paginationDTO.setQuestions(questionDTOList); //paginationDTO对象存储所查到的数据
        return paginationDTO; //返回paginationDTO对象
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        //新建PaginationDTO用以封装所查询到的相应页面的数据
        PaginationDTO paginationDTO = new PaginationDTO();

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);


        paginationDTO.setPagination(totalCount, page, size);//设置页码显示的相关规则

        //偏移量 = 每页显示的数据条数 * （当前页面的页码 - 1）
        Integer offset = size * (page - 1); //计算出偏移量

        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        //创建一个list对象用来存储questionDTO对象
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator()); //根据question数据查询相应的创建者user
            QuestionDTO questionDTO = new QuestionDTO();
            //从question拷贝数据到questionDTO对象，questionDTO是对question进行了一层包装的对象，多了个user属性
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user); //设置user
            questionDTOList.add(questionDTO); //存储questionDTO对象
        }
        paginationDTO.setQuestions(questionDTOList); //paginationDTO对象存储所查到的数据
        return paginationDTO; //返回paginationDTO对象
    }

    public QuestionDTO getById(Long id) {
       Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);

        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question){
        if(question.getId() == null){
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        }else{
            //更新

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion,example);
            if(updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
}
