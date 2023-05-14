package top.wann.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.wann.model.system.SysUser;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wann
 * @since 2023-03-01
 */
public interface SysUserService extends IService<SysUser> {

    // 更新状态
    void updateStatus(Long id, Integer status);

    // 根据用户名查询
    SysUser getUserByUserName(String username);

    Map<String, Object> getCurrentUser();
}
