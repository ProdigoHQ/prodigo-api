package com.licode.prodigoerp.auth.application.helpers;

import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.auth.domain.model.Role;
import com.licode.prodigoerp.auth.domain.model.RolePermission;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RolePermissionLinker {

    private final RolePersistencePort rolePort;

    public void link(Role role, Permission permission, String grantedBy){

        if(rolePort.rolePermissionExists(role.getId(), permission.getId())){
            throw new ConflictException("Permission " + permission.getCode() + " is already assigned to this role");
        }

        RolePermission rp = new RolePermission();

        rp.setId(null);
        rp.setRole(role);
        rp.setPermission(permission);
        rp.setGrantedAt(Instant.now());
        rp.setGrantedBy(grantedBy);

        rolePort.saveRolePermission(rp);
    }

    public void unlink(Role role, Permission permission){
        if (!rolePort.rolePermissionExists(role.getId(), permission.getId())) {
            throw new NotFoundException("Permission is not assigned to this role");
        }
        rolePort.deleteRolePermission(role.getId(), permission.getId());
    }
}
