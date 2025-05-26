package top.mushanyu.system.domain;

import lombok.*;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_role_rel" )
@Getter
@Setter
@IdClass(UserRoleRel.class)
public class UserRoleRel {

    @Id
    private String userId;

    @Id
    private String roleId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleRel that = (UserRoleRel) o;
        return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}