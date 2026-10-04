package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.port.input.PermissionQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.PermissionSummaryCommand;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionQueryService implements PermissionQueryUseCase {

    private final PermissionPersistencePort permissionPort;

    @Override
    public PermissionSummaryCommand get(UUID permissionId) {
        return permissionPort.findById(permissionId)
                .filter(Permission::isTenantAssignable) // just the Tenant's permission
                .map(AuthMappings::toSummary)
                .orElseThrow(() -> new NotFoundException("Permission not found with id: " + permissionId));

    }

    @Override
    public List<PermissionSummaryCommand> list() {
        return permissionPort.findAllTenantAssignable()
                .stream().map(AuthMappings::toSummary)
                .toList();
    }
}
