package com.social.group.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "PhotoService")
@RequestMapping(value = "/photos")
public interface PhotoServiceProxy {
    @DeleteMapping(value = "/all/group/{groupId}")
    void deleteGroupsPhotos(@PathVariable int groupId);

    @DeleteMapping(value = "/all/group/{groupId}/user/{userId}")
    void deleteUsersGroupPhotos(@PathVariable int userId, @PathVariable int groupId);
}
