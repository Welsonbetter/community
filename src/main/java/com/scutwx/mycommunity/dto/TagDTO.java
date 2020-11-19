package com.scutwx.mycommunity.dto;

import lombok.Data;

import java.util.List;

@Data
public class TagDTO {

    private String categoryName;
    private List<String> tags;

}
