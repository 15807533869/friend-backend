package com.morty.friend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求
 *
 * @author morty
 * @create 2024-09-19 00:22
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -2558124957694840896L;

    private long id;

}
