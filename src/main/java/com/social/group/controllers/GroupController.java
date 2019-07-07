package com.social.group.controllers;

import com.social.group.entities.Group;
import com.social.group.entities.Request;
import com.social.group.services.GroupService;
import com.social.group.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/groups")
public class GroupController { //todo separate to groups and requests controllers

    @Autowired
    GroupService groupService;

    @Autowired
    RequestService requestService;

    //Groups
    @GetMapping//todo this is wrong, no one should have access like this!
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @PostMapping
    public ResponseEntity<Object> addNewGroup(@RequestBody Group group) {//todo where is the validation?
        return new ResponseEntity<>(groupService.addNewGroup(group) ? HttpStatus.CREATED : HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping(value = "/{id}")
    public Group getGroup(@PathVariable int id) {
        return groupService.getGroup(id);
    }

    @GetMapping(value = "/search/{query}")
    public List<Group> searchForGroups(@PathVariable String query) {
        return groupService.searchForGroups(query);
    }

    @DeleteMapping(value = "/{groupId}/{userId}") //todo endpoint should be changed
    public ResponseEntity<Object> deleteGroup(@PathVariable int groupId, @PathVariable int userId) {
        return new ResponseEntity<>(groupService.deleteGroup(groupId, userId) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/user/{id}/")//todo rename
    public List<Group> getUserGroups(@PathVariable int id) {
        return groupService.getUserGroups(id);
    }

    @PatchMapping(value = "/{id}/name")
    public ResponseEntity<Object> renameGroup(@PathVariable int id, @RequestBody String newName) {
        return new ResponseEntity<>(groupService.renameGroup(id, newName) ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE);
    }

    @PatchMapping(value = "/{id}/description")
    public ResponseEntity<Object> changeGroupDescription(@PathVariable int id, @RequestBody String newDescription) {
        return new ResponseEntity<>(groupService.changeGroupDescription(id, newDescription) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    //Members
    @PatchMapping(value = "/{id}/members")
    public ResponseEntity<Object> addNewMembersToGroup(@PathVariable int id, @RequestBody List<Integer> userIds) {
        return new ResponseEntity<>(groupService.addNewMembersToGroup(id, userIds) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    @PatchMapping(value = "/{id}/{removerId}/members/remove")
    public ResponseEntity<Object> removeMembersFromGroup(@PathVariable int id, @PathVariable int removerId, @RequestBody List<Integer> userIds) {
        return new ResponseEntity<>(groupService.removeGroupMembersFromGroup(id, removerId, userIds) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    //Requests
    @GetMapping(value = "/{id}/requests")
    public List<Request> viewRequests(@PathVariable int id, @RequestBody int userId) {
        return requestService.viewRequests(id, userId);
    }

    @PatchMapping(value = "/request/{userId}/{requestId}/")
    public ResponseEntity<Object> acceptRequest(@PathVariable int requestId, @PathVariable int userId) {
        return new ResponseEntity<>(requestService.acceptRequest(requestId, userId) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/request/{userId}/{requestId}/")
    public ResponseEntity<Object> declineRequest(@PathVariable int requestId, @PathVariable int userId) {
        return new ResponseEntity<>(requestService.declineRequest(requestId, userId) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);

    }

    @PostMapping(value = "/{id}/requests/{userId}")
    public void sendRequest(@PathVariable int id, @PathVariable int userId) {
        requestService.sendRequest(id, userId);
    }

    //User
    @PatchMapping(value = "/user/{id}/{activate}")
    public void activateOrDeactivateGroupsOfUser(@PathVariable int userId, @PathVariable boolean activate) {
        groupService.activateOrDeactivateGroupsOfUser(userId, activate);
    }


}
