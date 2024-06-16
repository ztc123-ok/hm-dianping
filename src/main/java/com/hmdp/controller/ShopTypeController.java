package com.hmdp.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService typeService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("list")
    public Result queryTypeList() {
        // 以下是添加的类别缓存
        // 1.从Redis中查询类别缓存
        String key = "cache:shoptype";
        String typeListJson = stringRedisTemplate.opsForValue().get(key);
        // 2.判断缓存是否存在
        if (StrUtil.isNotBlank(typeListJson)) {
            // 3.存在返回
            List<ShopType> typeList = JSONUtil.toList(typeListJson,ShopType.class);
            return Result.ok(typeList);
        }
        // 3.不存在查询数据库
        List<ShopType> typeList = typeService
                .query().orderByAsc("sort").list();
        // 4.添加缓存
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(typeList));

        return Result.ok(typeList);
    }
}
