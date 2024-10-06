package com.morty.friend.service;

import com.morty.friend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morty.friend.model.domain.User;
import com.morty.friend.model.dto.TeamQuery;
import com.morty.friend.model.request.TeamJoinRequest;
import com.morty.friend.model.request.TeamUpdateRequest;
import com.morty.friend.model.vo.TeamUserVO;

import java.util.List;

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

    /**
     * 查询队伍列表
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}
