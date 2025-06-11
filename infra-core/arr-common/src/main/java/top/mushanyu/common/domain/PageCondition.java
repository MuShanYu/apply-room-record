package top.mushanyu.common.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PageCondition implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final Integer PAGE_DEFAULT_VALUE = -1;
    private static final Integer LIMIT_DEFAULT_VALUE = Integer.MAX_VALUE;

    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    private String sortField;
    private String direction;

    private Integer page = PAGE_DEFAULT_VALUE;
    private Integer limit = LIMIT_DEFAULT_VALUE;
    private Boolean count = Boolean.TRUE;

    public boolean isPaging() {
        return !PAGE_DEFAULT_VALUE.equals(this.page) && !LIMIT_DEFAULT_VALUE.equals(this.limit);
    }

}
