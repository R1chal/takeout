package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openId查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByUserId(String openid);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    /**
     * 查询用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
