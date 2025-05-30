package com.example.satrect.mapper;

import org.mapstruct.Mapper;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import com.example.satrect.dto.request.UsersRequest;
import com.example.satrect.dto.response.UserResponse;
import com.example.satrect.entity.Users;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Users toUsers(UsersRequest usersRequest);

    UserResponse toUserResponse(Users users);

}
