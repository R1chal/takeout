package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    private WorkspaceService workspaceService;

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
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            ;
            turnover = turnover == null ? 0.0 : turnover;
            turnOverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, "，"))
                .turnoverList(StringUtils.join(turnOverList, "，"))
                .build();

    }

    /**
     * 用户状态统计
     *
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
                .totalUserList(StringUtils.join(totalList, "，"))
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
        if (totalCount != 0) {
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

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }

    @Override
    public void export(HttpServletResponse response) {
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        BusinessDataVO businessData = workspaceService
                .getBusinessData(
                        LocalDateTime.of(dateBegin, LocalTime.MIN),
                        LocalDateTime.of(dateEnd, LocalTime.MAX));

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            XSSFWorkbook sheets = new XSSFWorkbook(resourceAsStream);
            //填充数据
            XSSFSheet sheet1 = sheets.getSheet("sheet1");
            sheet1.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

            XSSFRow row = sheet1.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getUnitPrice());

            //填充明细数据
            for(int i = 0; i < 30; i++){
                LocalDate localDate = dateBegin.plusDays(i);
                BusinessDataVO busDateVO = workspaceService.getBusinessData(LocalDateTime.of(localDate, LocalTime.MIN), LocalDateTime.of(localDate, LocalTime.MAX));
                 row = sheet1.getRow(7 + i);
                 row.getCell(1).setCellValue(localDate.toString());
                 row.getCell(2).setCellValue(businessData.getTurnover());
                 row.getCell(3).setCellValue(businessData.getValidOrderCount());
                 row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                 row.getCell(5).setCellValue(businessData.getUnitPrice());
                 row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            ServletOutputStream outputStream = response.getOutputStream();
            sheets.write(outputStream);

            outputStream.close();
            sheets.close();
        } catch (IOException e) {

            throw new RuntimeException(e);

        }
    }
}
