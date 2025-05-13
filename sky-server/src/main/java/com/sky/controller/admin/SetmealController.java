package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.ldap.PagedResultsControl;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
/**
 * 套餐管理
 */
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> queryPage(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.queryPage(setmealPageQueryDTO);
        return Result.success(pageResult);
    }
}
