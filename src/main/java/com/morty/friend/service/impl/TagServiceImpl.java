package com.morty.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morty.friend.mapper.TagMapper;
import com.morty.friend.model.domain.Tag;
import com.morty.friend.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author morty
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-04-21 23:39:02
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




