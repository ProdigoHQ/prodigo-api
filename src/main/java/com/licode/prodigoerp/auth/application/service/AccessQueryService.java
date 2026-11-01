package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.AccessQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.query.GetUserAccessQuery;
import com.licode.prodigoerp.auth.application.port.input.result.UserAccessResult;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessQueryService implements AccessQueryUseCase {

    private final RolePersistencePort rolePort;
    private final PermissionPersistencePort permissionPort;

    @Override
    public List<String> activeRoleNames(GetUserAccessQuery query) {
        return rolePort.findActiveRoleNames(query.userId());
    }

    @Override
    public List<String> activePermissionCodes(GetUserAccessQuery query) {
        return permissionPort.findActivePermissionCodes(query.userId());
    }

    @Override
    public UserAccessResult activeAccess(GetUserAccessQuery query) {
        return new UserAccessResult(
                activeRoleNames(query),
                activePermissionCodes(query)
        );
    }
}
