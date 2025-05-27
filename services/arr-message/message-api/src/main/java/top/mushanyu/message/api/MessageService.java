package top.mushanyu.message.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import top.mushanyu.message.dto.MessageDTO;

import java.util.List;

/**
 * @author Yulf
 * Date 2025/5/27
 */
@FeignClient(name = Info.NAME, path = Info.JAX_RS_PATH, url = Info.URL, contextId = Info.CONTEXT_ID, primary = false)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/message")
public interface MessageService {

    @POST
    MessageDTO save(MessageDTO message);

    @GET
    List<MessageDTO> findAll();
}
