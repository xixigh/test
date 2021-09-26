package com.xixi.config.security.service.impl;

import com.xixi.domain.SysRole;
import com.xixi.domain.SysUser;
import com.xixi.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现UserDetailsService，实现自定义登录逻辑
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

      //1.在mapper中自定义登录，根据用户名获取用户信息

      SysUser sysUser = sysUserMapper.findByUsername(username);

      if (null == sysUser){
        throw new RuntimeException("用户名或密码错误!");
      }

      if (sysUser.isAdmin()){
        List<SysRole> list = new ArrayList<>();
        SysRole sysRole = new SysRole();
        sysRole.setCode("admin");
        list.add(sysRole);
        sysUser.setSysRoles(list);
      }else {
        sysUser.setSysRoles(sysUserMapper.findRoles(sysUser.getId()));
      }

        return sysUser;
    }
}
