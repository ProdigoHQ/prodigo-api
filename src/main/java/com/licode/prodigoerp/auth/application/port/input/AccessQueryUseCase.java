package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.query.GetUserAccessQuery;
import com.licode.prodigoerp.auth.application.port.input.result.UserAccessResult;

import java.util.List;

// user's active role names and permission codes without touching persistence ports directly

public interface AccessQueryUseCase {

    List<String> activeRoleNames(GetUserAccessQuery query);

    List<String> activePermissionCodes(GetUserAccessQuery query);

    UserAccessResult activeAccess(GetUserAccessQuery query);  // return both roles and permissions
}
