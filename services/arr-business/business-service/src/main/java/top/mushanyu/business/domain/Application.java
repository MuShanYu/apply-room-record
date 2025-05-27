package top.mushanyu.business.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import top.mushanyu.business.enums.ApplicationState;
import top.mushanyu.business.enums.ApplicationType;
import top.mushanyu.config.domain.AbstractModel;

@Entity
@Table(name = "application" )
@Getter
@Setter
public class Application  extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String reason;

    // 数据库为_连接命名，实体类使用驼峰明明，jpa默认遇到大写字母转为_连接，与数据库对应。
    @Enumerated(EnumType.ORDINAL)
    private ApplicationType applicationType;

    private Long matterRecordId;

    private Long handleUserId;

    private Long applyUserId;

    @Enumerated(EnumType.ORDINAL)
    private ApplicationState state;

    private String remarks;
}