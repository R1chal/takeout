package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    PageResult queryPage4User(int page, int pageSize, Integer status);

    OrderVO queryDetails(Long id);

    void cancelOrder(Long id);

    void repetition(Long id);

}
