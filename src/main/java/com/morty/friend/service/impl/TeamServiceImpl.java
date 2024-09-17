package com.morty.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morty.friend.model.domain.Team;
import com.morty.friend.service.TeamService;
import com.morty.friend.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author morty
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2024-09-18 00:07:45
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




