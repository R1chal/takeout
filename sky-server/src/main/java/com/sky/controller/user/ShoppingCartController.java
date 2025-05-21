package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingcartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingcartService shoppingcartService;

    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车，商品信息为：{}", shoppingCartDTO);
        shoppingcartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingcartService.showShoppingCart();
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result clean(){
        log.info("清空购物车");
        shoppingcartService.delete();
        return Result.success();
    }
}
