package com.xixi.config.security.contents;

/**
 * 白名单
 */
public class SecurityContents {
  public static final String[] WHILE_LIST = {
    //后端登录接口
    "/user/login",

    //swagger相关
    "/swagger-ui.html",
    "/doc.html",
    "/webjars/**",
    "swagger-resources/**",
    "v2/**",
    "configuration/ui",
    "configuration/security",
  };
}
