package com.licode.prodigoerp.module.adapter.input.rest.controller;

import com.licode.prodigoerp.module.adapter.input.rest.dto.request.CreateModuleRequest;
import com.licode.prodigoerp.module.adapter.input.rest.mapper.ModuleRestMapper;
import com.licode.prodigoerp.module.application.port.input.ModuleCreateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{version}/s/admin/modules")
@RequiredArgsConstructor
public class AdminModuleController {
    private final ModuleCreateUseCase moduleCreateUseCase;
    private final ModuleRestMapper moduleRestMapper;

    @PostMapping(path = "/", version = "1.0")
    public ResponseEntity<String> createModule(@RequestBody CreateModuleRequest request) {

        moduleCreateUseCase.createModule(
                moduleRestMapper.toCreateModuleCommand(request)
        );

        return ResponseEntity.ok("Module created successfully");
    }
}
