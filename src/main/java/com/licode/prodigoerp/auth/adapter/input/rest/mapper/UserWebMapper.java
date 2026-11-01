package com.licode.prodigoerp.auth.adapter.input.rest.mapper;

import com.licode.prodigoerp.auth.adapter.input.rest.dto.response.UserListResponse;
import com.licode.prodigoerp.auth.application.port.input.query.UserSummaryQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserWebMapper {

    UserListResponse toUserListResponse(UserSummaryQuery userSummaryQuery);
}
