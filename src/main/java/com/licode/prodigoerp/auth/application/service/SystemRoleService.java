package com.licode.prodigoerp.auth.application.service;

import com.licode.prodigoerp.auth.application.port.input.SystemRoleUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.RoleSummaryCommand;
import com.licode.prodigoerp.auth.application.port.input.command.SaveRoleCommand;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SystemRoleService implements SystemRoleUseCase {

    private final CurrentUserPort  currentUser;

    @Override
    public RoleSummaryCommand create(SaveRoleCommand cmd) {
        String name = cmd.name().trim();
        return null;
    }

    @Override
    public RoleSummaryCommand update(UUID roleId, SaveRoleCommand command) {
        return null;
    }

    @Override
    public RoleSummaryCommand get(UUID roleId) {
        return null;
    }

    @Override
    public List<RoleSummaryCommand> list() {
        return List.of();
    }

    @Override
    public void delete(UUID roleId) {

    }

    @Override
    public List<RoleSummaryCommand> listTenantRoles(UUID tenantId) {
        return List.of();
    }

    @Override
    public RoleSummaryCommand getTenantRole(UUID tenantId, UUID roleId) {
        return null;
    }
}
