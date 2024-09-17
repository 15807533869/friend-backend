package com.morty.friend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author morty
 * @create 2024--11 17:54
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 4049108750190537199L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;

}
