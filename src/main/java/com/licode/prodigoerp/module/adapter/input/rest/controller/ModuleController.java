package com.licode.prodigoerp.module.adapter.input.rest.controller;


import com.licode.prodigoerp.module.adapter.input.rest.dto.response.PublicModuleResponse;
import com.licode.prodigoerp.module.adapter.input.rest.mapper.ModuleRestMapper;
import com.licode.prodigoerp.module.application.port.input.AllPublicModuleLookUp;
import com.licode.prodigoerp.module.application.port.input.result.PublicModuleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/{version}/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleRestMapper moduleRestMapper;
    private final AllPublicModuleLookUp allPublicModuleLookUp;

    @GetMapping(value = "/public", version = "1.0")
    public ResponseEntity<List<PublicModuleResponse>> publicModule() {

        List<PublicModuleResult> publicModules = allPublicModuleLookUp.findAllPublicModules();

        return ResponseEntity.ok().body(
                publicModules.stream().map(
                        moduleRestMapper::toPublicModuleResponse
                ).toList()
        );
    }
}
