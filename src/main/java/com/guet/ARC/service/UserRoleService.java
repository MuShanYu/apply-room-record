package com.guet.ARC.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.RoleRepository;
import com.guet.ARC.dao.SysMenuRoleRepository;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.dao.UserRoleRepository;
import com.guet.ARC.domain.Role;
import com.guet.ARC.domain.SysMenuRole;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.UserRole;
import com.guet.ARC.domain.dto.role.CancelGrantRoleDTO;
import com.guet.ARC.domain.dto.role.GrantRoleToUserDTO;
import com.guet.ARC.domain.enums.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SysMenuRoleRepository sysMenuRoleRepository;

    @Autowired
    private UserRepository userRepository;

    public PageInfo<Role> queryRoleList(Integer page, Integer size, String roleDes) {
        Page<Role> pageData;
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("createTime").descending());
        if (StrUtil.isEmpty(roleDes)) {
            pageData = roleRepository.findByState(State.ACTIVE, pageRequest);
        } else {
            pageData = roleRepository.findByRoleDesIsLikeAndState("%" + roleDes + "%", State.ACTIVE, pageRequest);
        }
        return new PageInfo<>(pageData);
    }

    public Role updateRole(Role role) {
        Role roleInDB = roleRepository.findByIdOrElseNull(role.getId());
        Map<String, Object> updateMap = BeanUtil.beanToMap(role, false, true);
        BeanUtil.copyProperties(updateMap, roleInDB);
        roleInDB.setUpdateTime(System.currentTimeMillis());
        return roleRepository.save(roleInDB);
    }

    public Role save(Role role) {
        if (roleRepository.existsByRoleName(role.getRoleName())) {
            throw new AlertException(1000, role.getRoleName() + "已经存在。");
        }
        role.setId(IdUtil.fastSimpleUUID());
        role.setUpdateTime(System.currentTimeMillis());
        role.setCreateTime(System.currentTimeMillis());
        role.setState(State.ACTIVE);
        return roleRepository.save(role);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void delRole(String roleId) {
        // menu role
        List<String> menuRoleIds = sysMenuRoleRepository.findByRoleIdIn(CollectionUtil.toList(roleId)).stream()
                .map(SysMenuRole::getId)
                .collect(Collectors.toList());
        // user role
        List<String> userRoleIds = userRoleRepository.findByRoleIdIn(CollectionUtil.toList(roleId)).stream()
                .map(UserRole::getId)
                .collect(Collectors.toList());
        // role
        Role role = roleRepository.findByIdOrElseNull(roleId);
        role.setUpdateTime(System.currentTimeMillis());
        role.setState(State.DEL);
        roleRepository.save(role);
        sysMenuRoleRepository.deleteAllById(menuRoleIds);
        userRoleRepository.deleteAllById(userRoleIds);
    }

    public PageInfo<User> queryRoleGrantedUser(String roleId, String stuNum, Integer page, Integer size) {
        Page<UserRole> pageData = userRoleRepository.findByRoleId(roleId, PageRequest.of(page - 1, size));
        List<String> userIds = pageData.getContent().stream()
                .map(UserRole::getUserId)
                .distinct().collect(Collectors.toList());
        PageInfo<User> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        List<User> userList = userRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StrUtil.isNotEmpty(stuNum)) {
                predicates.add(criteriaBuilder.equal(root.get("stuNum"), stuNum));
            }
            predicates.add(criteriaBuilder.in(root.get("id")).value(userIds));
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        });
        pageInfo.setTotalSize(StrUtil.isNotEmpty(stuNum) ? userList.size() : pageData.getTotalElements());
        pageInfo.setPageData(userList);
        return pageInfo;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void cancelRoleGrant(CancelGrantRoleDTO dto) {
        log.info("dto: {}", dto);
        List<UserRole> userRoles = userRoleRepository.findByRoleIdAndAndUserIdIn(dto.getRoleId(), dto.getUserIds());
        userRoleRepository.deleteAllInBatch(userRoles);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void grantUserRole(String stuNum, String roleId) {
        Optional<User> userOptional = userRepository.findByStuNum(stuNum);
        if (userOptional.isEmpty()) {
            throw new AlertException(ResultCode.USER_NOT_EXIST);
        }
        User user = userOptional.get();
        UserRole userRole = new UserRole();
        userRole.setId(IdUtil.fastSimpleUUID());
        userRole.setCreateTime(System.currentTimeMillis());
        userRole.setUpdateTime(System.currentTimeMillis());
        userRole.setState(State.ACTIVE);
        userRole.setRoleId(roleId);
        userRole.setUserId(user.getId());
        userRoleRepository.save(userRole);
    }

    public List<Role> queryRoleByUserId(String userId) {
        List<String> roleIds = userRoleRepository.findByUserIdAndState(userId, State.ACTIVE).stream()
                .map(UserRole::getRoleId)
                .distinct().collect(Collectors.toList());
        return roleRepository.findAllById(roleIds);
    }

    public void grantRoleToUser(GrantRoleToUserDTO dto) {
        log.info("dot: {}", dto);
        // 删除原来的，赋予新的
        List<UserRole> userRoles = userRoleRepository.findByUserIdAndState(dto.getUserId(), State.ACTIVE);
        log.info("userRoles {}", userRoles);
        userRoleRepository.deleteAllInBatch(userRoles);
        // 赋予
        long now = System.currentTimeMillis();
        List<UserRole> added = new ArrayList<>();
        dto.getRoleIds().forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setId(IdUtil.fastSimpleUUID());
            userRole.setCreateTime(now);
            userRole.setUpdateTime(now);
            userRole.setState(State.ACTIVE);
            userRole.setRoleId(roleId);
            userRole.setUserId(dto.getUserId());
            added.add(userRole);
        });
        userRoleRepository.saveAll(added);
    }
}
