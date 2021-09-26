package com.xixi.domain;

import lombok.Data;

import java.util.List;

@Data
public class SysMenu {
    /**
     * 主键
     */
    private Long id;

    /**
     * 前端路由
     */
    private String path;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 前端组件
     */
    private String component;

    private List<SysMenu> children;
}
