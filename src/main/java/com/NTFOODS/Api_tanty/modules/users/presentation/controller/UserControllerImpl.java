package com.NTFOODS.Api_tanty.modules.users.presentation.controller;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.NTFOODS.Api_tanty.modules.users.application.create.command.CreateUserCommand;
import com.NTFOODS.Api_tanty.modules.users.application.create.handler.CreateUserHandler;

@RestController
public class UserControllerImpl implements UserControllerApi {
    private final CreateUserHandler create;
    public UserControllerImpl(CreateUserHandler create) {
        this.create = create;
    }
    @Override
    public void create(@RequestBody CreateUserCommand cmd) {
        create.handle(cmd);
    }
    
}
