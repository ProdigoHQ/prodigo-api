package com.licode.prodigoerp.auth.application.port.input;

import com.licode.prodigoerp.auth.application.port.input.query.GetPermissionSummaryQuery;
import com.licode.prodigoerp.auth.application.port.input.result.PermissionSummaryResult;

import java.util.List;

public interface PermissionQueryUseCase {
    PermissionSummaryResult get(GetPermissionSummaryQuery query);
    List<PermissionSummaryResult> list();           // tenant-assignable only
}
