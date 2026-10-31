package com.youkeda.shede.dao;

import com.youkeda.shede.dataobject.ProductDetailDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 规格表的 SQL 在 ProductDetailDAO.xml
@Mapper
public interface ProductDetailDAO {

    int insert(ProductDetailDO productDetailDO);

    ProductDetailDO selectById(@Param("id") Long id);

    ProductDetailDO selectByProductDetailId(@Param("productDetailId") String productDetailId);

    // 只查上架的
    List<ProductDetailDO> selectByProductId(@Param("productId") String productId);

    // 按规格号扣，0 就是不够
    int decreaseStock(@Param("productDetailId") String productDetailId, @Param("quantity") int quantity);

    // 扣库存，返回 0 就是被抢完了
    int decreaseStockById(@Param("id") Long id, @Param("quantity") int quantity);

    // 回补库存，别重复调
    int increaseStockById(@Param("id") Long id, @Param("quantity") int quantity);

    int increaseStock(@Param("productDetailId") String productDetailId, @Param("quantity") int quantity);

    // 只更新传了值的字段
    int updateById(ProductDetailDO productDetailDO);

    int deleteByProductDetailId(@Param("productDetailId") String productDetailId);
}
