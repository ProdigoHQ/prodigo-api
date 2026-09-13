package com.licode.prodigoerp.tenant.adapter.input.rest.controller;

import com.licode.prodigoerp.common.config.TenantContext;
import com.licode.prodigoerp.module.adapter.input.rest.dto.ModuleResponseDto;
import com.licode.prodigoerp.module.adapter.input.rest.dto.ShowPublicModuleDto;
import com.licode.prodigoerp.module.adapter.input.rest.mapper.ModuleWebMapper;
import com.licode.prodigoerp.tenant.application.port.input.TenantModuleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/tenant/modules")
@RequiredArgsConstructor
public class TenantModuleController {

    private final TenantModuleUseCase  tenantModuleUseCase;
    private final ModuleWebMapper  moduleWebMapper;

    @GetMapping(path = "/" , version = "1.0")
    public ResponseEntity<List<ShowPublicModuleDto>> getAllTenantActiveModules(){
        UUID currentTenantId = TenantContext.getCurrentTenant();

        return ResponseEntity.ok().body(
                tenantModuleUseCase.findAllActiveModulesByTenantId(currentTenantId)
                        .stream().map(
                                moduleWebMapper::toShowPublicModuleDto
                        ).toList()
        );
    }

    // this endpoint get all available modules that the current Tenant doesn't have
    @GetMapping(path = "/available", version = "1.0")
    public ResponseEntity<List<ShowPublicModuleDto>> getAllAvailableModules(){

        UUID currentTenantId = TenantContext.getCurrentTenant();

        return ResponseEntity.ok().body(
                tenantModuleUseCase.findAllAvailableModulesToPay(currentTenantId)
                        .stream().map(
                                moduleWebMapper::toShowPublicModuleDto
                        ).toList()
        );
    }

    // get the module details infos with its permissions
    @GetMapping(path = "/{moduleKey}", version = "1.0")
    public ResponseEntity<ModuleResponseDto> getModule(@PathVariable String moduleKey){

        UUID currentTenantId = TenantContext.getCurrentTenant();

        return ResponseEntity.ok().body(
                moduleWebMapper.toModuleResponseDto(
                        tenantModuleUseCase.findModuleWithPermissionsByKeyAndTenantId(moduleKey, currentTenantId))
        );
    }

}
