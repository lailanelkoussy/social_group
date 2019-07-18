package com.social.group.controllers;

import com.social.group.dtos.CreateGroupDTO;
import com.social.group.dtos.GroupDTO;
import com.social.group.dtos.GroupPatchDTO;
import com.social.group.dtos.RemoveGroupDTO;
import com.social.group.entities.Group;
import com.social.group.services.GroupMemberService;
import com.social.group.services.GroupService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InvalidClassException;
import java.util.List;

@RestController
@RequestMapping(value = "/groups")
@Api(value = "Group Management Service")
public class GroupController {

    @Autowired
    GroupService groupService;

    @Autowired
    GroupMemberService groupMemberService;

    //Groups
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all groups", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get group by id", response = Group.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public Group getGroup(
            @ApiParam(value = "Id of group", required = true) @PathVariable int id) {
        return groupService.getGroup(id);
    }

    @PostMapping
    @ApiOperation(value = "Create new group")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully added object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 406, message = "Invalid group object")})
    public ResponseEntity<Object> addNewGroup(
            @ApiParam(value = "Group object to create", required = true) @RequestBody CreateGroupDTO group) throws InvalidClassException {
        groupService.addNewGroup(group);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/search/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Search for group using query", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public List<Group> searchForGroups(
            @ApiParam(value = "Search query", required = true) @PathVariable String query) {
        return groupService.searchForGroups(query);
    }

    @DeleteMapping(value = "/{groupId}/{userId}")
    @ApiOperation(value = "Delete group")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 406, message = "Invalid group object")})
    public ResponseEntity<Object> deleteGroup(
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId,
            @ApiParam(value = "Id of user performing the action", required = true) @PathVariable int userId) throws IllegalAccessException {
        groupService.deleteGroup(groupId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //User
    @GetMapping(value = "/user/{id}/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a user's groups")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public List<Group> getUserGroups(
            @ApiParam(value = "Id of user") @PathVariable int id) {
        return groupService.getUserGroups(id);
    }

    @GetMapping(value = "/user/{id}/ids", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a user's group ids")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public List<Integer> getUserGroupIds(
            @ApiParam(value = "Id of user") @PathVariable int id) {
        return groupService.getUserGroupIds(id);
    }

    @DeleteMapping(value = "/{id}/{removerId}/members/remove")
    @ApiOperation(value = "Remove members from group")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully updated object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public ResponseEntity<Object> removeMembersFromGroup(
            @ApiParam(value = "Id of group", required = true) @PathVariable int id,
            @ApiParam(value = "Id of user performing the action", required = true) @PathVariable int removerId,
            @ApiParam(value = "List of user ids of users to remove") @RequestBody RemoveGroupDTO removeGroupDTO) throws IllegalAccessException {
        groupService.removeGroupMembersFromGroup(id, removerId, removeGroupDTO.getRemove());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping(value = "/all/user/{userId}/activate/{activate}")
    @ApiOperation(value = "Activate or deactivate groups created by a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public void activateOrDeactivateGroupsOfUser(
            @ApiParam(value = "Id of user") @PathVariable int userId,
            @ApiParam(value = "Boolean value indicating whether to activate or deactivate") @PathVariable boolean activate) {
        groupService.activateOrDeactivateGroupsOfUser(userId, activate);
    }

    @PatchMapping(value = "/{groupId}/user/{userId}")
    @ApiOperation(value = "Rename group, change description, add members, or activate/deactivate group")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 403, message = "Not authorized to perform this action"),
            @ApiResponse(code = 406, message = "Invalid group object")})
    public void patchService(
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId,
            @ApiParam(value = "Id of user performing those actions") @PathVariable int userId,
            @ApiParam(value = "Group object containing only relevant field changes") @RequestBody GroupPatchDTO groupDTO) throws IllegalAccessException, InvalidClassException {
        groupService.patchService(userId, groupId, groupDTO);
    }

    @ApiOperation(value = "Delete a user's groups and group members")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted requests"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),})
    @DeleteMapping("/all/user/{userId}")
    public void deleteUsersGroupsAndMembers(
            @ApiParam(value = "User's id", required = true)
            @PathVariable int userId) throws IllegalAccessException {
        groupService.deleteUsersOwnedGroups(userId);
        groupMemberService.deleteUserMembers(userId);

    }


}
