package com.scutwx.mycommunity.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//把页码相关操作封装到此类中
@Data
public class PaginationDTO {

    private List<QuestionDTO> questions; //所有的question表数据
    private boolean showPrevious; //是否展示上一页的图标
    private boolean showFirstPage; //是否展示第一页的图标
    private boolean showNext; //是否展示下一页的图标
    private boolean showEndPage; //是否展示最后一页的图标
    private Integer page; //当前页
    private List<Integer> pages = new ArrayList<>(); //当前页面可以看到的所有页
    private Integer totalPage;//要显示的总页码数, totalcount为数据的总条数

    /**
     *  设置页码显示的相关规则
     * @param totalcount  question表中的数据总条数
     * @param page  //当前页码
     * @param size  //每页显示数据的条数
     */
    public void setPagination(Integer totalcount, Integer page, Integer size) {
        this.page = page;

        if (totalcount % size == 0) {  //判断要显示的总页数
            totalPage = totalcount / size;
        } else {
            totalPage = totalcount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage && totalPage>=0) {
            page = totalPage;
        }

        pages.add(page); //添加当前页码
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0) {
                pages.add(0, page - i);  //添加前三页
            }
            if (page + i <= totalPage) {
                pages.add(page + i);  //添加后三页
            }
        }
        //是否展示上一页的图标
        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }
        //是否展示下一页的图标
        if (page == totalPage) {
            showNext = false;
        } else {
            showNext = true;
        }
        //是否展示第一页的图标
        if (pages.contains(1)) {
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }
        //是否展示最后一页的图标
        if (pages.contains(totalPage)) {
            showEndPage = false;
        } else {
            showEndPage = true;
        }
    }
}
