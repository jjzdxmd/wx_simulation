package com.networkflow.backendspringboot3.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.networkflow.backendspringboot3.common.R;
import com.networkflow.backendspringboot3.model.domain.Task;
import com.networkflow.backendspringboot3.model.request.TaskRequest;

public interface TaskService extends IService<Task> {
    // 获取所有任务
    R allTask();

    // 获取指定ID的任务
    R geTaskByID(String taskId);

    // 创建拒绝服务攻击任务
    R createDosTask(TaskRequest createTaskRequest);

    // 创建端口扫描攻击任务
    R createScanTask(TaskRequest createTaskRequest);

    // 创建路由攻击任务
    R createRouteTask(TaskRequest createTaskRequest);

    // 删除任务
    R deleteTask(String[] taskIds);

    // 开始任务
    R startTask(String[] taskIds);

    // 停止任务
    R stopTask(String[] taskIds);

    // 根据Task修改
    boolean updateTaskByTask(Task task);

    // 定时任务处理Status
    void checkStatus();

}
