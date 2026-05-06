package com.openclassrooms.starterjwt.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.entity.Session;
import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.service.interfaces.ITeacherService;
import com.openclassrooms.starterjwt.service.interfaces.IUserService;

@Component
@Mapper(componentModel = "spring",
        uses = {IUserService.class},
        imports = {Arrays.class, Collectors.class, Session.class, User.class, Collections.class, Optional.class})
public abstract class SessionMapper implements EntityMapper<SessionDto, Session> {

    @Autowired
    protected ITeacherService teacherService;

    @Autowired
    protected IUserService userService;

    @Mappings({
            @Mapping(source = "description", target = "description"),
            @Mapping(target = "teacher",
                    expression = "java(sessionDto.getTeacher_id() != null ? this.teacherService.findById(sessionDto.getTeacher_id()) : null)"),
            @Mapping(target = "users",
                    expression = "java(Optional.ofNullable(sessionDto.getUsers()).orElseGet(Collections::emptyList).stream().map(userId -> this.userService.findById(userId)).collect(Collectors.toList()))"),
    })
    public abstract Session toEntity(SessionDto sessionDto);

    @Mappings({
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "session.teacher.id", target = "teacher_id"),
            @Mapping(target = "users",
                    expression = "java(Optional.ofNullable(session.getUsers()).orElseGet(Collections::emptyList).stream().map(u -> u.getId()).collect(Collectors.toList()))"),
    })
    public abstract SessionDto toDto(Session session);
}
