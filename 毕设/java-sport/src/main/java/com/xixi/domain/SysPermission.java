package com.xixi.domain;

import lombok.Data;

import java.util.List;

@Data
public class SysPermission {
    /**
     * 主键
     */
    private Long id;

    /**
     * 数据权限标签名称
     */
    private String label;

    /**
     * 数据权限标签值
     */
    private String code;


}
