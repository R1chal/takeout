package com.sky.mapper;

import com.sky.dto.DishDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     * @param dishDto
     */
    void batchInsert(DishDTO dishDto);

}
