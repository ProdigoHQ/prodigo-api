package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.result.UserDetailsResult;
import com.licode.prodigoerp.auth.application.port.input.result.UserSummaryResult;

import java.util.List;
import java.util.UUID;

public interface PlatformAdminQueryUseCase {

    List<UserSummaryResult> findAllAdmins();
    UserDetailsResult findUserById(UUID id);
}
