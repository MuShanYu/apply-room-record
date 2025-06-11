package top.mushanyu.system.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import top.mushanyu.common.domain.PageQueryResult;
import top.mushanyu.system.cnodition.RoleCondition;
import top.mushanyu.system.dto.RoleDTO;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@FeignClient(name = Info.NAME, path = Info.JAX_RS_PATH, url = Info.URL, contextId = Info.CONTEXT_ID, primary = false)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/role")
public interface RoleService {

    @POST
    RoleDTO save(RoleDTO roleDTO);

    @POST
    @Path("/by-condition")
    PageQueryResult<RoleDTO> findByCondition(RoleCondition condition);

    @PUT
    RoleDTO update(RoleDTO roleDTO);

    @DELETE
    RoleDTO delete(@QueryParam("id") Long id);

    @PUT
    @Path("/recover")
    RoleDTO recover(@QueryParam("id") Long id);
}
