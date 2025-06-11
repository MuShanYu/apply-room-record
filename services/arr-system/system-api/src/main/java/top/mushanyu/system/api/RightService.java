package top.mushanyu.system.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import top.mushanyu.system.dto.RightDTO;
import top.mushanyu.system.enums.RightType;

import java.util.List;

/**
 * @author Yulf
 * Date 2025/6/4
 */
@FeignClient(name = Info.NAME, path = Info.JAX_RS_PATH, url = Info.URL, contextId = Info.CONTEXT_ID, primary = false)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/rights")
public interface RightService {

    @GET
    @Path("/cur-user-rights")
    List<String> findCurrentUserRights(@QueryParam("type")RightType type);

    @GET
    @Path("/all")
    List<RightDTO> findAllRights();

    @GET
    @Path("/by-role-id")
    List<RightDTO> findRightsByRoleId(@QueryParam("roleId") Long roleId);

    @POST
    @Path("/save")
    RightDTO save(RightDTO right);

    @PUT
    @Path("/update")
    RightDTO update(RightDTO right);
}
