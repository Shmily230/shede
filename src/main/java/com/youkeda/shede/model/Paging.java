package com.youkeda.shede.model;

import java.io.Serializable;

// 分页信息，前端拿去算页码
public class Paging implements Serializable {

    private static final long serialVersionUID = 1L;

    // 没传每页几条就用 10
    public static final int DEFAULT_PAGE_SIZE = 10;

    private int pageNum; // 第几页，1 开头

    private int pageSize;

    private long total; // 一共多少条

    public Paging() {
        this.pageNum = 1;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    // 页码和条数不合法就换成默认值
    public Paging(int pageNum, int pageSize) {
        this.pageNum = pageNum < 1 ? 1 : pageNum;
        this.pageSize = pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public static Paging of(Integer pageNum, Integer pageSize) {
        return new Paging(pageNum == null ? 1 : pageNum,
                pageSize == null ? DEFAULT_PAGE_SIZE : pageSize);
    }

    // 配 LIMIT 用，得减 1，不然漏第一页
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    public int getLimit() {
        return pageSize;
    }

    // 向上取整，25 条也算 3 页
    public int getTotalPage() {
        if (total <= 0) {
            return 0;
        }
        return (int) ((total + pageSize - 1) / pageSize);
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum < 1 ? 1 : pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    // 小于 1 就用默认值，不然 LIMIT 0
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Paging{pageNum=" + pageNum + ", pageSize=" + pageSize
                + ", total=" + total + ", totalPage=" + getTotalPage() + '}';
    }
}
