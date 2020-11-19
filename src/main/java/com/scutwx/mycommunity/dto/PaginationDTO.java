package com.scutwx.mycommunity.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//把页码相关操作封装到此类中
@Data
public class PaginationDTO<T> {

    private List<T> data; //所有的question表数据
    private boolean showPrevious; //是否展示上一页的图标
    private boolean showFirstPage; //是否展示第一页的图标
    private boolean showNext; //是否展示下一页的图标
    private boolean showEndPage; //是否展示最后一页的图标
    private Integer page; //当前页
    private List<Integer> pages = new ArrayList<>(); //当前页面可以看到的所有页
    private Integer totalPage;//要显示的总页码数, totalcount为数据的总条数

    /**
     *  设置页码显示的相关规则
     * @param totalPage  应当展示的数据总条数
     * @param page  //当前页码
     */
    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage = totalPage;
        this.page = page;

        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0) {
                pages.add(0, page - i);
            }

            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        // 是否展示上一页
        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }

        // 是否展示下一页
        if (page == totalPage) {
            showNext = false;
        } else {
            showNext = true;
        }

        // 是否展示第一页
        if (pages.contains(1)) {
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }

        // 是否展示最后一页
        if (pages.contains(totalPage)) {
            showEndPage = false;
        } else {
            showEndPage = true;
        }
    }
}
