package com.scutwx.mycommunity.mapper;

import com.scutwx.mycommunity.model.Comment;
import com.scutwx.mycommunity.model.Question;

public interface CommentExtMapper {

    int incCommentCount(Comment comment);
}
