package com.youkeda.shede.service.impl;

import com.youkeda.shede.dao.ProductDAO;
import com.youkeda.shede.dao.ProductDetailDAO;
import com.youkeda.shede.dataobject.ProductDO;
import com.youkeda.shede.dataobject.ProductDetailDO;
import com.youkeda.shede.model.BizException;
import com.youkeda.shede.model.Product;
import com.youkeda.shede.service.ProductDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

// 商品详情：先查主表，再查规格，拼一个对象
@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    @Resource
    private ProductDAO productDAO;

    @Resource
    private ProductDetailDAO productDetailDAO;

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
