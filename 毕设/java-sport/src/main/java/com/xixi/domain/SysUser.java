package com.xixi.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户实体
 */
@Data
public class SysUser implements UserDetails {
    private Long id;

    private String userName;

    private String password;

    private Integer sex;

    private String avatar;

    private String address;

    private String openId;

    private boolean status;

    private boolean admin;

    private String phoneNumber;

    private List<SysRole> sysRoles;

    /**
     * 权限数据
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        sysRoles.forEach(item -> list.add(new SimpleGrantedAuthority("ROLE_" + item.getCode())));
        return list;
    }

    /**
     * 用户名
     * @return
     */
    @Override
    public String getUsername() {
        return userName;
    }

    /**
     * 账号是否过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    /**
     * 账号是否被锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    /**
     * 是否被禁用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return status;
    }
}
