package com.project.petcare.controller;

import com.project.petcare.model.User;
import com.project.petcare.service.user.UserService;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.USERS)
@RestController
public class UserController {

    private final UserService userService;



    @PutMapping
    public void addUser(@RequestBody User user) {
        userService.addUser(user);

    }



}
