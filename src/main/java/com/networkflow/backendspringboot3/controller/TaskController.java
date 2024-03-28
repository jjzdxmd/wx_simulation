package com.networkflow.backendspringboot3.controller;

import com.networkflow.backendspringboot3.common.R;
import com.networkflow.backendspringboot3.model.request.TaskRequest;
import com.networkflow.backendspringboot3.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/task")
@Tag(name = "任务表接口")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Operation(summary = "获取所有任务信息")
    @GetMapping("/getAllTask")
    public R getAllTask() {
        return taskService.allTask();
    }

    @Operation(summary = "获取制定ID的任务信息")
    @GetMapping("/geTaskByID")
    public R geTaskByID(@RequestParam("taskId") String taskId) {
        return taskService.geTaskByID(taskId);
    }

    @Operation(summary = "新建拒绝服务攻击任务")
    @PostMapping("/createDosTask")
    public R createDosTask(@RequestParam("taskId") String taskId,
            @RequestParam("createTime") String createTime,
            @RequestParam("mode") Integer mode,
            @RequestParam("status") Integer status,
            @RequestParam(name = "attackParam", required = false) String[] attackParam) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTask_id(taskId);
        taskRequest.setCreate_time(LocalDateTime.parse(createTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        taskRequest.setMode(mode);
        taskRequest.setStatus(status);
        taskRequest.setIp(attackParam[0]);
        return taskService.createDosTask(taskRequest);
    }

    @Operation(summary = "新建端口扫描攻击任务")
    @PostMapping("/createScanTask")
    public R createOnlineTask(@RequestParam("taskId") String taskId,
            @RequestParam("createTime") String createTime,
            @RequestParam("mode") Integer mode,
            @RequestParam("status") Integer status,
            @RequestParam(name = "attackParam", required = false) String[] attackParam) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTask_id(taskId);
        taskRequest.setCreate_time(LocalDateTime.parse(createTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        taskRequest.setMode(mode);
        taskRequest.setStatus(status);
        taskRequest.setIp(attackParam[0]);
        taskRequest.setPort(attackParam[1]);
        taskRequest.setProtocol(attackParam[2]);

        return taskService.createScanTask(taskRequest);
    }

    @Operation(summary = "新建路由攻击任务")
    @PostMapping("/createRouteTask")
    public R createRouteTask(@RequestParam("taskId") String taskId,
            @RequestParam("createTime") String createTime,
            @RequestParam("mode") Integer mode,
            @RequestParam("status") Integer status,
            @RequestParam(name = "attackParam", required = false) String[] attackParam) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTask_id(taskId);
        taskRequest.setCreate_time(LocalDateTime.parse(createTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        taskRequest.setMode(mode);
        taskRequest.setStatus(status);
        taskRequest.setIp(attackParam[0]);
        taskRequest.setPort(attackParam[1]);
        taskRequest.setDuration(attackParam[2]);

        return taskService.createRouteTask(taskRequest);
    }

    @Operation(summary = "删除任务")
    @PostMapping("/deleteTask")
    public R deleteTask(@RequestParam("taskId") String[] taskId) {
        return taskService.deleteTask(taskId);
    }

    @Operation(summary = "开始任务")
    @PostMapping("/startTask")
    public R startTask(@RequestParam("taskId") String[] taskId) {
        return taskService.startTask(taskId);
    }

    @Operation(summary = "停止任务")
    @PostMapping("/stopTask")
    public R stopTask(@RequestParam("taskId") String[] taskId) {
        return taskService.stopTask(taskId);
    }
}
