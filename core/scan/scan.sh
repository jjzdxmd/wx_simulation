#!/bin/sh

# 检查参数是否提供正确
if [ "$#" -lt 1 ]; then
    echo "使用方法: $0 <目标主机或IP> [nmap参数]"
    exit 1
fi

# 提取目标主机或IP
target="$1"

# 提取额外的参数
shift
additional_args="$@"

# 使用 nmap 执行端口扫描，包括额外的参数
nmap "$target" $additional_args

