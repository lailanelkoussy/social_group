package com.social.group.services;

import com.social.group.entities.Group;
import com.social.group.entities.Request;
import com.social.group.proxies.UserServiceProxy;
import com.social.group.repos.RequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class RequestService {

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    UserServiceProxy userServiceProxy;

    public void sendRequest(int groupId, int userId) {

        if (!requestAlreadyExists(groupId, userId)) {
            try {
                userServiceProxy.getUser(userId);
            } catch (Exception e){
                throw new EntityNotFoundException("User not found");
            }
            log.info("Sending request...");
            Group group = groupService.getGroup(groupId);
            Request request = new Request(userId, group);
            requestRepository.save(request);
        }
    }

    public List<Request> viewRequests(int groupId, int userId) throws IllegalAccessException {

        Group group = groupService.getGroup(groupId);

        if (group.getCreatorId() == userId) {
            log.info("Retrieving requests...");
            return requestRepository.findAllByGroupId(groupId);

        } else throw (new IllegalAccessException("Not authorized to perform this action"));
    }

    private void acceptRequest(int request_id, int userId) throws IllegalAccessException {
        Optional<Request> requestOptional = requestRepository.findById(request_id);
        log.info("Retrieving request...");

        if (requestOptional.isPresent()) {
            log.info("Request retrieved");
            Request request = requestOptional.get();
            Group group = request.getGroup();

            if (group.getCreatorId() == userId) {
                List<Integer> userIdList = new ArrayList<>();
                userIdList.add(request.getUserId());
                groupService.addNewMembersToGroup(group.getId(), userIdList);
                requestRepository.deleteById(request_id);
                log.info("Request accepted");


            } else {
                log.error("Not allowed to perform this action");
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }
        } else {
            log.error("Request to join not found");
            throw (new EntityNotFoundException("Request to join not found"));
        }
    }

    public void acceptOrDeclineRequest(int requestId, int userId, boolean accept) throws IllegalAccessException {
        if (accept) {
            acceptRequest(requestId, userId);
        } else {
            declineRequest(requestId, userId);
        }
    }

    private void declineRequest(int request_id, int userId) throws IllegalAccessException {

        Optional<Request> requestOptional = requestRepository.findById(request_id);
        log.info("Retrieving request...");
        if (requestOptional.isPresent()) {
            log.info("Request retrieved");
            Request request = requestOptional.get();
            Group group = request.getGroup();

            if (group.getCreatorId() == userId || request.getUserId() == userId) {
                Set<Request> requests = group.getRequests();
                requests.remove(request);
                requestRepository.delete(request);
                group.setRequests(requests);
                groupService.updateGroup(group);
                log.info("Request declined");

            } else {
                log.error("Not allowed to perform this action");
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }

        } else {
            log.error("Request to join not found");
            throw (new EntityNotFoundException("Request to join not found"));
        }
    }

    void deleteRequests(Set<Request> requests) {
        requestRepository.deleteAll(requests);
    }

    private boolean requestAlreadyExists(int groupId, int userId) {
        return (requestRepository.countAllByUserIdAndGroupId(userId, groupId) != 0);

    }

    public void deleteUsersRequests(int userId){
        requestRepository.deleteAllByUserId(userId);
    }
}
