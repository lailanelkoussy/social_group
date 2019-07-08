package com.social.group.controllers;

import com.social.group.entities.Group;
import com.social.group.services.GroupService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/groups")
@Api(value = "Group Management Service")
public class GroupController {

    @Autowired
    GroupService groupService;

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
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),})
    public ResponseEntity<Object> addNewGroup(
            @ApiParam(value = "Group object to create", required = true) @RequestBody Group group) {
        return new ResponseEntity<>(groupService.addNewGroup(group) ? HttpStatus.CREATED : HttpStatus.NOT_ACCEPTABLE);
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
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public ResponseEntity<Object> deleteGroup(
            @ApiParam(value = "Id of group", required = true) @PathVariable int groupId,
            @ApiParam(value = "Id of user performing the action", required = true) @PathVariable int userId) throws IllegalAccessException {
        return new ResponseEntity<>(groupService.deleteGroup(groupId, userId) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PatchMapping(value = "/{id}/name", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rename group")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public ResponseEntity<Object> renameGroup(
            @ApiParam(value = "Id of group", required = true) @PathVariable int id,
            @ApiParam(value = "New name to assign to the group", required = true) @RequestBody String newName) {
        return new ResponseEntity<>(groupService.renameGroup(id, newName) ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE);
    }

    @PatchMapping(value = "/{id}/description")
    @ApiOperation(value = "Change group description")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public ResponseEntity<Object> changeGroupDescription(
            @ApiParam(value = "Id of group", required = true) @PathVariable int id,
            @ApiParam(value = "New description for group", required = true) @RequestBody String newDescription) {
        return new ResponseEntity<>(groupService.changeGroupDescription(id, newDescription) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    //Members
    @PatchMapping(value = "/{id}/members")
    @ApiOperation(value = "Add new members to a group")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully updated object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public ResponseEntity<Object> addNewMembersToGroup(
            @ApiParam(value = "Id of group", required = true) @PathVariable int id,
            @ApiParam(value = "List of user ids of users to add") @RequestBody List<Integer> userIds) {
        return new ResponseEntity<>(groupService.addNewMembersToGroup(id, userIds) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    @PatchMapping(value = "/{id}/{removerId}/members/remove")
    @ApiOperation(value = "Remove members from group")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully updated object"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public ResponseEntity<Object> removeMembersFromGroup(
            @ApiParam(value = "Id of group", required = true) @PathVariable int id,
            @ApiParam(value = "Id of user performing the action", required = true) @PathVariable int removerId,
            @ApiParam(value = "List of user ids of users to remove") @RequestBody List<Integer> userIds) throws IllegalAccessException {
        return new ResponseEntity<>(groupService.removeGroupMembersFromGroup(id, removerId, userIds) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);

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

    @PatchMapping(value = "/user/{id}/{activate}")
    @ApiOperation(value = "Activate or deactivate groups created by a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public void activateOrDeactivateGroupsOfUser(
            @ApiParam(value = "Id of user") @PathVariable int userId,
            @ApiParam(value = "Boolean value indicating whether to activate or deactivate") @PathVariable boolean activate) {
        groupService.activateOrDeactivateGroupsOfUser(userId, activate);
    }
}
