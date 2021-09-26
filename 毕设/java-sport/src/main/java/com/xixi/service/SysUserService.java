package com.xixi.service;

import com.xixi.domain.SysUser;
import com.xixi.util.Result;
import com.xixi.vo.LoginVo;

/**
 * 用户操作逻辑接口
 */
public interface SysUserService {

    /**
     * 登录
     * @param loginVo 登录参数：账号和密码
     * @return 返回token，用token去获取资源
     */
    Result login(LoginVo loginVo);

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    SysUser findByUsername(String username);
}
