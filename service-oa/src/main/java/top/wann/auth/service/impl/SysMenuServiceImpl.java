package top.wann.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.wann.auth.mapper.SysMenuMapper;
import top.wann.auth.service.SysMenuService;
import top.wann.auth.service.SysRoleMenuService;
import top.wann.auth.util.MenuHelper;
import top.wann.common.config.exception.GuiguException;
import top.wann.model.system.SysMenu;
import top.wann.model.system.SysRoleMenu;
import top.wann.vo.system.AssginMenuVo;
import top.wann.vo.system.MetaVo;
import top.wann.vo.system.RouterVo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author wann
 * @since 2023-03-02
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenu> findNodes() {
        // 1、查询所有的数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);

        // 2、构建树形结构
        return MenuHelper.buildTree(sysMenuList);
    }

    // 删除菜单
    @Override
    public void removeMenuById(Long id) {
        // 判断当前菜单是否有下一层菜单
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysMenu::getParentId, id);

        Integer count = baseMapper.selectCount(lambdaQueryWrapper);

        if (count > 0) {
            throw new GuiguException(201, "菜单不能删除");
        }
        baseMapper.deleteById(id);
    }

    // 查询所有菜单和角色分配的菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {

        LambdaQueryWrapper<SysMenu> sysMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysMenuLambdaQueryWrapper.eq(SysMenu::getStatus, 1);
        List<SysMenu> sysMenuList = baseMapper.selectList(sysMenuLambdaQueryWrapper);

        LambdaQueryWrapper<SysRoleMenu> sysRoleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysRoleMenuLambdaQueryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(sysRoleMenuLambdaQueryWrapper);

        List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        sysMenuList.forEach(item -> item.setSelect(menuIdList.contains(item.getId())));

        // 4、返回规定格式的菜单列表
        return MenuHelper.buildTree(sysMenuList);
    }

    // 为角色分配菜单
    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        LambdaQueryWrapper<SysRoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysRoleMenu::getRoleId, assginMenuVo.getRoleId());
        sysRoleMenuService.remove(lambdaQueryWrapper);

        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        List<SysRoleMenu> sysRoleMenuList = menuIdList.stream()
                .map(menuId -> {
                    SysRoleMenu sysRoleMenu = new SysRoleMenu();
                    sysRoleMenu.setMenuId(menuId);
                    sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
                    return sysRoleMenu;
                }).collect(Collectors.toList());
        sysRoleMenuService.saveBatch(sysRoleMenuList);
    }

    // 根据 用户id 获取用户可以操作的菜单列表
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenusList = null;
        // 1、判断当前用户是否是管理员       userId=1 是管理员
        // 1.1、 如果是管理员，查询所有菜单列表
        if (userId == 1) {
            // 查询所有菜单列表
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysMenu::getStatus, 1);
            queryWrapper.orderByAsc(SysMenu::getSortValue);
            sysMenusList = baseMapper.selectList(queryWrapper);
        } else {
            // 1.2、如果不是管理员，根据 userId 查询可以操作菜单列表
            // 多表关联查询:sys_role、sys_role_menu、sys_menu
            sysMenusList = baseMapper.findMenuListByUserId(userId);
        }

        // 2、把查询出来的数据列表， 构建成框架要求的路由结构
        // 先构建树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenusList);
        // 构建框架要求的路由结构
        return this.buildRouter(sysMenuTreeList);
    }

    // 构建框架要求的路由结构
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        // 创建 list 集合，存值最终数据
        List<RouterVo> routers = new ArrayList<>();
        // menus 遍历
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            // 下一层数据
            List<SysMenu> children = menu.getChildren();
            if (menu.getType() == 1) {
                // 加载隐藏路由
                List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    router.setAlwaysShow(true);
                    // 递归
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;

    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if (menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    // 根据 用户id 获取用户可以操作的按钮列表
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        // 1、判断是否是管理员，如果是管理员，查询所有按钮列表
        List<SysMenu> sysMenusList = null;
        if (userId == 1) {
            // 查询所有菜单列表
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysMenu::getStatus, 1);
            sysMenusList = baseMapper.selectList(queryWrapper);
        } else {
            // 2、如果不是管理员，根据userId查询可以操作按钮列表
            // 多表关联查询:sys_role、sys_role_menu、sys_menu
            sysMenusList = baseMapper.findMenuListByUserId(userId);
        }

        // 3、从查询出来的数据里面，获取可以操作按钮值的List集合，返回
        return sysMenusList.stream()
                .filter(item -> item.getType() == 2)
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
    }
}
