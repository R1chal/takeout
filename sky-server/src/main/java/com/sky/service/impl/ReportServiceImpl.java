package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public TurnoverReportVO getTurnStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnOverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);;
            turnover = turnover == null ? 0.0 : turnover;
            turnOverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, "，"))
                .turnoverList(StringUtils.join(turnOverList, "，"))
                .build();

    }

    /**
     *
     * 用户状态统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalList = new ArrayList<>();

        Map map = new HashMap<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);
            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);

            totalList.add(totalUser);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .totalUserList(StringUtils.join(totalList,"，"))
                .newUserList(StringUtils.join(newUserList, "，"))
                .dateList(StringUtils.join(dateList, "，"))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> totalCountList = new ArrayList<>();

        for (LocalDate localDate : dateList) {
            //查询订单总数
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Integer totalCount = getOrderCount(beginTime, endTime, null);
            //查询有效订单数
            Integer orderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            totalCountList.add(totalCount);
            orderCountList.add(orderCount);
        }

        Integer validCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer totalCount = totalCountList.stream().reduce(Integer::sum).get();

        Double orderCompleteRate = 0.0;
        if(totalCount != 0){
            orderCompleteRate = validCount.doubleValue() / totalCount;
        }

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, "，"))
                .validOrderCountList(StringUtils.join(orderCountList, "，"))
                .orderCountList(StringUtils.join(totalCountList, "，"))
                .totalOrderCount(totalCount)
                .validOrderCount(validCount)
                .orderCompletionRate(orderCompleteRate)
                .build();

        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO queryTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> sales = orderMapper.getSales(beginTime, endTime);
        List<String> nameList = sales.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String names = StringUtils.join(nameList, "，");
        List<Integer> numList = sales.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String nums = StringUtils.join(numList, "，");

        return SalesTop10ReportVO
                .builder()
                .nameList(names)
                .numberList(nums)
                .build();
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end,Integer status){
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }
}
