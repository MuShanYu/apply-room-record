package top.mushanyu.system.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import top.mushanyu.system.dto.RoleDTO;

import java.util.List;

/**
 * @author Yulf
 * Date 2025/5/27
 */
@FeignClient(name = Info.NAME, path = Info.JAX_RS_PATH, url = Info.URL, contextId = Info.CONTEXT_ID, primary = false)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/role")
public interface RoleService {

    @POST
    RoleDTO save(RoleDTO roleDTO);

    @GET
    List<RoleDTO> findAll();
}
