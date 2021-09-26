package com.xixi.controller;

import com.xixi.service.SysUserService;
import com.xixi.util.Result;
import com.xixi.util.SecurityUtil;
import com.xixi.vo.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 登录
 * 退出
 * 获取当前登录用户的基本信息
 */
@RestController
@RequestMapping("/user")
@Api(value = "用户使用接口")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation(value = "登录接口")
    @PostMapping("/login")
    public  Result login(@RequestBody LoginVo loginVo){
        return sysUserService.login(loginVo);
    }

    @ApiOperation(value = "获取用户基本信息")
    @GetMapping("/getInfo")
  public Result getUserInfo(Principal principal){
      if (null == principal){
        return Result.fail("请登录！");
      }
      return Result.success("获取当前用户信息", SecurityUtil.getUser());
    }
}
