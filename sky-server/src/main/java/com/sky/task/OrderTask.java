package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类
 */
@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeOutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> byStatusAndOrderTimeLimit =
                orderMapper.getByStatusAndOrderTimeLimit(Orders.PENDING_PAYMENT, localDateTime);
        if(!CollectionUtils.isEmpty(byStatusAndOrderTimeLimit)){
            for(Orders orders : byStatusAndOrderTimeLimit){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ？")//凌晨一点触发
    public void processDeliveryOrder(){
        log.info("处理派送中的订单", LocalDateTime.now());
        List<Orders> byStatusAndOrderTimeLimit =
                orderMapper.getByStatusAndOrderTimeLimit(
                        Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));
        if(!CollectionUtils.isEmpty(byStatusAndOrderTimeLimit)){
            for(Orders orders : byStatusAndOrderTimeLimit){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
}
