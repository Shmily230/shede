package com.youkeda.shede.dao;

import com.youkeda.shede.dataobject.ProductDO;
import com.youkeda.shede.param.QueryProductParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 商品表的 SQL 在 ProductDAO.xml
@Mapper
public interface ProductDAO {

    int insert(ProductDO productDO);

    ProductDO selectById(@Param("id") Long id);

    ProductDO selectByProductId(@Param("productId") String productId);

    List<ProductDO> selectByParam(QueryProductParam param);

    // 不带 LIMIT，分页交给 PageHelper
    List<ProductDO> selectList(QueryProductParam param);

    int countByParam(QueryProductParam param);

    // 按商品号扣，顺手加销量，0 就是不够
    int decreaseStock(@Param("productId") String productId, @Param("quantity") int quantity);

    // 扣库存，返回 0 就是被抢完了
    int decreaseStockById(@Param("id") Long id, @Param("quantity") int quantity);

    // 回补库存，别重复调
    int increaseStockById(@Param("id") Long id, @Param("quantity") int quantity);

    int increaseStock(@Param("productId") String productId, @Param("quantity") int quantity);

    int increaseSales(@Param("productId") String productId, @Param("quantity") int quantity);

    // 只更新传了值的字段
    int updateById(ProductDO productDO);

    int deleteByProductId(@Param("productId") String productId);
}
