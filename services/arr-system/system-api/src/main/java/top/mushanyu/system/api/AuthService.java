package top.mushanyu.system.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import top.mushanyu.system.dto.AuthDTO;
import top.mushanyu.system.dto.LoginDTO;
import top.mushanyu.system.dto.UserDTO;
import top.mushanyu.system.enums.RightType;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/6/4
 */
@FeignClient(name = Info.NAME, path = Info.JAX_RS_PATH, url = Info.URL, contextId = Info.CONTEXT_ID, primary = false)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/auth")
public interface AuthService {

    @GET
    @Path("/cur-user-action-rights")
    List<String> findCurUserActionRights();

    @GET
    @Path("/cur-user-info")
    UserDTO findCurUserInfo();

    @POST
    @Path("/login")
    AuthDTO login(LoginDTO dto);

}
