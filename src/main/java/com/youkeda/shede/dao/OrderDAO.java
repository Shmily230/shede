package com.youkeda.shede.dao;

import com.youkeda.shede.dataobject.OrderDO;
import com.youkeda.shede.param.QueryOrderParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

// 订单表的 SQL 在 OrderDAO.xml，表名要加反引号
@Mapper
public interface OrderDAO {

    // 插进去时 status 给 0，待付款
    int insert(OrderDO orderDO);

    OrderDO selectByOrderId(@Param("orderId") String orderId);

    List<OrderDO> selectByParam(QueryOrderParam param);

    int countByParam(QueryOrderParam param);

    List<OrderDO> selectByUserId(@Param("userId") String userId, @Param("status") Integer status);

    // 后台硬改状态，不管现在啥状态
    int updateStatus(@Param("orderId") String orderId, @Param("status") Integer status);

    // 只有待付款能改，重复回调也就返回 0
    int updatePaid(@Param("orderId") String orderId, @Param("payTime") Date payTime);

    // 同上，返回 0 就别回补库存
    int updateCancelled(@Param("orderId") String orderId, @Param("cancelTime") Date cancelTime);

    int deleteByOrderId(@Param("orderId") String orderId);
}
