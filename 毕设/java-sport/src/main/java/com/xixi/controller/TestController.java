package com.xixi.controller;

import com.xixi.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "测试接口")
public class TestController {

    /**
     * 测试接口连接
     */

    @ApiOperation(value = "测试test")
    //只有admin角色才能访问
    @PreAuthorize("hasAnyRole('admin')")
    @GetMapping("/test")
    public Object test(){
        return Result.success("信息返回成功", "我爱你");
    }
}
