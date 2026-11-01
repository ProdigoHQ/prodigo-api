package com.licode.prodigoerp.tenant.adapter.input.rest.controller;

import com.licode.prodigoerp.common.config.TenantContext;
import com.licode.prodigoerp.module.adapter.input.rest.dto.response.ModuleDetailsResponse;
import com.licode.prodigoerp.module.adapter.input.rest.dto.response.PublicModuleResponse;
import com.licode.prodigoerp.module.adapter.input.rest.mapper.ModuleRestMapper;
import com.licode.prodigoerp.module.application.port.input.query.GetTenantModuleDetailsQuery;
import com.licode.prodigoerp.module.application.port.input.query.ListAvailableTenantModulesQuery;
import com.licode.prodigoerp.module.application.port.input.query.ListTenantActiveModulesQuery;
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
    private final ModuleRestMapper  moduleRestMapper;

    @GetMapping(path = "/" , version = "1.0")
    public ResponseEntity<List<PublicModuleResponse>> getAllTenantActiveModules(){
        UUID currentTenantId = TenantContext.getCurrentTenant();

        return ResponseEntity.ok().body(
                tenantModuleUseCase.findAllActiveModulesByTenantId(new ListTenantActiveModulesQuery(currentTenantId))
                        .stream().map(
                                moduleRestMapper::toPublicModuleResponse
                        ).toList()
        );
    }

    // this endpoint get all available modules that the current Tenant doesn't have
    @GetMapping(path = "/available", version = "1.0")
    public ResponseEntity<List<PublicModuleResponse>> getAllAvailableModules(){

        UUID currentTenantId = TenantContext.getCurrentTenant();

        return ResponseEntity.ok().body(
                tenantModuleUseCase.findAllAvailableModulesToPay(new ListAvailableTenantModulesQuery(currentTenantId))
                        .stream().map(
                                moduleRestMapper::toPublicModuleResponse
                        ).toList()
        );
    }

    // get the module details infos with its permissions
    @GetMapping(path = "/{moduleKey}", version = "1.0")
    public ResponseEntity<ModuleDetailsResponse> getModule(@PathVariable String moduleKey){

        UUID currentTenantId = TenantContext.getCurrentTenant();

        return ResponseEntity.ok().body(
                moduleRestMapper.toModuleDetailsResponse(
                        tenantModuleUseCase.findModuleWithPermissionsByKeyAndTenantId(
                                new GetTenantModuleDetailsQuery(moduleKey, currentTenantId)))
        );
    }

}
