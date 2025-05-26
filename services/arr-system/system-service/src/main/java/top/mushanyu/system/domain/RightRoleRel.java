package top.mushanyu.system.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Yulf
 * Date 2025/5/23
 */
@Entity
@Table(name = "right_role_rel")
@Getter
@Setter
@IdClass(RightRoleRel.class)
public class RightRoleRel {

    @Id
    private Long rightId;

    @Id
    private Long roleId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RightRoleRel that = (RightRoleRel) o;
        return Objects.equals(rightId, that.rightId) && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rightId, roleId);
    }
}
