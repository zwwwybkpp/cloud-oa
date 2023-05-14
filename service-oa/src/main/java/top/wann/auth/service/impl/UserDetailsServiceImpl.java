package top.wann.auth.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.wann.auth.service.SysMenuService;
import top.wann.auth.service.SysUserService;
import top.wann.custom.CustomUser;
import top.wann.model.system.SysUser;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wannn
 * @date 2023/5/10 21:45
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getUserByUserName(username);
        if (null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        if (sysUser.getStatus() == 0) {
            throw new RuntimeException("账号已停用");
        }
        List<String> perms = sysMenuService.findUserPermsByUserId(sysUser.getId());
        List<SimpleGrantedAuthority> authorities = perms.stream().map((perm ->
                new SimpleGrantedAuthority(perm.trim())
        )).collect(Collectors.toList());
        return new CustomUser(sysUser, authorities);
    }
}
