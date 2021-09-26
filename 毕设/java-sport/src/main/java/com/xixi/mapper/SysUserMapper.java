package com.xixi.mapper;

import com.xixi.domain.SysRole;
import com.xixi.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户相关的操作
 */
@Mapper
public interface SysUserMapper {

    /**
     * 查询用户信息
     * @return
     */
    List<SysUser> findAll();

    /**
     * 根据用户名获取登录对象
     * @param username
     * @return
     */
    SysUser findByUsername(String username);

  /**
   * 根据用户ID查询权限信息
   * @param userId
   * @return
   */
    List<SysRole> findRoles(Long userId);
}
