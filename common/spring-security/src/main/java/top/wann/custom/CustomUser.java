package top.wann.custom;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import top.wann.model.system.SysUser;

import java.util.Collection;

/**
 * ClassName: CustomUser <br>
 * Package: com.jerry.security.custom <br>
 * Description:
 *
 * @Author wann
 * @Create 2023-03-03 14:32
 * @Version 1.0
 */
public class CustomUser extends User {
    /**
     * 我们自己的用户实体对象，要调取用户信息时直接获取这个实体对象。（这里我就不写get/set方法了）
     */
    private SysUser sysUser;

    public CustomUser(SysUser sysUser, Collection<? extends GrantedAuthority> authorities) {
        super(sysUser.getUsername(), sysUser.getPassword(), authorities);
        this.sysUser = sysUser;
    }

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }
}
