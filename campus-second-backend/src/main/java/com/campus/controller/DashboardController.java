package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.Orders;
import com.campus.mapper.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘统计接口
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Resource
    private ShangpinxinxiMapper shangpinxinxiMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private YonghuMapper yonghuMapper;
    @Resource
    private QiugouxinxiMapper qiugouxinxiMapper;
    @Resource
    private JuanzengxinxiMapper juanzengxinxiMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("goods", shangpinxinxiMapper.selectCount(null));
        map.put("orders", ordersMapper.selectCount(null));
        map.put("users", yonghuMapper.selectCount(null));
        map.put("qiugou", qiugouxinxiMapper.selectCount(null));
        map.put("juanzeng", juanzengxinxiMapper.selectCount(null));
        String[] statuses = {"待付款", "待发货", "待收货", "已完成", "已取消", "已退款"};
        Map<String, Long> statusCount = new HashMap<>();
        for (String s : statuses) {
            statusCount.put(s, ordersMapper.selectCount(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, s)));
        }
        map.put("orderStatusCount", statusCount);
        return Result.success(map);
    }
}
