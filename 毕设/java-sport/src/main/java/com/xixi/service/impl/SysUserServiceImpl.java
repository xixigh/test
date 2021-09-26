package com.xixi.service.impl;

import com.xixi.config.security.service.impl.UserDetailServiceImpl;
import com.xixi.domain.SysUser;
import com.xixi.mapper.SysUserMapper;
import com.xixi.service.SysUserService;
import com.xixi.util.Result;
import com.xixi.util.TokenUtil;
import com.xixi.vo.LoginVo;
import jdk.nashorn.internal.ir.annotations.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 登录
     * @param loginVo 登录参数：账号和密码
     * @return
     */
    @Override
    public Result login(LoginVo loginVo) {

        log.info("1.开始登陆");
        UserDetails userDetails = userDetailService.loadUserByUsername(loginVo.getUserName());

        log.info("2.判断用户是否存在，密码是否正确");
        if(null==userDetails || !passwordEncoder.matches(loginVo.getPassword(),userDetails.getPassword())){
            return Result.fail("账号或密码错误，请重新输入！");
        }
        if(!userDetails.isEnabled()){
            return Result.fail("该账号已禁用，请联系管理员");
        }

        log.info("3.登陆成功，在security对象中存入登陆者信息");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.info("4.根据登录信息获取token");
        //需要借助jwt来生成token
        String token = "";
        Map<String,String> map = new HashMap<>(2);
        token = tokenUtil.generateToken(userDetails);
        map.put("tokenHead",tokenHead);
        map.put("token",token);
        return Result.success("登陆成功!",map);
    }

    @Override
    public SysUser findByUsername(String username) {
        return sysUserMapper.findByUsername(username);
    }
}
