package com.scutwx.mycommunity.mapper;

import com.scutwx.mycommunity.model.Comment;
import com.scutwx.mycommunity.model.Question;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question question);

}
