package com.morty.friend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author morty
 * @create 2024-09-22 17:46
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 4049108750190537199L;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}
