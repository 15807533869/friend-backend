package com.morty.friend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author shkstart
 * @create 2024--11 17:54
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 4049108750190537199L;

    private String userAccount;
    private String userPassword;

}
