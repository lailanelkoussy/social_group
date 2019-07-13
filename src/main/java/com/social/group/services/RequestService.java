package com.social.group.services;

import com.social.group.entities.Group;
import com.social.group.entities.Request;
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

    public void sendRequest(int groupId, int userId) {

        if (!requestAlreadyExists(groupId, userId)) {

            Group group = groupService.getGroup(groupId);
            Set<Request> requestList = group.getRequests(); //todo this is a very HEAVY operation, you should just create a new request and save it, that's it !
            Request request = new Request(userId,group);

            requestList.add(request);
            requestRepository.save(request);
            groupService.updateGroup(group);
        }
    }

    public List<Request> viewRequests(int groupId, int userId) throws IllegalAccessException {

        Group group = groupService.getGroup(groupId);

        if (group.getCreatorId() == userId) {
            return requestRepository.findAllByGroupId(groupId);

        } else throw (new  IllegalAccessException("Not authorized to perform this action"));
    }

    private boolean acceptRequest(int request_id, int userId) throws IllegalAccessException {
        Optional<Request> requestOptional = requestRepository.findById(request_id);

        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            Group group = request.getGroup();

            if (group.getCreatorId() == userId) {
                List<Integer> userIdList = new ArrayList<>();
                userIdList.add(request.getUserId());

                groupService.addNewMembersToGroup(group.getId(), userIdList);

                requestRepository.deleteById(request_id);
                return true;

            } else {
                log.error("Not allowed to perform this action");
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }

        } else {
            log.error("Request to join not found");
            throw (new EntityNotFoundException("Request to join not found"));
        }
    }
    public boolean acceptOrDeclineRequest( int requestId, int userId, boolean accept) throws IllegalAccessException {
        return accept? acceptRequest(requestId, userId): declineRequest(requestId, userId);
    }

    private boolean declineRequest(int request_id, int userId) throws IllegalAccessException {

        Optional<Request> requestOptional = requestRepository.findById(request_id);
        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            Group group = request.getGroup();

            if (group.getCreatorId() == userId || request.getUserId() == userId) {
                Set<Request> requests = group.getRequests();
                requests.remove(request);

                requestRepository.delete(request);

                group.setRequests(requests);
                groupService.updateGroup(group);

                return true;

            } else {
                log.error("Not allowed to perform this action");
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }

        } else {
            log.error("Request to join not found");
            throw (new EntityNotFoundException("Request to join not found"));
        }
    }

    public void deleteRequests(Set<Request> requests){
        requestRepository.deleteAll(requests);
    }

    private boolean requestAlreadyExists(int groupId, int userId) {
        return (requestRepository.countAllByUserIdAndGroupId(userId, groupId) != 0); // todo why not find all by ?

    }
}
