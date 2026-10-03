package com.licode.prodigoerp.auth.adapter.input.rest.controller;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.RoleSummaryDto;
import com.licode.prodigoerp.auth.adapter.input.rest.mapper.AuthoritiesWebMapper;
import com.licode.prodigoerp.auth.application.port.input.AuthoritiesUseCase;
import com.licode.prodigoerp.auth.application.port.input.command.RoleSummaryCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/s/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final AuthoritiesWebMapper  authoritiesWebMapper;
    private final AuthoritiesUseCase authoritiesUseCase;

    @GetMapping(value = "/{roleId}", version = "1.0")
    public ResponseEntity<RoleSummaryDto> fetchRoleSummary(@PathVariable UUID roleId) {

        RoleSummaryCommand roleSummaryCommand = authoritiesUseCase.fetchRoleSummary(roleId, null);

        return ResponseEntity.ok().body(authoritiesWebMapper.toRoleSummaryDto(roleSummaryCommand));
    }

}
