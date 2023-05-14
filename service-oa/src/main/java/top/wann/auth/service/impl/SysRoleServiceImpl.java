package top.wann.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.wann.auth.mapper.SysRoleMapper;
import top.wann.auth.service.SysRoleService;
import top.wann.auth.service.SysUserRoleService;
import top.wann.model.system.SysRole;
import top.wann.model.system.SysUserRole;
import top.wann.vo.system.AssginRoleVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: SysRoleServiceImpl
 * Package: top.wann.auth.service.impl
 * Description:
 *
 * @Author wann
 * @Create 2023-03-01 9:13
 * @Version 1.0
 */

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        List<SysRole> allRoleList = baseMapper.selectList(null);
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<Long> roleIdList = sysUserRoleService.list(wrapper).stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        List<SysRole> assignRoleList = allRoleList.stream()
                .filter((r) -> roleIdList.contains(r.getId()))
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("assignRoleList", assignRoleList);
        map.put(("allRoleList"), allRoleList);
        return map;
    }

    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        Long userId = assginRoleVo.getUserId();
        List<SysUserRole> userRoleList = assginRoleVo.getRoleIdList().stream()
                .map((roleId) -> {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(userId);
                    sysUserRole.setRoleId(roleId);
                    return sysUserRole;
                }).collect(Collectors.toList());
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleService.remove(wrapper);
        sysUserRoleService.saveBatch(userRoleList);
    }
}
