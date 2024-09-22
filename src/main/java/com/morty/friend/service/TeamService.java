package com.morty.friend.service;

import com.morty.friend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morty.friend.model.domain.User;

/**
 * @author morty
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2024-09-18 00:07:45
 */
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

}
