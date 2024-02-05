package com.networkflow.backendspringboot3.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.networkflow.backendspringboot3.common.R;
import com.networkflow.backendspringboot3.mapper.*;
import com.networkflow.backendspringboot3.model.domain.Abstract;
import com.networkflow.backendspringboot3.model.domain.Task;
import com.networkflow.backendspringboot3.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AbstractServiceImpl extends ServiceImpl<AbstractMapper, Abstract> implements AbstractService {
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public R allAbstract() {
        // task_status
        // 在线检测 100:错误 200:已停止 0:未开始 1:待开始 2:执行中 4:已完成

        // 介绍栏
        Map<String, Integer> introduce = new HashMap<>();
        Long activeCount = taskMapper.selectCount(new QueryWrapper<Task>().lambda().in(Task::getStatus, 1, 2));
        Long abnormalCount = taskMapper.selectCount(new QueryWrapper<Task>().lambda().in(Task::getStatus, 100, 200));
        Long totalCount = taskMapper.selectCount(new QueryWrapper<Task>());
        // TODO 需要设置卫星节点数量
        Long wxCount = (long) 0;
        introduce.put("activeCount", activeCount.intValue());
        introduce.put("abnormalCount", abnormalCount.intValue());
        introduce.put("totalCount", totalCount.intValue());
        introduce.put("wxCount", wxCount.intValue());

        // 任务类型
        Map<String, Integer> proportion = new HashMap<>();
        Long dosCount = taskMapper.selectCount(new QueryWrapper<Task>().lambda().eq(Task::getMode, 0));
        Long scanCount = taskMapper.selectCount(new QueryWrapper<Task>().lambda().eq(Task::getMode, 1));
        Long routeCount = taskMapper.selectCount(new QueryWrapper<Task>().lambda().eq(Task::getMode, 2));
        proportion.put("dosCount", dosCount.intValue());
        proportion.put("scanCount", scanCount.intValue());
        proportion.put("routeCount", routeCount.intValue());

        // 最新任务
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(Task::getCreate_time);
        List<Task> curTasks = taskMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("introduce", introduce);
        result.put("proportion", proportion);
        result.put("curTasks", curTasks);

        return R.success("success", result);
    }

    @Override
    public R abstractByID(String taskId) {
        Map<String, Object> result = new HashMap<>();
        result.put("progress", taskMapper.selectById(taskId).getProgress());

        return R.success("success", result);
    }
}