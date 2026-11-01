package com.licode.prodigoerp.auth.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.UserSummaryResponse;
import com.licode.prodigoerp.auth.application.port.input.result.UserSummaryResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    UserSummaryResponse toUserSummaryResponse(UserSummaryResult result);
}
