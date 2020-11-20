package com.scutwx.mycommunity.dto;

import lombok.Data;

/**
 * Created by Rabbit99  2020/11/19 20:41
 */
@Data
public class QuestionQueryDTO {

    private String search;
    private String sort;
    private Long time;
    private String tag;
    private Integer page;
    private Integer size;

}
