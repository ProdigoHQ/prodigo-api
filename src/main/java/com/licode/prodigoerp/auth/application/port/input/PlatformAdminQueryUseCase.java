package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.query.UserSummaryQuery;

import java.util.List;

public interface PlatformAdminQueryUseCase {

    List<UserSummaryQuery> get();
}
