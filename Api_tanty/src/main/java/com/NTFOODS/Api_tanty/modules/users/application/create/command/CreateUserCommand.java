package com.NTFOODS.Api_tanty.modules.users.application.create.command;

import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserRole;
import java.time.LocalDate;

public record CreateUserCommand(

        String firstname,

        String lastname,

        String phone,

        String email,

        String password,

        String address,

        String cni,

        LocalDate dateOfBirth,

        String placeOfBirth,

        String nationality,

        String level,

        String maritalStatus,

        UserRole role

){}
