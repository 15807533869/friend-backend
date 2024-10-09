package com.morty.friend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 *
 * @author morty
 * @create 2024-09-22 17:46
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = 4049108750190537199L;

    /**
     * 队伍id
     */
    private Long teamId;

}
