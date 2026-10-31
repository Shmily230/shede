package com.youkeda.shede.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youkeda.shede.dao.ProductDAO;
import com.youkeda.shede.dao.ProductDetailDAO;
import com.youkeda.shede.dataobject.ProductDO;
import com.youkeda.shede.dataobject.ProductDetailDO;
import com.youkeda.shede.model.BizException;
import com.youkeda.shede.model.PageResult;
import com.youkeda.shede.model.Paging;
import com.youkeda.shede.model.Product;
import com.youkeda.shede.param.QueryProductParam;
import com.youkeda.shede.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

// 商品列表和详情
@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductDAO productDAO;

    @Resource
    private ProductDetailDAO productDetailDAO;

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

    // 先查主表再查规格，拼成一个对象返回
    @Override
    public Product queryDetail(String productId) {
        if (!StringUtils.hasText(productId)) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "productId 不能为空");
        }
        ProductDO productDO = productDAO.selectByProductId(productId);
        if (productDO == null) {
            throw new BizException(BizException.CODE_NOT_FOUND, "商品不存在");
        }
        Product product = productDO.convertToModel();
        // 规格才是真扣库存的维度
        List<ProductDetailDO> detailDOList = productDetailDAO.selectByProductId(productId);
        product.fillDetails(detailDOList);
        return product;
    }
}
