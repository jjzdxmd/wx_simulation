package com.networkflow.backendspringboot3.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.networkflow.backendspringboot3.common.R;
import com.networkflow.backendspringboot3.mapper.TaskMapper;
import com.networkflow.backendspringboot3.model.domain.Task;
import com.networkflow.backendspringboot3.model.request.TaskRequest;
import com.networkflow.backendspringboot3.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    private static final Log log = LogFactory.get();
    private final DetectTask detectTask;
    private final TaskManager taskManager;
    private final TaskMapper taskMapper;

    @Autowired
    TaskServiceImpl(DetectTask detectTask, TaskManager taskManager, TaskMapper taskMapper) {
        this.detectTask = detectTask;
        this.taskManager = taskManager;
        this.taskMapper = taskMapper;
    }

    // 攻击任务
    // 100:错误 200:已停止 0:未开始 1:待开始 2:执行中 4:已完成
    @Override
    public R allTask() {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(Task::getCreate_time);
        return R.success(null, taskMapper.selectList(queryWrapper));
    }

    @Override
    public R geTaskByID(String taskId) {
        return R.success(null, taskMapper.selectById(taskId));
    }

    @Override
    public R createDosTask(TaskRequest createTaskRequest) {
        Task task = new Task();
        BeanUtils.copyProperties(createTaskRequest, task);

        if (taskMapper.insert(task) > 0) {
            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R createScanTask(TaskRequest createTaskRequest) {
        Task task = new Task();
        BeanUtils.copyProperties(createTaskRequest, task);

        if (taskMapper.insert(task) > 0) {
            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R createRouteTask(TaskRequest createTaskRequest) {
        Task task = new Task();
        BeanUtils.copyProperties(createTaskRequest, task);

        if (taskMapper.insert(task) > 0) {
            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R deleteTask(String[] taskIds) {
        if (taskMapper.deleteBatchIds(Arrays.asList(taskIds)) > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public boolean updateTaskByTask(Task task) {
        return taskMapper.updateById(task) > 0;
    }

    @Override
    public R startTask(String[] taskIds) {
        int successCount = 0;
        for (String taskId : taskIds) {
            UpdateWrapper<Task> updateWrapper = Wrappers.update();
            updateWrapper.lambda().set(Task::getStatus, 1).set(Task::getStart_time, null).set(Task::getEnd_time, null)
                    .set(Task::getProgress, 0).set(Task::getResult, "")
                    .eq(Task::getTask_id, taskId);

            if (taskMapper.update(null, updateWrapper) > 0) {
                successCount++;
            }
        }
        if (successCount == taskIds.length) {
            return R.success("开始成功");
        } else if (successCount > 0 && successCount < taskIds.length) {
            return R.success("部分开始成功");
        } else {
            return R.error("开始失败");
        }
    }

    @Override
    public R stopTask(String[] taskIds) {
        int successCount = 0;
        for (String taskId : taskIds) {
            taskManager.stopTask(taskId);
            UpdateWrapper<Task> updateWrapper = Wrappers.update();
            updateWrapper.lambda().set(Task::getStatus, 200).eq(Task::getTask_id, taskId);
            if (taskMapper.update(null, updateWrapper) > 0) {
                successCount++;
            }
        }
        if (successCount == taskIds.length) {
            return R.success("停止成功");
        } else if (successCount > 0 && successCount < taskIds.length) {
            return R.success("部分停止成功");
        } else {
            return R.error("停止失败");
        }
    }

    @Scheduled(cron = "0/5 * *  * * ? ")
    @Override
    public void checkStatus() {
        // log.info("轮询数据库, 线程名字为 = " + Thread.currentThread().getName());

        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Task::getStatus, 1);
        List<Task> list = taskMapper.selectList(queryWrapper);

        for (Task task : list) {
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            task.setStart_time(LocalDateTime.parse(currentTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            task.setStatus(2);
            taskMapper.updateById(task);
            if (taskMapper.updateById(task) > 0) {
                // 启动脚本
                detectTask.executeScript(task);
            } else {
                log.info("启动失败");
            }
        }
    }
}

@Component
class DetectTask {
    private static final Log log = LogFactory.get();
    private final TaskManager taskManager;
    private final TaskMapper taskMapper;

    DetectTask(TaskManager taskManager, TaskMapper taskMapper) {
        this.taskManager = taskManager;
        this.taskMapper = taskMapper;
    }

    @Async("checkTaskPool")
    public void executeScript(Task currentTask) {
        try {
            String line;
            BufferedReader reader;
            ProcessBuilder processBuilder = new ProcessBuilder("");

            // 启动脚本
            log.info("任务: " + currentTask.getTask_id() + " 执行检测, 线程名字为 = " + Thread.currentThread().getName());
            switch (currentTask.getMode()) {
                case 0:
                    // TODO 拒绝服务攻击脚本
                    processBuilder = new ProcessBuilder("/usr/bin/sh",
                            System.getProperty("user.dir") + System.getProperty("file.separator")
                                    + "core" + System.getProperty("file.separator") + "dos/dos.sh");
                    break;
                case 1:
                    // 端口攻击脚本
//                    processBuilder = new ProcessBuilder("/usr/bin/sh",
//                            System.getProperty("user.dir") + System.getProperty("file.separator")
//                                    + "core" + System.getProperty("file.separator") + "scan/scan.sh");
                    processBuilder = new ProcessBuilder("/usr/bin/nmap",currentTask.getIp(),"-p",currentTask.getPort(),currentTask.getAttackMode());
                    break;
                case 2:
                    // TODO 路由攻击脚本
                    processBuilder = new ProcessBuilder("/usr/bin/sh",
                            System.getProperty("user.dir") + System.getProperty("file.separator")
                                    + "core" + System.getProperty("file.separator") + "route/route.sh");
                    break;
                default:
                    break;
            }

            processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误流
            Process process = processBuilder.start();
            taskManager.addTaskProcess(currentTask.getTask_id(), process);
            log.info("任务: " + currentTask.getTask_id() + " 检测脚本运行的PID为:" + process.pid());
            StringBuilder resulteBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                // log.info("任务: " + currentTask.getTask_id() + " " + line);
                resulteBuilder.append(line).append('\n');
            }
            int exitCode = process.waitFor();
            reader.close();
            taskManager.stopTask(currentTask.getTask_id());

            // 更新任务完成状态
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            currentTask
                    .setEnd_time(LocalDateTime.parse(currentTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            currentTask.setStatus(4);
            currentTask.setProgress((float) 1);
            // 任务结果
            currentTask.setResult(resulteBuilder.toString());
            taskMapper.updateById(currentTask);
            log.info("任务: " + currentTask.getTask_id() + " 检测成功, 已停止, 退出码为: " + exitCode);
        } catch (IOException | InterruptedException e) {
            log.error("错误: ", e);
            Task task = new Task();
            task.setTask_id(currentTask.getTask_id());
            task.setStatus(100);
            taskMapper.updateById(task);
            taskManager.stopTask(currentTask.getTask_id());
            log.info("任务: " + currentTask.getTask_id() + " 检测失败, 已停止");
        }
    }
}

@Component
class TaskManager {
    private static final Log log = LogFactory.get();
    private final Map<String, Process> taskProcesses = new ConcurrentHashMap<>();

    public void addTaskProcess(String taskId, Process process) {
        taskProcesses.put(taskId, process);
        log.info("添加任务: " + taskId + " Map中任务数: " + taskProcesses.size());
    }

    public void stopTask(String taskId) {
        Process process = taskProcesses.get(taskId);
        if (process != null) {
            process.destroy();
            taskProcesses.remove(taskId);
        }
        log.info("删除任务: " + taskId + " Map中任务数: " + taskProcesses.size());
    }
}