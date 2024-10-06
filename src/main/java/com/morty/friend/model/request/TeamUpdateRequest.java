package com.morty.friend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author morty
 * @create 2024-09-22 17:46
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = 4049108750190537199L;

    /**
     * 队伍id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

}
