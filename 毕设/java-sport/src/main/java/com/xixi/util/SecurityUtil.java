package com.xixi.util;

import com.xixi.domain.SysUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 获取当前登录用户的基本信息
 */
public class SecurityUtil {

  /**
   * 从Security主体信息中获取用户信息
   */
  public static SysUser getUser(){
    return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  /**
   * 从Security中获取用户名
   */
  public static String getUsername(){
    return getUser().getUsername();
  }

  /**
   * 从Security中获取用户ID
   */
  public static Long getUserId(){
    return getUser().getId();
  }
}
