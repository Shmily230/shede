package com.youkeda.shede.param;

import java.io.Serializable;

// 分页查询的父类，列表参数都继承它
public class BasePageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PAGE_SIZE = 10; // 默认 10 条

    private Integer pageNum = 1; // 第几页

    private Integer pageSize = DEFAULT_PAGE_SIZE; // 每页几条

    // 默认值写在 getter 里，不是写错
    public int getPageNum() {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    // 前端传 0 也救回来，给默认 10
    public int getPageSize() {
        return pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    // 配 LIMIT 用，得减 1 不然漏第一页
    public int getOffset() {
        return (getPageNum() - 1) * getPageSize();
    }

    public int getLimit() {
        return getPageSize();
    }
}
