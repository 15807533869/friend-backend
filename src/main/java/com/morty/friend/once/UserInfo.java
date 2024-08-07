package com.morty.friend.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用户信息
 */
@Data
public class UserInfo {
    /**
     * id
     */
    @ExcelProperty("成员编号")
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
}