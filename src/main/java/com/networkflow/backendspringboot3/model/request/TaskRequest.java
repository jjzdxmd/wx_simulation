package com.networkflow.backendspringboot3.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {
    private String task_id;
    private LocalDateTime create_time;
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    // mode: 0为拒绝服务攻击，1为端口扫描攻击，3为路由攻击
    private Integer mode;
    // status：0 未启动；1 待开始；2 执行中；4 已完成
    private Integer status;

    // 攻击IP
    private String ip;
    // 攻击端口
    private String port;
    // 攻击时长
    private String duration;
}
