package com.social.group.controllers;

import com.social.group.entities.Request;
import com.social.group.services.RequestService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/requests")
@Api(value = "Group Management Service")
public class RequestController {

    @Autowired
    RequestService requestService;

    @GetMapping(value = "/{groupId}/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "View join requests to a group")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    public List<Request> viewRequests(
            @ApiParam(value = "Id of group", required = true)
            @PathVariable int groupId,
            @ApiParam(value = "Id of user performing this request", required = true)
            @PathVariable int userId) throws IllegalAccessException {
        return requestService.viewRequests(groupId, userId);
    }


    @DeleteMapping(value = "/{userId}/{requestId}/{accept}")
    @ApiOperation(value = "Accept or decline join request to group")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully accepted or declined request"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),})
    public ResponseEntity<Object> declineRequest(
            @ApiParam(value = "Id of request", required = true)
            @PathVariable int requestId,
            @ApiParam(value = "Id of user performing this request", required = true)
            @PathVariable int userId,
            @ApiParam(value = "boolean indicating whether to accept or decline", required = true) @PathVariable boolean accept) throws IllegalAccessException {
        return new ResponseEntity<>(requestService.acceptOrDeclineRequest(requestId, userId, accept) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/{groupId}/requests/{userId}")
    @ApiOperation(value = "Send join request to group")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully sent request"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),})
    public void sendRequest(
            @ApiParam(value = "Id of group", required = true)
            @PathVariable int groupId,
            @ApiParam(value = "Id of user performing this request", required = true)
            @PathVariable int userId) {
        requestService.sendRequest(groupId, userId);
    }


}
