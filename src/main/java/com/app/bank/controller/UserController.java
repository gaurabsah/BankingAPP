package com.app.bank.controller;

import com.app.bank.dto.BankResponse;
import com.app.bank.dto.UserRequest;
import com.app.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/",method = RequestMethod.POST)
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public BankResponse getAccountDetails(@RequestParam Long userId){
        return userService.getAccountDetails(userId);
    }
}
