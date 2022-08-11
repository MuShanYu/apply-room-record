package com.guet.ARC.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.UserListQueryDTO;
import com.guet.ARC.domain.dto.UserLoginDTO;
import com.guet.ARC.domain.dto.UserRegisterDTO;
import com.guet.ARC.domain.vo.UserRoleVo;
import com.guet.ARC.mapper.UserDynamicSqlSupport;
import com.guet.ARC.mapper.UserMapper;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.RSAUtils;
import com.guet.ARC.util.RedisCacheUtil;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisCacheUtil<String> redisCacheUtil;

    @Autowired
    private UserRoleService userRoleService;

    public Map<String, String> getPublicKey(Long currentTimeMillis) {
        Map<String, String> keyPair = null;
        try {
            keyPair = RSAUtils.genKeyPair();
            String privateKey = keyPair.get("private");
            String publicKey = keyPair.get("public");
            redisCacheUtil.setCacheObject(String.valueOf(currentTimeMillis), privateKey, 30, TimeUnit.SECONDS);
            Map<String, String> map = new HashMap<>();
            map.put("public", publicKey);
            return map;
        } catch (Exception e) {
            throw new RuntimeException("私钥，密钥生成失败");
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public User register(UserRegisterDTO userRegisterDTO) {
        User user = new User();
        long now = System.currentTimeMillis();
        user.setState(CommonConstant.STATE_ACTIVE);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        String userId = CommonUtils.generateUUID();
        String pwd = SaSecureUtil.md5("123456");
        String name = userRegisterDTO.getName();
        String stuNum = userRegisterDTO.getStuNum();
        String institute = userRegisterDTO.getInstitute();
        String tel = userRegisterDTO.getTel();
        user.setTel(tel);
        user.setName(name);
        user.setStuNum(stuNum);
        user.setInstitute(institute);
        String nickname = stuNum + name;
        user.setNickname(nickname);
        user.setPwd(pwd);
        user.setId(userId);
        userMapper.insertSelective(user);
        userRoleService.setRole(userId, CommonConstant.ROLE_USER_ID);
        return user;
    }

    public Map<String, Object> login(UserLoginDTO userLoginDTO) {
        String tel = userLoginDTO.getTel();
        String encodePwd = userLoginDTO.getPwd();
        String key = userLoginDTO.getKey();
        String privateKey = redisCacheUtil.getCacheObject(key);
        String pwd = SaSecureUtil.md5(RSAUtils.decrypt(encodePwd, privateKey));
        SelectStatementProvider queryStatement = select(UserMapper.selectList)
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.tel, isEqualTo(tel))
                .and(UserDynamicSqlSupport.pwd, isEqualTo(pwd))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        Optional<User> userOptional = userMapper.selectOne(queryStatement);
        User user = null;
        Map<String, Object> map = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            map = new HashMap<>();
            // 用户登录
            StpUtil.login(user.getId());
            // 获取登录token
            String token = StpUtil.getTokenValueByLoginId(StpUtil.getLoginId());
            // 后台存储用户信息
            StpUtil.getSessionByLoginId(StpUtil.getLoginId()).set("userId", user.getId());
            // 返回结果
            map.put("user", user);
            map.put("token", token);
        }
        return map;
    }

    public Map<String, String> refreshToken(String userId) {
        // toekn已经过期，token被顶下线，被剔下线
        // 更新登录状态
        StpUtil.login(userId);
        StpUtil.getSessionByLoginId(userId).set("userId", userId);
        Map<String, String> map = new HashMap<>();
        map.put("token", StpUtil.getTokenValueByLoginId(userId));
        return map;
    }

    public Map<String, Object> adminLogin(UserLoginDTO userLoginDTO) {
        String tel = userLoginDTO.getTel();
        String encodePwd = userLoginDTO.getPwd();
        String key = userLoginDTO.getKey();
        String privateKey = redisCacheUtil.getCacheObject(key);
        String pwd = SaSecureUtil.md5(RSAUtils.decrypt(encodePwd, privateKey));
        SelectStatementProvider queryStatement = select(UserMapper.selectList)
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.tel, isEqualTo(tel))
                .and(UserDynamicSqlSupport.pwd, isEqualTo(pwd))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        Optional<User> userOptional = userMapper.selectOne(queryStatement);
        User user = null;
        Map<String, Object> map = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            map = new HashMap<>();
            // 用户登录
            StpUtil.login(user.getId());
            // 获取登录token
            String token = StpUtil.getTokenValueByLoginId(StpUtil.getLoginId());
            // 后台存储用户信息
            StpUtil.getSessionByLoginId(StpUtil.getLoginId()).set("userId", user.getId());
            // 返回结果
            map.put("user", user);
            map.put("token", token);
            // 获取权限列表
            List<String> roleList = StpUtil.getRoleList();
            if (roleList.contains(CommonConstant.ADMIN_ROLE) || roleList.contains(CommonConstant.SUPER_ADMIN_ROLE)) {
                // 拥有任意权限，允许登录
                 map.put("roles", roleList);
            } else {
                // 权限不足，踢出下线，抛出错误
                StpUtil.logout();
                throw new AlertException(ResultCode.PERMISSION_REJECTED);
            }
        }
        return map;
    }


    public List<UserRoleVo> queryUserList(UserListQueryDTO userListQueryDTO) {
        Integer page = userListQueryDTO.getPage();
        Integer size = userListQueryDTO.getSize();
        String name = userListQueryDTO.getName();
        String institute = userListQueryDTO.getInstitute();
        name = StringUtils.hasLength(name) ? name + "%" : null;
        institute = StringUtils.hasLength(institute) ? institute + "%" : null;
        PageHelper.startPage(page, size);
        SelectStatementProvider queryStatement = select(UserMapper.selectList)
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.name, isLikeWhenPresent(name))
                .and(UserDynamicSqlSupport.institute, isLikeWhenPresent(institute))
                .build().render(RenderingStrategies.MYBATIS3);
        List<User> users = userMapper.selectMany(queryStatement);
        List<UserRoleVo> userRoleVos = new ArrayList<>();
        BeanCopier userCopier = BeanCopier.create(User.class, UserRoleVo.class, false);
        for (User user : users) {
            UserRoleVo userRoleVo = new UserRoleVo();
            userCopier.copy(user, userRoleVo, null);
            userRoleVo.setRoleList(userRoleService.queryRoleByUserId(user.getId()));
            userRoleVos.add(userRoleVo);
        }
        return userRoleVos;
    }
}
