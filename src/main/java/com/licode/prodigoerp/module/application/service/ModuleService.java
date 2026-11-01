package com.licode.prodigoerp.module.application.service;

import com.licode.prodigoerp.auth.application.port.input.PermissionCatalogUseCase;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;
import com.licode.prodigoerp.common.exception.ConflictException;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.common.shared.application.output.CurrentUserPort;
import com.licode.prodigoerp.module.application.helpers.ModuleMappings;
import com.licode.prodigoerp.module.application.port.input.ModuleCreateUseCase;
import com.licode.prodigoerp.module.application.port.input.ModuleLookUpUseCase;
import com.licode.prodigoerp.module.application.port.input.ModuleSubscriptionUseCase;
import com.licode.prodigoerp.module.application.port.input.TenantModuleSubscriptionUseCase;
import com.licode.prodigoerp.module.application.port.input.command.CreateModuleCommand;
import com.licode.prodigoerp.module.application.port.input.command.CreateModuleSubscriptionCommand;
import com.licode.prodigoerp.module.application.port.input.command.SelectedModuleCommand;
import com.licode.prodigoerp.module.application.port.input.result.ModuleDetailsResult;
import com.licode.prodigoerp.module.application.port.output.ModuleQueryPort;
import com.licode.prodigoerp.module.application.port.output.SaveModulePort;
import com.licode.prodigoerp.module.domain.model.Module;
import com.licode.prodigoerp.tenant.application.port.input.TenantLookUpUseCase;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModuleService implements TenantModuleSubscriptionUseCase, ModuleCreateUseCase {

    private final ModuleLookUpUseCase moduleLookUpUseCase;
    private final TenantLookUpUseCase tenantLookUpUseCase;
    private final ModuleSubscriptionUseCase moduleSubscriptionUseCase;
    private final PermissionCatalogUseCase permissionCatalogUseCase;
    private final CurrentUserPort currentUser;
    private final ModuleQueryPort moduleQueryPort;
    private final SaveModulePort saveModulePort;

    @Override
    @Transactional
    public Map<String, Module> createTenantModuleSubscription(UUID tenantId, List<SelectedModuleCommand> selectedModuleCommands) {

        // to keep track of all the modules subscribe by the tenant
        Map<String, Module> allModuleSubscriptions = new HashMap<>();

        // first need to fetch all the Selected module Object
        // (Because, here the frontend juste provide the module id,
        // name and key, but we need the whole Module object)
        List<Module> selectedModuleObj = selectedModuleCommands.stream()
                .map(
                        selectedModuleCommand -> {
                            Optional<Module> fetchedModule = moduleLookUpUseCase.findModuleByModuleKey(selectedModuleCommand.moduleKey());

                            if(fetchedModule.isEmpty()){
                                throw new NotFoundException("Module Not Found with id: " + selectedModuleCommand.moduleKey());
                            }

                            return fetchedModule.get();
                        }
                ).toList();

        // Fetch for the tenant infos with the tenant id
        Tenant tenant = tenantLookUpUseCase.findTenantById(tenantId);

        // with the Tenant info and the fetched modules, we can now create a ModuleSubscription
        // for every module in the selectedModulesObj
        selectedModuleObj.forEach(selectedModule -> {

            // to determine which Subscription is free,
            // we just check if the module is the first in the list
            boolean isFree = selectedModuleObj.getFirst() == selectedModule;
            BigDecimal price = isFree ? BigDecimal.valueOf(0) : selectedModule.getPrice();


            allModuleSubscriptions.put(selectedModule.getModuleKey(), selectedModule);

            // TODO: Figure out to relate the currency depending on the user (Tenant) country
            CreateModuleSubscriptionCommand createModuleSubscriptionCommand = new CreateModuleSubscriptionCommand(
                    tenant,
                    selectedModule,
                    isFree,
                    price,
                    "XAF"
            );

            // Then Save the ModuleSubscription
            moduleSubscriptionUseCase.createModuleSubscription(createModuleSubscriptionCommand);
        });

        return allModuleSubscriptions;
    }

    @Override
    @Transactional
    public ModuleDetailsResult createModule(CreateModuleCommand command) {

        // check if there is already a Module with the moduleKey provided
        if(moduleQueryPort.findModuleByModuleKey(command.moduleKey().toUpperCase()).isPresent()){
            log.error("Module already exists with id: {}", command.moduleKey().toUpperCase());
            throw new ConflictException("Module already exists with this key: " + command.moduleKey());
        }

        String actor = currentUser.usernameOrSystem();

        Module newModule = new Module();
        Instant now = Instant.now();

        newModule.setId(null);
        newModule.setDescription(command.description());
        newModule.setModuleKey(command.moduleKey().toUpperCase());
        newModule.setName(command.name());
        newModule.setPrice(command.price());
        newModule.setCurrency("XAF");
        newModule.setIsActive(true);
        newModule.setCreatedAt(now);
        newModule.setUpdatedAt(now);
        newModule.setCreatedBy(actor);
        newModule.setUpdatedBy(actor);

        Module createdModule = saveModulePort.saveModule(newModule);

        // We need to generate all permissions for the module created
        List<PermissionSummaryResult> createdPermissions = command.createPermissions()
                .stream()
                .map(permissionCatalogUseCase::create)
                .toList();

        return ModuleMappings.toDetails(createdModule, createdPermissions);
    }
}
