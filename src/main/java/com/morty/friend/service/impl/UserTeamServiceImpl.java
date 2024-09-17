package com.morty.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morty.friend.model.domain.UserTeam;
import com.morty.friend.service.UserTeamService;
import com.morty.friend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author morty
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-09-18 00:10:14
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




