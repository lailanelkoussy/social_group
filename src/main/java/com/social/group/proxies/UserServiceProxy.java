package com.social.group.proxies;

import com.social.group.dtos.UserDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "UserService")
@RequestMapping(value = "/users")
public interface UserServiceProxy {

    @GetMapping(value = "/{id}")
    UserDTO getUser(
            @ApiParam(value = "Id of user", required = true) @PathVariable int id);




}
