package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.helpers.AuthMappings;
import com.licode.prodigoerp.auth.application.port.input.PermissionQueryUseCase;
import com.licode.prodigoerp.auth.application.port.input.query.GetPermissionSummaryQuery;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.domain.model.Permission;
import com.licode.prodigoerp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionQueryService implements PermissionQueryUseCase {

    private final PermissionPersistencePort permissionPort;

    @Override
    public PermissionSummaryResult get(GetPermissionSummaryQuery query) {
        return permissionPort.findById(query.permissionId())
                .filter(Permission::isTenantAssignable) // just the Tenant's permission
                .map(AuthMappings::toSummary)
                .orElseThrow(() -> new NotFoundException("Permission not found with id: " + query.permissionId()));

    }

    @Override
    public List<PermissionSummaryResult> list() {
        return permissionPort.findAllTenantAssignable()
                .stream().map(AuthMappings::toSummary)
                .toList();
    }
}
