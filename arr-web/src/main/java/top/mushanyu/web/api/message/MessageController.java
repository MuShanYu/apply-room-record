package top.mushanyu.web.api.message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.mushanyu.message.api.MessageService;
import top.mushanyu.message.dto.MessageDTO;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Tag(name = "消息管理")
@RestController
@RequestMapping(Info.V1 + "/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "保持消息")
    public MessageDTO save(@RequestBody MessageDTO dto) {
        return messageService.save(dto);
    }

    @GetMapping
    @Operation(summary = "查询所有消息")
    public List<MessageDTO> findAll() {
        return messageService.findAll();
    }
}
