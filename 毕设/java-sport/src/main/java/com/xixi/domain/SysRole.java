package com.xixi.domain;

import lombok.Data;

import java.util.List;

@Data
public class SysRole {
    /**
     * 主键
     */
    private Long id;

    /**
     * 标签名称
     */
    private String label;

    /**
     * 标签值
     */
    private String code;

    /**
     * 角色对应的菜单列表
     */
    private List<SysMenu> menus;

    /**
     * 数据权限
     */
    private List<SysPermission> sysPermissions;
}
