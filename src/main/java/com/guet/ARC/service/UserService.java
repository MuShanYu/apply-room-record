package com.guet.ARC.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.enmu.Device;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.user.*;
import com.guet.ARC.domain.enums.State;
import com.guet.ARC.netty.manager.UserOnlineManager;
import com.guet.ARC.util.RedisCacheUtil;
import com.guet.ARC.util.WxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

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
        User user = null;
        try {
            for (UserRegisterDTO userRegisterDTO : userRegisterDTOS) {
                // 初始化信息
                user = buildUser(userRegisterDTO, now);
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
                }
            }
        } catch (Exception e) {
            errorData.add(user);
            errorMsg.add(e.getMessage());
        }
        userRepository.saveAllAndFlush(successData);
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
            map.put("permissions", StpUtil.getPermissionList());
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
            if (StpUtil.getPermissionList().isEmpty()) {
                // 权限不足，踢出下线，抛出错误
                StpUtil.logout();
                throw new AlertException(1000, "没有权限登录后台管理");
            }
        } else {
            throw new AlertException(1000, "账号或者密码错误");
        }
        return map;
    }

    // 微信登录
    public Map<String, Object> wxLogin(String code) {
        // 获取openId
        String openid = WxUtils.getInstance().getOpenid(code);

        // 查询用户是否已经绑定了，没有绑定则无法登录
        if (StrUtil.isEmpty(openid)) {
            throw new AlertException(1000, "用户标识获取失败");
        }
        // 判断是否已经绑定
//        log.info("openId:{}", openid);
        Optional<User> userOptional = userRepository.findByOpenId(openid);
        Map<String, Object> map = new HashMap<>();
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            map.put("canWxLogin", true);
            login(map, user, Device.WECHAT.getDevice());
            map.put("roles", StpUtil.getRoleList());
            map.put("permissions", StpUtil.getPermissionList());
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
        String openid = WxUtils.getInstance().getOpenid(code);
        // 查询用户是否已经绑定了，没有绑定则无法登录
        if (StrUtil.isEmpty(openid)) {
            throw new AlertException(1000, "用户标识获取失败");
        }
        String userId = StpUtil.getLoginIdAsString();
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
        }
    }

    public void unBindWx(String code) {
        // 获取openId
        String openid = WxUtils.getInstance().getOpenid(code);
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

    public PageInfo<User> queryUserList(UserListQueryDTO queryDTO) {
        org.springframework.data.domain.Page<User> pageData = userRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StrUtil.isNotEmpty(queryDTO.getName())) {
                predicates.add(cb.like(root.get("name"), queryDTO.getName() + "%"));
            }
            if (StrUtil.isNotEmpty(queryDTO.getStuNum())) {
                predicates.add(cb.like(root.get("stuNum"), queryDTO.getStuNum()));
            }
            if (StrUtil.isNotEmpty(queryDTO.getInstitute())) {
                predicates.add(cb.like(root.get("institute"), queryDTO.getInstitute() + "%"));
            }
            query.orderBy(cb.desc(root.get("createTime")));
            return cb.and(predicates.toArray(Predicate[]::new));
        }, PageRequest.of(queryDTO.getPage() - 1, queryDTO.getSize()));
        pageData.getContent().forEach(user -> {
            user.setPwd("");
            user.setOpenId("");
        });
        return new PageInfo<>(pageData);
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

    public void updatePersonalInfo(Map<String, String> userInfo) {
        String userIdContext = StpUtil.getLoginIdAsString();
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
            User updateUserInfo = CglibUtil.copy(user, User.class);
            CglibUtil.fillBean(userInfo, updateUserInfo);
            updateUserInfo.setUpdateTime(System.currentTimeMillis());
            userRepository.save(updateUserInfo);
        });
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
            if (CollectionUtil.isNotEmpty(StpUtil.getPermissionList())) {
                return user;
            } else {
                throw new AlertException(1000, name + "非后台管理员，无法设置成负责人");
            }
        } else {
            throw new AlertException(1000, "学号/工号错误，或者负责人" + stuNum + name + "未注册");
        }
    }

    // 由于spring的事务传播机制,save方法是一个事务，会把外层添加进事务，触发脏检查
    public void updateUserName(UserUpdateNameDTO userUpdateNameDTO) {
        User user = userRepository.findByIdOrElseNull(userUpdateNameDTO.getUserId());
        User copiedUser = CglibUtil.copy(user, User.class);
        copiedUser.setName(userUpdateNameDTO.getName());
        copiedUser.setId(userUpdateNameDTO.getUserId());
        copiedUser.setUpdateTime(System.currentTimeMillis());
        userRepository.save(copiedUser);
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
        res.put("userInfo", userRepository.findByIdOrElseNull(userId));
        res.put("roles", StpUtil.getRoleList());
        res.put("permissions", StpUtil.getPermissionList());
        return res;
    }

    public List<Map<String, Object>> getOlineUserList() {
        List<Map<String, Object>> res = new ArrayList<>();
        ConcurrentMap<String, List<String>> onlineUserIdToSources = UserOnlineManager.getOnlineUserIdToSources();
        for (User user : userRepository.findAllById(onlineUserIdToSources.keySet())) {
            Map<String, Object> beanToMap = BeanUtil.beanToMap(user, "id", "name", "stuNum", "institute");
            beanToMap.put("sources", onlineUserIdToSources.get(user.getId()));
            res.add(beanToMap);
        }
        return res;
    }

    public List<User> findUserByIds(List<String> ids) {
        return userRepository.findAllById(ids);
    }
    public Map<String, Object> getUserPermissionAndRole() {
        Map<String, Object> res = new HashMap<>();
        res.put("roles", StpUtil.getRoleList());
        res.put("permission", StpUtil.getPermissionList());
        return res;
    }

    public void resetUserPwd(String newPwd, String userId) {
        User copiedUser = CglibUtil.copy(userRepository.findByIdOrElseNull(userId), User.class);
        copiedUser.setPwd(SaSecureUtil.md5(newPwd));
        copiedUser.setUpdateTime(System.currentTimeMillis());
        userRepository.save(copiedUser);
    }

    public void updateUserInfoAdmin(User userInfo) {
        User copiedUser = CglibUtil.copy(userRepository.findByIdOrElseNull(userInfo.getId()), User.class);
        Map<String, Object> updateMap = new HashMap<>();
        BeanUtil.beanToMap(userInfo, updateMap, false, true);
        CglibUtil.fillBean(updateMap, copiedUser);
        userRepository.findByStuNum(copiedUser.getStuNum()).ifPresent(u -> {
            if (!u.getId().equals(copiedUser.getId())) {
                throw new AlertException(1000, "学号" + copiedUser.getStuNum() + "已被其他用户注册");
            }
        });
        userRepository.findByMail(copiedUser.getMail()).ifPresent(u -> {
            if (!u.getId().equals(copiedUser.getId())) {
                throw new AlertException(1000, "邮箱" + copiedUser.getMail() + "已被其他用户注册");
            }
        });
        copiedUser.setUpdateTime(System.currentTimeMillis());
        userRepository.save(copiedUser);
    }

    public User findUserById(String userId) {
        return userRepository.findByIdOrElseNull(userId);
    }

    public List<Dict> findUserByName(String name) {
        // 姓名可能会重复，全部返回
        List<User> users = userRepository.findByNameAndState(name, State.ACTIVE);
        List<Dict> res = new ArrayList<>();
        for (User user : users) {
            Dict dict = Dict.create();
            dict.set("userId", user.getId());
            dict.set("name", user.getName());
            dict.set("stuNum", user.getStuNum());
            res.add(dict);
        }
        return res;
    }
}
