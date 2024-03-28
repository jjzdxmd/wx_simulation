package com.networkflow.backendspringboot3.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

@TableName(value = "task")
@Data
public class Task {
    @TableId(type = IdType.ASSIGN_UUID)
    private String task_id;

    private LocalDateTime create_time;
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    // mode: 0为拒绝服务攻击，1为端口扫描攻击，2为路由攻击
    private Integer mode;
    // status：0 未启动；1 待开始；2 待执行；3 已完成。
    private Integer status;
    private String protocol;
    // ip: 攻击IP
    private String ip;
    // port: 攻击端口
    private String port;
    // attackMode: 攻击模式
//    private String attackMode;
    // duration: 攻击时长
    private String duration;
    // progress: 进度
    private Float progress;
    // result: 结果
    private String result;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
