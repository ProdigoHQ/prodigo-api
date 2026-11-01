package com.licode.prodigoerp.module.application.port.input;

import com.licode.prodigoerp.module.application.port.input.result.PublicModuleResult;

import java.util.List;

public interface AllPublicModuleLookUp {

    List<PublicModuleResult> findAllPublicModules();
}
