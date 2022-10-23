package com.guet.ARC.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.user.*;
import com.guet.ARC.domain.vo.user.UserRoleVo;
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
    public Map<String, Object> register(UserRegisterDTO userRegisterDTO) {
        // 检验账号是否已经被注册
        SelectStatementProvider statement = select(UserMapper.selectList)
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.stuNum, isEqualTo(userRegisterDTO.getStuNum()))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        SelectStatementProvider telStatement = select(UserMapper.selectList)
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.tel, isEqualTo(userRegisterDTO.getTel()))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        List<User> users = userMapper.selectMany(statement);
        List<User> usersTel = userMapper.selectMany(telStatement);
        if (!users.isEmpty()) {
            throw new AlertException(1000, "学号" + userRegisterDTO.getStuNum() + "已被注册");
        }
        if (!usersTel.isEmpty()) {
            throw new AlertException(1000, "手机号" + userRegisterDTO.getTel() + "已被注册");
        }
        User user = new User();
        long now = System.currentTimeMillis();
        user.setState(CommonConstant.STATE_ACTIVE);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        String userId = CommonUtils.generateUUID();
        String pwd = SaSecureUtil.md5(System.currentTimeMillis() + "");
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
        // 返回信息
        Map<String, Object> map = new HashMap<>();
        // 用户登录
        StpUtil.login(user.getId());
        // 获取登录token
        String token = StpUtil.getTokenValueByLoginId(StpUtil.getLoginId());
        // 后台存储用户信息
        StpUtil.getSessionByLoginId(StpUtil.getLoginId()).set("userId", user.getId());
        // 返回结果
        map.put("user", user);
        map.put("token", token);
        return map;
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
        // 清除旧的登录状态
        StpUtil.logout(userId);
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


    public PageInfo<UserRoleVo> queryUserList(UserListQueryDTO userListQueryDTO) {
        Integer page = userListQueryDTO.getPage();
        Integer size = userListQueryDTO.getSize();
        String name = userListQueryDTO.getName();
        String institute = userListQueryDTO.getInstitute();
        name = StringUtils.hasLength(name) ? name + "%" : null;
        institute = StringUtils.hasLength(institute) ? institute + "%" : null;
        SelectStatementProvider count = select(count())
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.name, isLikeWhenPresent(name))
                .and(UserDynamicSqlSupport.institute, isLikeWhenPresent(institute))
                .build().render(RenderingStrategies.MYBATIS3);
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
            userRoleVo.setName(CommonUtils.encodeName(userRoleVo.getName()));
            userRoleVo.setTel(CommonUtils.encodeTel(userRoleVo.getTel()));
            userRoleVos.add(userRoleVo);
        }
        PageInfo<UserRoleVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setPageData(userRoleVos);
        pageInfo.setTotalSize(userMapper.count(count));
        return pageInfo;
    }

    /**
     * 获取验证码
     *
     * @param tel 手机号
     * @return 验证码
     */
    public Map<String, String> getVerifyCode(String tel) {
        // 是否已经生成验证码
        String keyCache = (String) redisCacheUtil.getCacheObject(tel);
        Map<String, String> map = new HashMap<>();
        if (StringUtils.hasLength(keyCache)) {
            // 已经生成验证码，重复获取，抛出错误
            Long expire = redisCacheUtil.getExpire(tel);
            throw new AlertException(1000, "验证码已经生成,重复获取,请" + expire + "s后重新获取");
        }
        // 验证是否有该用户，检验手机号
        SelectStatementProvider statementProvider = select(UserMapper.selectList)
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.tel, isEqualTo(tel))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        List<User> users = userMapper.selectMany(statementProvider);
        if (users.size() != 1) {
            throw new AlertException(ResultCode.USER_NOT_EXIST);
        }
        // 生成验证码并返回
        // 随机四位验证码
        String code = String.valueOf((new Random().nextInt(8999) + 1000));
        redisCacheUtil.setCacheObject(tel, code, 120, TimeUnit.SECONDS);
        map.put("code", code);
        return map;
    }

    public void updatePwd(UserUpdatePwdDTO userUpdatePwdDTO) {
        // 获取解密后的密码
        String key = userUpdatePwdDTO.getKey();
        String privateKey = redisCacheUtil.getCacheObject(key);
        if (!StringUtils.hasLength(privateKey)) {
            throw new AlertException(1000, "非法修改密码操作");
        }
        String pwd = RSAUtils.decrypt(userUpdatePwdDTO.getPwd(), privateKey);
        // 验证验证码
        String code = redisCacheUtil.getCacheObject(userUpdatePwdDTO.getTel());
        if (!StringUtils.hasLength(code)) {
            throw new AlertException(1000, "请重新获取验证码");
        }
        if (!code.equals(userUpdatePwdDTO.getCode())) {
            throw new AlertException(1000, "验证码错误");
        }
        // 验证手机号
        // 验证是否有该用户，检验手机号
        SelectStatementProvider statementProvider = select(UserMapper.selectList)
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.tel, isEqualTo(userUpdatePwdDTO.getTel()))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        List<User> users = userMapper.selectMany(statementProvider);
        if (users.size() != 1) {
            throw new AlertException(ResultCode.USER_NOT_EXIST);
        }
        // 清除存储的code
        redisCacheUtil.deleteObject(userUpdatePwdDTO.getTel());
        // 修改密码
        User user = users.get(0);
        user.setPwd(SaSecureUtil.md5(pwd));
        int update = userMapper.updateByPrimaryKeySelective(user);
        if (update == 0) {
            throw new AlertException(ResultCode.SYSTEM_ERROR);
        }

    }

    public void updatePersonalInfo(UserUpdateDTO userUpdateDTO) {
        // 获取用户24小时可更新个人信息次数
        Integer leftUpdateTimes = (Integer) redisCacheUtil.getCacheObject(userUpdateDTO.getId() + "updateInfo");
        if (leftUpdateTimes != null) {
            throw new AlertException(ResultCode.UPDATE_USERINFO_OUT_OF_TIMES);
        } else {
            redisCacheUtil.setCacheObject(userUpdateDTO.getId() + "updateInfo", 0, 24, TimeUnit.HOURS);
        }
        // 更新用户信息
        User user = new User();
        user.setId(userUpdateDTO.getId());
        user.setStuNum(userUpdateDTO.getStuNum());
        user.setName(userUpdateDTO.getName());
        user.setInstitute(userUpdateDTO.getInstitute());
        user.setNickname(userUpdateDTO.getStuNum() + user.getName());
        int update = userMapper.updateByPrimaryKeySelective(user);
        if (update == 0) {
            throw new AlertException(ResultCode.SYSTEM_ERROR);
        }
    }

    public void changeUserRole(String userId, String[] roleIds) {
        if (!StringUtils.hasLength(userId)) {
            throw new AlertException(1000, "用户ID不能为空");
        }
        String userIdContext = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        if (userIdContext.equals(userId)) {
            throw new AlertException(ResultCode.OPERATE_OBJECT_NOT_SELF);
        }
        userRoleService.changeRole(userId, roleIds);
    }
}
