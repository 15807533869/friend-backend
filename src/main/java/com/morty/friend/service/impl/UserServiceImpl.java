package com.morty.friend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.morty.friend.common.BaseResponse;
import com.morty.friend.common.ErrorCode;
import com.morty.friend.exception.BusinessException;
import com.morty.friend.model.domain.User;
import com.morty.friend.mapper.UserMapper;
import com.morty.friend.model.vo.UserVO;
import com.morty.friend.service.UserService;
import com.morty.friend.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.morty.friend.contant.UserConstant.ADMIN_ROLE;
import static com.morty.friend.contant.UserConstant.USER_LOGIN_STATIE;

/**
 * 用户服务实现类
 *
 * @author morty
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-03-10 17:29:24
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "morty";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            // todo 修改为自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "[^A-Za-z0-9]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
        }

        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[^A-Za-z0-9]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }

        // 3.用户脱敏
        User safetyUser = getSafetyUser(user);

        // 4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATIE, safetyUser);

        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetUser = new User();
        safetUser.setId(originUser.getId());
        safetUser.setUsername(originUser.getUsername());
        safetUser.setUserAccount(originUser.getUserAccount());
        safetUser.setAvatarUrl(originUser.getAvatarUrl());
        safetUser.setGender(originUser.getGender());
        safetUser.setPhone(originUser.getPhone());
        safetUser.setEmail(originUser.getEmail());
        safetUser.setPlanetCode(originUser.getPlanetCode());
        safetUser.setUserRole(originUser.getUserRole());
        safetUser.setUserStatus(originUser.getUserStatus());
        safetUser.setCreateTime(originUser.getCreateTime());
        safetUser.setTags(originUser.getTags());
        return safetUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATIE);
        return 1;
    }

    /**
     * 根据标签搜索用户（内存过滤）
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1、先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2、在内存中判断是否包含要求的标签 并发流：parallelStream()
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            // 处理可能为空的情况
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 推荐用户列表
     *
     * @param pageSize
     * @param pageNum
     * @param request
     * @return
     */
    @Override
    public Page<User> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        String redisKey = String.format("friend:user:recommed:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 如果有缓存，直接读缓存
        Page<User> userPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
        if (userPage != null) {
            return userPage;
        }

        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", loginUser.getId()); // 排除当前登录用户，视业务需求
        userPage = page(new Page<>(pageNum, pageSize), queryWrapper);

        // 写缓存
        try {
            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }

        return userPage;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 补充校验，如果用户没有传任何要更新的值，就直接报错，不用执行 update 语句
        if (user.getUsername() == null && user.getUserAccount() == null && user.getAvatarUrl() == null &&
                user.getGender() == null && user.getUserPassword() == null && user.getPhone() == null &&
                user.getEmail() == null && user.getUserStatus() == null && user.getCreateTime() == null &&
                user.getUpdateTime() == null && user.getIsDelete() == null && user.getUserRole() == null &&
                user.getPlanetCode() == null && user.getTags() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）用户
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATIE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATIE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        // 仅管理员可查询
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        List<User> userList = this.list();
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        SortedMap<Integer, Long> indexDistanceMap = new TreeMap<>();
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签
            if (StringUtils.isBlank(userTags)) {
                continue;
            }
            List<String> userTagList = gson.fromJson(user.getTags(), new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            indexDistanceMap.put(i, distance);
        }
        List<User> userVOList = new ArrayList<>();
        int i = 0;
        for (Map.Entry<Integer, Long> entry : indexDistanceMap.entrySet()) {
            if (i > num) {
                break;
            }
            User user = userList.get(entry.getKey());
            System.out.println(user.getId() + ":" + entry.getKey() + ":" + entry.getValue());
            userVOList.add(user);
            i++;
        }
        return userVOList;
    }

    /**
     * 根据标签搜索用户（SQL 查询）
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Deprecated
    private List<User> searchUserByTagsBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 拼接 and 查询
        // like '%Java%' and like '%Python%'
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

}




