package com.guet.ARC.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.enmu.Device;
import com.guet.ARC.common.enmu.RedisCacheKey;
import com.guet.ARC.common.enmu.RoleType;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.dao.UserRoleRepository;
import com.guet.ARC.dao.mybatis.UserQueryRepository;
import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import com.guet.ARC.domain.Role;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.UserRole;
import com.guet.ARC.domain.dto.user.*;
import com.guet.ARC.domain.enums.State;
import com.guet.ARC.domain.vo.user.UserRoleVo;
import com.guet.ARC.netty.manager.UserOnlineManager;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.RedisCacheUtil;
import com.guet.ARC.util.WxUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private EmailService emailService;

    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> register(UserRegisterDTO userRegisterDTO) {
        // 检验账号是否已经被注册
        if (userRepository.existsByStuNumAndState(userRegisterDTO.getStuNum(), State.ACTIVE)) {
            throw new AlertException(1000, "学号" + userRegisterDTO.getStuNum() + "已被注册");
        }
        if (userRepository.existsByMail(userRegisterDTO.getMail())) {
            throw new AlertException(1000, "邮箱" + userRegisterDTO.getMail() + "已被注册");
        }
        // 校验code
        Integer code = (Integer) redisCacheUtil.getCacheObject(userRegisterDTO.getStuNum());
        if (null == code) {
            // 不存在
            throw new AlertException(1000, "验证码不存在");
        }
        // 验证验证码
        if (!code.equals(userRegisterDTO.getCode())) {
            throw new AlertException(1000, "验证码错误");
        }
        User user = buildUser(userRegisterDTO, System.currentTimeMillis());
        userRepository.saveAndFlush(user);
        userRoleService.setRole(user.getId(), RoleType.USER.getId());
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
        User user = null;
        try {
            for (UserRegisterDTO userRegisterDTO : userRegisterDTOS) {
                // 初始化信息
                user = buildUser(userRegisterDTO, now);
                UserRole userRole = new UserRole();
                userRole.setId(IdUtil.fastSimpleUUID());
                userRole.setUserId(user.getId());
                userRole.setRoleId(RoleType.USER.getId());
                userRole.setState(State.ACTIVE);
                userRole.setUpdateTime(now);
                userRole.setCreateTime(now);
                if (user.getStuNum().isEmpty() || user.getInstitute().isEmpty() || user.getName().isEmpty()) {
                    errorData.add(user);
                    continue;
                }
                // 检验账号是否已经被注册
                if (!StrUtil.isEmpty(user.getStuNum()) && userRepository.existsByStuNumAndState(user.getStuNum(), State.ACTIVE)) {
                    errorData.add(user);
                    errorMsg.add(user.getStuNum() + "该学号已经被注册");
                } else {
                    successData.add(user);
                    userRoles.add(userRole);
                }
            }
        } catch (Exception e) {
            errorData.add(user);
            errorMsg.add(e.getMessage());
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
        user.setName(userRegisterDTO.getName());
        user.setStuNum(userRegisterDTO.getStuNum());
        user.setInstitute(userRegisterDTO.getInstitute());
        user.setNickname(userRegisterDTO.getName());
        user.setMail(userRegisterDTO.getMail());
        user.setPwd(SaSecureUtil.md5(userRegisterDTO.getStuNum()));
        user.setId(IdUtil.fastSimpleUUID());
        return user;
    }

    // 使用https连接，无需进行密码加密
    public Map<String, Object> login(UserLoginDTO userLoginDTO) {
        String pwd = SaSecureUtil.md5(userLoginDTO.getPwd());
        Optional<User> userOptional = userRepository.findByStuNumAndPwdAndState(userLoginDTO.getStuNum(), pwd, State.ACTIVE);
        User user;
        Map<String, Object> map;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            map = new HashMap<>();
            login(map, user, userLoginDTO.getDevice());
            map.put("roles", StpUtil.getRoleList());
        } else {
            throw new AlertException(1000, "账号或者密码错误");
        }
        return map;
    }

    public Map<String, Object> adminLogin(UserLoginDTO userLoginDTO) {
        String pwd = SaSecureUtil.md5(userLoginDTO.getPwd());
        Optional<User> userOptional = userRepository.findByStuNumAndPwdAndState(userLoginDTO.getStuNum(), pwd, State.ACTIVE);
        User user;
        Map<String, Object> map;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            map = new HashMap<>();
            login(map, user, userLoginDTO.getDevice());
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
        } else {
            throw new AlertException(1000, "账号或者密码错误");
        }
        return map;
    }

    // 微信登录
    public Map<String, Object> wxLogin(String code) {
        // 获取openId
        String openid = WxUtils.getOpenid(code);

        // 查询用户是否已经绑定了，没有绑定则无法登录
        if (StrUtil.isEmpty(openid)) {
            throw new AlertException(1000, "用户标识获取失败");
        }
        // 判断是否已经绑定
//        log.info("openId:{}", openid);
        Optional<User> userOptional = userRepository.findByOpenId(openid);
        Map<String, Object> map = new HashMap<>();
        ;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            map.put("canWxLogin", true);
            login(map, user, Device.WECHAT.getDevice());
            map.put("roles", StpUtil.getRoleList());
        } else {
            map.put("canWxLogin", false);
        }
        return map;
    }

    private void login(Map<String, Object> map, User user, String device) {
        // 用户登录
        StpUtil.login(user.getId(), device);
        // 获取登录token
        String token = StpUtil.getTokenValueByLoginId(StpUtil.getLoginId());
        // 后台存储用户信息
        StpUtil.getSessionByLoginId(StpUtil.getLoginId()).set("userId", user.getId());
        // 返回结果
        map.put("user", user);
        map.put("token", token);
        map.put("isBindWx", !StrUtil.isEmpty(user.getOpenId()));
        user.setPwd("");
        user.setOpenId("");
    }

    // 绑定微信
    public void bindWx(String code) {
        // 获取openId
        String openid = WxUtils.getOpenid(code);
        // 查询用户是否已经绑定了，没有绑定则无法登录
        if (StrUtil.isEmpty(openid)) {
            throw new AlertException(1000, "用户标识获取失败");
        }
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new AlertException(ResultCode.USER_NOT_EXIST);
        }
        // 是否已经绑定其他用户了
        Optional<User> userOptionalOpenId = userRepository.findByOpenId(openid);
        userOptionalOpenId.ifPresent(userByOpenId -> {
            String account = userByOpenId.getStuNum();
            if (!account.equals(userOptional.get().getStuNum())) {
                throw new AlertException(1000, "微信只允许绑定一个账号，您的微信已绑定账号：" + account + "。如果这不是您的账号请及时联系客服处理。");
            }
        });
        User user = userOptional.get();
        if (StrUtil.isEmpty(user.getOpenId())) {
            user.setUpdateTime(System.currentTimeMillis());
            user.setOpenId(openid);
            userRepository.save(user);
        } else if (user.getOpenId().equals(openid)) {
            // 与获取的Openid相同
        }
    }

    public void unBindWx(String code) {
        // 获取openId
        String openid = WxUtils.getOpenid(code);
        // 查询用户是否已经绑定了，没有绑定则无法登录
        if (StrUtil.isEmpty(openid)) {
            throw new AlertException(1000, "用户标识获取失败");
        }
        Optional<User> userOptional = userRepository.findByOpenId(openid);
        if (userOptional.isEmpty()) {
            throw new AlertException(ResultCode.USER_NOT_EXIST);
        }
        User user = userOptional.get();
        user.setOpenId("");
        user.setUpdateTime(System.currentTimeMillis());
        userRepository.save(user);
    }

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
            userRoleVos.add(userRoleVo);
        }
        PageInfo<UserRoleVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setPageData(userRoleVos);
        pageInfo.setTotalSize(queryDataPage.getTotal());
        return pageInfo;
    }

    // 获取邮箱验证码
    public void sendVerifyCode(String stuNum, String mail) {
        Optional<User> userOptional = userRepository.findByStuNum(stuNum);
        Integer code = RandomUtil.randomInt(1000, 9999);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getState().equals(State.NEGATIVE)) {
                throw new AlertException(1000, "抱歉，账户" + stuNum + "处于不可用状态,无法操作");
            }
            // 判断邮箱绑定是否一致
            if (StrUtil.isEmpty(user.getMail())) {
                throw new AlertException(1000, "账户" + stuNum + "未绑定邮箱");
            }

            // 发送验证码并缓存，有效时间未5分钟
            String message = "您的验证码为：" + code + "。有效期为5分钟。请勿将该验证码泄露给任何人！";
            // 缓存code
            redisCacheUtil.setCacheObject(stuNum, code, 5L, TimeUnit.MINUTES);
            // 发送短信
            emailService.sendSimpleMail(user.getMail(), "房间预约与流动统计App,邮箱验证码", message);
        } else {
            // 新注册用户
            if (!StrUtil.isEmpty(mail)) {
                // 发送验证码并缓存，有效时间未5分钟
                String message = "您的验证码为：" + code + "。有效期为5分钟。请勿将该验证码泄露给任何人！";
                // 缓存code
                redisCacheUtil.setCacheObject(stuNum, code, 5L, TimeUnit.MINUTES);
                // 发送短信
                emailService.sendSimpleMail(mail, "房间预约与流动统计App,邮箱验证码", message);
            }
        }
    }

    public void updatePwd(UserUpdatePwdDTO dto) {
        // 获取code
        Integer code = (Integer) redisCacheUtil.getCacheObject(dto.getStuNum());
        if (null == code) {
            // 不存在
            throw new AlertException(1000, dto.getStuNum() + "未获取验证码或验证码已过期");
        }
        // 验证验证码
        if (!code.equals(dto.getCode())) {
            throw new AlertException(1000, "验证码错误");
        }
        // 更新密码
        Optional<User> userOptional = userRepository.findByStuNum(dto.getStuNum());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUpdateTime(System.currentTimeMillis());
            user.setPwd(SaSecureUtil.md5(dto.getPwd()));
            redisCacheUtil.deleteObject(dto.getStuNum());
            userRepository.save(user);
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void updatePersonalInfo(Map<String, String> userInfo) {
        String userIdContext = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        Optional<User> userOptional = userRepository.findById(userIdContext);
        if (!StrUtil.isEmpty(userInfo.get("mail"))) {
            // 我提交的邮箱是不是已经被其他人注册了
            Optional<User> userByMailOptional = userRepository.findByMail(userInfo.get("mail"));
            if (userByMailOptional.isPresent()) {
                if (!userIdContext.equals(userByMailOptional.get().getId())) {
                    throw new AlertException(1000, userInfo.get("mail") + "邮箱已被注册");
                }
            }
        }
        userOptional.ifPresent(user -> {
            // 避免修改查询后的数据，触发jpa脏检查，触发多余更新
            User updateUserInfo = new User();
            CglibUtil.copy(user, updateUserInfo);
            CglibUtil.fillBean(userInfo, updateUserInfo);
//            log.info("user or:{}", user);
//            log.info("update user: {}", updateUserInfo);
            updateUserInfo.setUpdateTime(System.currentTimeMillis());
            userRepository.save(updateUserInfo);
        });
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

    public User userCanBeCurrentRoomCharger(String stuNum, String name) {
        Optional<User> userOptional = userRepository.findByStuNum(stuNum);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 是否可用
            if (user.getState().equals(State.NEGATIVE)) {
                throw new AlertException(1000, name + "负责人账户处于不可用状态");
            }
            // 姓名是否对应
            if (!user.getName().equals(name)) {
                throw new AlertException(1000, "学号/工号与注册姓名不匹配");
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
            throw new AlertException(1000, "学号/工号错误，或者负责人" + stuNum + name + "未注册");
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public User userBeCurrentRoomCharger(String stuNum, String name) {
        Optional<User> userOptional = userRepository.findByStuNum(stuNum);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 是否可用
            if (user.getState().equals(State.NEGATIVE)) {
                throw new AlertException(999, name + "负责人账户处于不可用状态");
            }
            // 判断权限
            List<Role> roles = userRoleService.queryRoleByUserId(user.getId());
            List<String> roleNames = new ArrayList<>();
            roles.forEach(v -> roleNames.add(v.getRoleName()));
            if (!roleNames.contains(CommonConstant.ADMIN_ROLE)) {
                // 修改为管理员
                userRoleService.setRole(user.getId(), RoleType.ADMIN.getId());
            }
        } else {
            // 要设置的负责人未注册
            throw new AlertException(1000, "学号/工号错误，或者负责人" + stuNum + name + "未注册");
        }
        return user;
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

    public Map<String, Object> refreshToken(String userId, String device) {
        // 用的redis存储，时间一到token就会被清楚，变为未登录，所以直接判定当前会话有没有登录即可
        // 判定当前会话是否登录
        Map<String, Object> res = new HashMap<>();
        // 旧的退出登录
        StpUtil.logout();
        // 刷新登录信息
        // 用户是否存在，id是否正确
        if (!userRepository.existsById(userId)) {
            throw new AlertException(ResultCode.USER_NOT_EXIST);
        }
        // 用户登录
        StpUtil.login(userId, device);
        // 获取登录token
        String loginToken = StpUtil.getTokenValueByLoginId(StpUtil.getLoginId());
        // 后台存储用户信息
        StpUtil.getSessionByLoginId(StpUtil.getLoginId()).set("userId", userId);
        // 返回结果
        res.put("token", loginToken);
        res.put("isNeedRefresh", Boolean.TRUE);
        return res;
    }

    public List<Map<String, Object>> getOlineUserList() {
        List<Map<String, Object>> res = new ArrayList<>();
        String id = StpUtil.getLoginIdAsString();
        ConcurrentMap<String, List<String>> onlineUserIdToSources = UserOnlineManager.getOnlineUserIdToSources();
        for (User user : userRepository.findAllById(onlineUserIdToSources.keySet())) {
            if (!StpUtil.hasRole(RoleType.SUPER_ADMIN.getName()) && !id.equals(user.getId())) {
                user.setName(DesensitizedUtil.chineseName(user.getName()));
            }
            Map<String, Object> beanToMap = BeanUtil.beanToMap(user, "id", "name", "stuNum", "institute");
            beanToMap.put("sources", onlineUserIdToSources.get(user.getId()));
            res.add(beanToMap);
        }
        return res;
    }
}
