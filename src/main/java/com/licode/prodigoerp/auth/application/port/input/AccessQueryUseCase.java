package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.command.UserAccessCommand;

import java.util.List;
import java.util.UUID;

public interface AccessQueryUseCase {

    List<String> activeRoleNames(UUID userId);
    List<String> activePermissionCodes(UUID userId);
    UserAccessCommand activeAccess(UUID userId);
}
