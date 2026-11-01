package com.youkeda.shede.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youkeda.shede.dao.ProductDAO;
import com.youkeda.shede.dataobject.ProductDO;
import com.youkeda.shede.model.PageResult;
import com.youkeda.shede.model.Paging;
import com.youkeda.shede.model.Product;
import com.youkeda.shede.param.QueryProductParam;
import com.youkeda.shede.service.ProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

// 商品列表
@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductDAO productDAO;

    // startPage 必须紧挨着查询写，中间别插别的 SQL
    @Override
    public PageResult<Product> queryList(QueryProductParam param) {
        // null 就当成一个空条件，免得后面空指针
        QueryProductParam query = param == null ? new QueryProductParam() : param;

        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        // 用不带 LIMIT 的 selectList，带 LIMIT 的会两个 LIMIT 报错
        List<ProductDO> productDOList = productDAO.selectList(query);

        List<Product> products = new ArrayList<Product>(productDOList.size());
        for (ProductDO productDO : productDOList) {
            products.add(productDO.convertToModel());
        }

        PageInfo<ProductDO> pageInfo = new PageInfo<ProductDO>(productDOList);
        Paging paging = new Paging(pageInfo.getPageNum(), pageInfo.getPageSize());
        paging.setTotal(pageInfo.getTotal());
        return PageResult.of(products, paging);
    }
}
