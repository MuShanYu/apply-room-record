package com.guet.ARC.common.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.user.UserRegisterDTO;
import com.guet.ARC.service.UserService;

import java.util.List;

public class UserExcelDataListener implements ReadListener<UserRegisterDTO> {
    private static final int BATCH_COUNT = 100;

    private List<UserRegisterDTO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private List<User> errorData;

    private UserService userService;

    private List<String> errorMsg;

    public UserExcelDataListener(UserService userService, List<User> errorData, List<String> errorMsg) {
        this.userService = userService;
        this.errorData = errorData;
        this.errorMsg = errorMsg;
    }

    @Override
    public void invoke(UserRegisterDTO data, AnalysisContext context) {
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            // 保存数据
            errorData.addAll(userService.batchRegister(cachedDataList, errorMsg));
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        errorData.addAll(userService.batchRegister(cachedDataList, errorMsg));
    }
}
