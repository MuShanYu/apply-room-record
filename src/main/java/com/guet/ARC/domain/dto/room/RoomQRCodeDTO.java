package com.guet.ARC.domain.dto.room;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Yulf
 * Date 2024/9/19
 */
@Getter
@Setter
@ToString
public class RoomQRCodeDTO {

    private List<String> roomIds;

    /**
     * 是否覆盖已有的文件
     */
    private boolean cover;
}
