package top.wann.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.wann.model.system.SysMenu;
import top.wann.vo.system.AssginMenuVo;
import top.wann.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author wann
 * @since 2023-03-02
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    // 删除菜单
    void removeMenuById(Long id);

    // 查询所有菜单和角色分配的菜单
    List<SysMenu> findMenuByRoleId(Long roleId);

    // 为角色分配菜单
    void doAssign(AssginMenuVo assginMenuVo);

    // 根据 用户id 获取用户可以操作的菜单列表
    List<RouterVo> findUserMenuListByUserId(Long userId);

    // 根据 用户id 获取用户可以操作的按钮列表
    List<String> findUserPermsByUserId(Long userId);
}
