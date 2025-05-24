package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    Page<Orders> queryPage(OrdersPageQueryDTO pageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders queryById(Long id);

    void update(Orders orders);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLimit(Integer status, LocalDateTime outTime);

    Double sumByMap(Map map);

    Integer countByMap(Map localDate);

    List<GoodsSalesDTO> getSales(LocalDateTime begin, LocalDateTime end);
}
