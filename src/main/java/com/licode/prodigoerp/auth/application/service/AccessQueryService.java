package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.AccessQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.UserAccessCommand;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.application.port.output.RolePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessQueryService implements AccessQueryUseCase {

    private final RolePersistencePort rolePort;
    private final PermissionPersistencePort permissionPort;

    @Override
    public List<String> activeRoleNames(UUID userId) {
        return rolePort.findActiveRoleNames(userId);
    }

    @Override
    public List<String> activePermissionCodes(UUID userId) {
        return permissionPort.findActivePermissionCodes(userId);
    }

    @Override
    public UserAccessCommand activeAccess(UUID userId) {
        return new UserAccessCommand(
                activeRoleNames(userId),
                activePermissionCodes(userId)
        );
    }
}
