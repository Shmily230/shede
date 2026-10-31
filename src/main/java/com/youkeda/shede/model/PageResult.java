package com.youkeda.shede.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 分页结果：这一页的数据 + 分页信息
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> list; // 这一页的数据

    private Paging paging; // 页码那些

    // 新建就给空集合，别让前端拿到 null
    public PageResult() {
        this.list = new ArrayList<T>();
        this.paging = new Paging();
    }

    // 传空也不怕，这里补个默认值
    public static <T> PageResult<T> of(List<T> list, Paging paging) {
        PageResult<T> result = new PageResult<T>();
        result.setList(list == null ? new ArrayList<T>() : list);
        result.setPaging(paging == null ? new Paging() : paging);
        return result;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
