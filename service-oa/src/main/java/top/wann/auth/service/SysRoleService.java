package top.wann.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.wann.model.system.SysRole;
import top.wann.vo.system.AssginRoleVo;

import java.util.Map;

/**
 * ClassName: SysRoleService
 * Package: top.wann.auth.service
 * Description:
 *
 * @Author wann
 * @Create 2023-03-01 9:12
 * @Version 1.0
 */


public interface SysRoleService extends IService<SysRole> {
    // 1、查询所有角色 和 当前用户所属角色
    Map<String, Object> findRoleDataByUserId(Long userId);

    // 2、为用户分配角色
    void doAssign(AssginRoleVo assginRoleVo);
}
