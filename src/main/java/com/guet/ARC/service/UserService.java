package com.guet.ARC.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.dao.UserRoleRepository;
import com.guet.ARC.domain.Role;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.UserRole;
import com.guet.ARC.domain.dto.user.*;
import com.guet.ARC.domain.enums.State;
import com.guet.ARC.domain.vo.user.UserRoleVo;
import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.UserQueryRepository;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.RedisCacheUtil;
import com.guet.ARC.util.WxUtils;
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
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private RedisCacheUtil<String> redisCacheUtil;

    @Autowired
    private UserRoleService userRoleService;

    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> register(UserRegisterDTO userRegisterDTO) {
        // 检验账号是否已经被注册
        if (userRepository.countByStuNumAndState(userRegisterDTO.getStuNum(), State.ACTIVE) > 0) {
            throw new AlertException(1000, "学号" + userRegisterDTO.getStuNum() + "已被注册");
        }
        if (userRepository.countByTelAndState(userRegisterDTO.getTel(), State.ACTIVE) > 0) {
            throw new AlertException(1000, "手机号" + userRegisterDTO.getTel() + "已被注册");
        }
        User user = buildUser(userRegisterDTO, System.currentTimeMillis());
        userRepository.saveAndFlush(user);
        userRoleService.setRole(user.getId(), CommonConstant.ROLE_USER_ID);
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

    public List<User> batchRegister(List<UserRegisterDTO> userRegisterDTOS, List<String> errorMsg) {
        long now = System.currentTimeMillis();
        List<User> errorData = new ArrayList<>();
        List<User> successData = new ArrayList<>();
        List<UserRole> userRoles = new ArrayList<>();
        for (UserRegisterDTO userRegisterDTO : userRegisterDTOS) {
            // 初始化信息
            User user = buildUser(userRegisterDTO, now);
            UserRole userRole = new UserRole();
            userRole.setId(IdUtil.fastSimpleUUID());
            userRole.setUserId(user.getId());
            userRole.setRoleId(CommonConstant.ROLE_USER_ID);
            userRole.setState(State.ACTIVE);
            userRole.setUpdateTime(now);
            userRole.setCreateTime(now);
            if (user.getStuNum().isEmpty() || user.getInstitute().isEmpty() || user.getTel().isEmpty() || user.getName().isEmpty()) {
                errorData.add(user);
                continue;
            }
            // 检验账号是否已经被注册
            long userStuNumCount = userRepository.countByStuNumAndState(user.getStuNum(), State.ACTIVE);
            long userTelCount = userRepository.countByTelAndState(user.getTel(), State.ACTIVE);
            if (userStuNumCount > 0 || userTelCount > 0) {
                errorData.add(user);
                if (userStuNumCount > 0) {
                    errorMsg.add(user.getStuNum() + "该学号已经被注册");
                }
                if (userTelCount > 0) {
                    errorMsg.add(user.getTel() + "该手机号已经被注册");
                }
            } else {
                successData.add(user);
                userRoles.add(userRole);
            }
        }
        userRepository.saveAllAndFlush(successData);
        userRoleRepository.saveAllAndFlush(userRoles);
        return errorData;
    }

    private User buildUser(UserRegisterDTO userRegisterDTO, long now) {
        User user = new User();
        user.setState(State.ACTIVE);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setTel(userRegisterDTO.getTel());
        user.setName(userRegisterDTO.getName());
        user.setStuNum(userRegisterDTO.getStuNum());
        user.setInstitute(userRegisterDTO.getInstitute());
        user.setNickname(userRegisterDTO.getName());
        user.setPwd(SaSecureUtil.md5(userRegisterDTO.getTel()));
        user.setId(IdUtil.fastSimpleUUID());
        return user;
    }

    // 使用https连接，无需进行密码加密
    public Map<String, Object> login(UserLoginDTO userLoginDTO) {
        String pwd = SaSecureUtil.md5(userLoginDTO.getPwd());
        Optional<User> userOptional = userRepository.findByTelAndPwdAndState(userLoginDTO.getTel(), pwd, State.ACTIVE);
        User user;
        Map<String, Object> map;
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
        } else {
            throw new AlertException(1000, "账号或者密码错误");
        }
        return map;
    }

    public Map<String, Object> adminLogin(UserLoginDTO userLoginDTO) {
        String pwd = SaSecureUtil.md5(userLoginDTO.getPwd());
        Optional<User> userOptional = userRepository.findByTelAndPwdAndState(userLoginDTO.getTel(), pwd, State.ACTIVE);
        User user;
        Map<String, Object> map;
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
                throw new AlertException(1000, "您不是管理员，没有权限登录后台管理");
            }
        }else {
            throw new AlertException(1000, "账号或者密码错误");
        }
        return map;
    }

    // 微信登录
    public void wxLogin(String code) {
        // 获取openId
        String openid = WxUtils.getOpenid(code);
        // 查询用户是否已经绑定了，没有绑定则无法登录

    }

    // 绑定微信


    public PageInfo<UserRoleVo> queryUserList(UserListQueryDTO userListQueryDTO) {
        Integer page = userListQueryDTO.getPage();
        Integer size = userListQueryDTO.getSize();
        String name = userListQueryDTO.getName();
        String institute = userListQueryDTO.getInstitute();
        name = StringUtils.hasLength(name) ? name + "%" : null;
        institute = StringUtils.hasLength(institute) ? institute + "%" : null;
        SelectStatementProvider queryStatement = select(UserQueryRepository.selectList)
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.name, isLikeWhenPresent(name))
                .and(UserDynamicSqlSupport.institute, isLikeWhenPresent(institute))
                .orderBy(UserDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<User> queryDataPage = PageHelper.startPage(page, size);
        List<User> users = userQueryRepository.selectMany(queryStatement);
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
        pageInfo.setTotalSize(queryDataPage.getTotal());
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
        Optional<User> userOptional = userRepository.findByTelAndState(tel, State.ACTIVE);
        if (userOptional.isEmpty()) {
            throw new AlertException(ResultCode.USER_NOT_EXIST);
        }
        // 生成验证码并返回
        // 随机四位验证码
        String code = String.valueOf((new Random().nextInt(8999) + 1000));
        redisCacheUtil.setCacheObject(tel, code, 120, TimeUnit.SECONDS);
        map.put("code", code);
        return map;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void updatePwd(UserUpdatePwdDTO userUpdatePwdDTO) {
        String pwd = userUpdatePwdDTO.getPwd();
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
        Optional<User> userOptional = userRepository.findByTelAndState(userUpdatePwdDTO.getTel(), State.ACTIVE);
        if (userOptional.isEmpty()) {
            throw new AlertException(ResultCode.USER_NOT_EXIST);
        }
        // 清除存储的code
        redisCacheUtil.deleteObject(userUpdatePwdDTO.getTel());
        // 修改密码
        User user = userOptional.get();
        user.setPwd(SaSecureUtil.md5(pwd));
        userRepository.save(user);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void updatePersonalInfo(UserUpdateDTO userUpdateDTO) {
        String errorMsg = "";
        boolean errorFlag = false;
        String userIdContext = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        // 我提交的手机号是不是已经被其他人注册了
        Optional<User> userByTelOptional = userRepository.findByTel(userUpdateDTO.getTel());
        if (userByTelOptional.isPresent()) {
            // 如果这个手机号不是我，那么就被其他人注册过了
            if (!userIdContext.equals(userByTelOptional.get().getId())) {
                errorMsg += userUpdateDTO.getTel() + "手机号已被注册,";
                errorFlag = true;
            }
        }
        // 我提交的邮箱是不是已经被其他人注册了
        Optional<User> userByMailOptional = userRepository.findByMail(userUpdateDTO.getMail());
        if (userByMailOptional.isPresent()) {
            if (!userIdContext.equals(userByMailOptional.get().getId())) {
                errorMsg += userUpdateDTO.getMail() + "邮箱已被注册";
                errorFlag = true;
            }
        }
        // 抛出异常
        if (errorFlag) {
            throw new AlertException(1000, errorMsg);
        }
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
        user.setMail(userUpdateDTO.getMail());
        user.setTel(userUpdateDTO.getTel());
        userRepository.save(user);
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

    public User userCanBeCurrentRoomCharger(String tel, String name) {
        Optional<User> userOptional = userRepository.findByTel(tel);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 是否可用
            if (user.getState().equals(State.NEGATIVE)) {
                throw new AlertException(1000, name + "负责人账户处于不可用状态");
            }
            // 是否对应
            if (!user.getName().equals(name)) {
                throw new AlertException(999, "负责人" + name + "的手机号:" + tel + ",与注册的手机号:" + user.getTel() + "不符");
            }
            // 判断权限
            List<Role> roles = userRoleService.queryRoleByUserId(user.getId());
            List<String> roleNames = new ArrayList<>();
            roles.forEach(v -> roleNames.add(v.getRoleName()));
            if (roleNames.contains(CommonConstant.ADMIN_ROLE) || roleNames.contains(CommonConstant.SUPER_ADMIN_ROLE)) {
                return user;
            } else {
                throw new AlertException(1000, name + "不是管理员，无法设置成负责人");
            }
        } else {
            // 相同姓名用户是否已经存在
            Optional<User> oldUserOptional = userRepository.findByName(name);
            if (oldUserOptional.isPresent()) {
                // 已经存在重名用户，所以手机号可能有问题
                throw new AlertException(1000, name + "已经注册" + ",请检查ta的手机号"+ tel +"是否正确");
            } else {
                throw new AlertException(1000, name + "负责人的账号" + tel + "未注册");
            }
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public User userBeCurrentRoomCharger(String tel, String name) {
        Optional<User> userOptional = userRepository.findByTel(tel);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 是否可用
            if (user.getState().equals(State.NEGATIVE)) {
                throw new AlertException(999, name + "负责人账户处于不可用状态");
            }
            // 是否对应
            if (!user.getName().equals(name)) {
                throw new AlertException(999, "负责人" + name + "的手机号:" + tel + ",与注册的手机号:" + user.getTel() + "不符");
            }
            // 判断权限
            List<Role> roles = userRoleService.queryRoleByUserId(user.getId());
            List<String> roleNames = new ArrayList<>();
            roles.forEach(v -> roleNames.add(v.getRoleName()));
            if (!roleNames.contains(CommonConstant.ADMIN_ROLE)) {
                // 修改为管理员
                userRoleService.setRole(user.getId(), CommonConstant.ROLE_ADMIN_ID);
            }
        } else {
            // 相同姓名用户是否已经存在
            Optional<User> oldUserOptional = userRepository.findByName(name);
            if (oldUserOptional.isPresent()) {
                // 已经存在重名用户，所以手机号可能有问题
                User oldUser = oldUserOptional.get();
                throw new AlertException(999, name + "已经注册手机号:" + oldUser.getTel() + ",请检查手机号:"+ tel +"是否正确");
            } else {
                // 给用户注册并设置为管理员
                // 初始化信息
                long now = System.currentTimeMillis();
                User newUser = new User();
                newUser.setState(State.ACTIVE);
                newUser.setCreateTime(now);
                newUser.setUpdateTime(now);
                String pwd = SaSecureUtil.md5(tel);
                newUser.setTel(tel);
                newUser.setName(name);
                newUser.setNickname(name);
                newUser.setStuNum(tel);
                newUser.setInstitute("计算机与信息安全学院");
                newUser.setPwd(pwd);
                newUser.setId(CommonUtils.generateUUID());
                userRepository.save(newUser);
                // 设置权限
                userRoleService.setRole(newUser.getId(), CommonConstant.ROLE_USER_ID);
                userRoleService.setRole(newUser.getId(), CommonConstant.ROLE_ADMIN_ID);
                return newUser;
            }
        }
        return user;
    }

    public void updateUserTel(UserUpdateTelDTO userUpdateTelDTO) {
        // 我提交的手机号是不是已经被其他人注册了
        String userIdContext = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        Optional<User> userByTelOptional = userRepository.findByTel(userUpdateTelDTO.getTel());
        if (userByTelOptional.isPresent()) {
            // 如果这个手机号不是我，那么就被其他人注册过了
            if (!userIdContext.equals(userByTelOptional.get().getId())) {
                throw new AlertException(1000, userUpdateTelDTO.getTel() + "手机号已被注册");
            }
        }
        User user = new User();
        user.setTel(userUpdateTelDTO.getTel());
        user.setId(userUpdateTelDTO.getUserId());
        user.setUpdateTime(System.currentTimeMillis());
        userRepository.save(user);
    }

    public void updateUserName(UserUpdateNameDTO userUpdateNameDTO) {
        User user = new User();
        user.setName(userUpdateNameDTO.getName());
        user.setId(userUpdateNameDTO.getUserId());
        user.setUpdateTime(System.currentTimeMillis());
        userRepository.save(user);
    }

    public void updateUserNickname(UserUpdateNicknameDTO userUpdateNicknameDTO) {
        User user = new User();
        user.setNickname(userUpdateNicknameDTO.getNickname());
        user.setId(userUpdateNicknameDTO.getUserId());
        user.setUpdateTime(System.currentTimeMillis());
        userRepository.save(user);
    }

}
