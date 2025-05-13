package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dto
     */
    void saveWithFlavor(DishDTO dto);

    PageResult queryPage(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品删除
     * @param dishIds
     */
    void deleteBatch(List<Long> dishIds);
}
