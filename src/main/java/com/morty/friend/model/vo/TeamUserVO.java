package com.morty.friend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍和用户信息封装类(脱敏)
 *
 * @author morty
 * @create 2024-09-26 23:52
 */
@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = 7310370792144310625L;

    /**
     * id
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
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id（队友id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
    UserVO createUser;

    /**
     * 是否已加入队伍
     */
    private boolean hasJoin;

}
