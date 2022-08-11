package com.guet.ARC.common.constant;

public interface CommonConstant {

    Short STATE_NEGATIVE = -1;//删除状态

    Short STATE_ACTIVE = 1;//状态正常

    Short ROOM_RESERVE_TO_BE_REVIEWED = 0;// 房间预约待审批状态

    Short ROOM_RESERVE_ALREADY_REVIEWED = 2;// 房间预约审批通过状态

    Short ROOM_RESERVE_CANCELED = 3; // 用户房间预约取消状态

    Short ROOM_RESERVE_TO_BE_REJECTED = 4; // 房间预约审批被驳回

    Short ROOM_CAN_NOT_BE_RESERVED = 5; // 房间不可被预约

    String ROLE_USER_ID = "0";

    String ROLE_ADMIN_ID = "1";

    String ROLE_SUPER_ADMIN_ID = "2";

    Integer MAX_PERSONAL_UPDATE_NUM = 1; // 最大个人信息修改次数，每24小时

    String USER_ROLE = "user";

    String ADMIN_ROLE= "admin";

    String SUPER_ADMIN_ROLE = "super-admin";
}
