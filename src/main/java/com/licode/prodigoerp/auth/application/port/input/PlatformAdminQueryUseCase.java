package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.result.UserSummaryResult;

import java.util.List;

public interface PlatformAdminQueryUseCase {

    List<UserSummaryResult> findAllAdmins();
}
