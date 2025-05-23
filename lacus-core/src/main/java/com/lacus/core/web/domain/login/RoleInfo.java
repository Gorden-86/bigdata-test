package com.lacus.core.web.domain.login;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.lacus.dao.system.entity.SysRoleEntity;
import com.lacus.enums.DataScopeEnum;
import com.lacus.enums.interfaces.BasicEnumUtil;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.SetUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleInfo {

    public RoleInfo(SysRoleEntity entity, String roleKey, Set<String> menuPermissions, Set<Long> menuIds) {
        if (entity != null) {
            this.roleId = entity.getRoleId();
            this.roleName = entity.getRoleName();
            this.dataScope = BasicEnumUtil.fromValue(DataScopeEnum.class, entity.getDataScope());

            if(StrUtil.isNotEmpty(entity.getDeptIdSet())) {
                this.deptIdSet = StrUtil.split(entity.getDeptIdSet(), ",").stream()
                    .map(Convert::toLong).collect( Collectors.toSet());
            }

            this.roleKey = roleKey;
            this.menuPermissions = menuPermissions != null ? menuPermissions : SetUtils.emptySet();
            this.menuIds = menuIds != null ? menuIds : SetUtils.emptySet();

        }
    }

    private Long roleId;
    private String roleName;
    private DataScopeEnum dataScope;
    private Set<Long> deptIdSet;
    private String roleKey;
    private Set<String> menuPermissions;
    private Set<Long> menuIds;

}
