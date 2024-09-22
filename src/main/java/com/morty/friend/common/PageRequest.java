package com.morty.friend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author morty
 * @create 2024-09-19 00:22
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -2558124957694840896L;

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;

}
