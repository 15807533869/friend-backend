package com.morty.friend.controller;


import com.morty.friend.service.TeamService;
import com.morty.friend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 队伍接口
 *
 * @author morty
 * create 2024-09-18
 */
@RestController
@RequestMapping("/team")
// 跨域处理
//@CrossOrigin(origins = {"http://47.106.217.210"}, allowCredentials = "true")
@CrossOrigin
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;




}