package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImp implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealDishMapper setmealDishMapper;
    /**
     * 新增菜品和对应的口味
     *
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDto) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);

        //向菜品表插入一条数据
        dishMapper.insert(dish);
        Long dishId = dish.getId();

        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.batchInsert(dishDto);
        }
    }

    /**
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult queryPage(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.queryPage(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品删除
     * @param dishIds
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> dishIds) {
        //判断菜品是否能够删除
        for (Long dishId : dishIds) {
            Dish dish = dishMapper.getById(dishId);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断是否绑定套餐
        List<Long> setMealIdsByDishIds = setmealDishMapper.getSetMealIdsByDishIds(dishIds);
        if(setMealIdsByDishIds != null && !setMealIdsByDishIds.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中指定的数据
        //根据菜品id集合批量删除菜品数据
        dishMapper.deleteByIds(dishIds);
        dishFlavorMapper.deleteByDishIds(dishIds);
    }
}
