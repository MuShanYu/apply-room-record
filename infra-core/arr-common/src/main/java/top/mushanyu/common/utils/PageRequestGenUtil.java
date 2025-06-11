package top.mushanyu.common.utils;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.RevisionSort;
import top.mushanyu.common.domain.PageCondition;

/**
 * @author MuShanYu
 * Date 2025/6/6
 */
public class PageRequestGenUtil {

    /**
     * 不指定排序方式默认按照createInstant降序排序
     */
    public static PageRequest getPageRequest(PageCondition cond) {
        if (!cond.isPaging()) {
            return null;
        }
        var sort = Sort.by(Sort.Direction.fromString(ObjectUtil.defaultIfBlank(cond.getDirection(), Sort.Direction.DESC.toString())),
                ObjectUtil.defaultIfBlank(cond.getSortField(), "id"));
        return PageRequest.of(cond.getPage() - 1, cond.getLimit(), sort);
    }

    /**
     * 按照指定的排序方式进行排序
     */
    public static PageRequest getPageRequest(PageCondition cond, Sort sort) {
        if (!cond.isPaging())
            return null;
        if (ObjUtil.isNull(sort))
            return PageRequest.of(cond.getPage() - 1, cond.getLimit());
        return PageRequest.of(cond.getPage() - 1, cond.getLimit(), sort);
    }
}
