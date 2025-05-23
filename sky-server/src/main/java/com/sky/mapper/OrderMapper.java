package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    Page<Orders> queryPage(OrdersPageQueryDTO pageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders queryById(Long id);

    void update(Orders orders);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);
}
