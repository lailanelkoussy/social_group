package com.social.group.services;

import com.social.group.dtos.CreateGroupDTO;
import com.social.group.dtos.GroupDTO;
import com.social.group.dtos.GroupPatchDTO;
import com.social.group.entities.Group;
import com.social.group.entities.GroupMember;
import com.social.group.entities.GroupMemberCK;
import com.social.group.entities.Request;
import com.social.group.proxies.PhotoServiceProxy;
import com.social.group.proxies.UserServiceProxy;
import com.social.group.repos.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityNotFoundException;
import java.io.InvalidClassException;
import java.util.*;

@Service
@Slf4j
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupMemberService groupMemberService;

    @Autowired
    RequestService requestService;

    @Autowired
    UserServiceProxy userServiceProxy;

    @Autowired
    PhotoServiceProxy photoServiceProxy;

    ModelMapper modelMapper = new ModelMapper();

    public List<Group> getAllGroups() {
        log.info("Retrieving all groups");
        return groupRepository.findAll();
    }

    public Group getGroup(int groupId) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        log.info("Retrieving group...");

        if (groupOptional.isPresent()) {
            log.info("Group retrieved");
            return groupOptional.get();

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }
    }

    public List<Group> searchForGroups(String query) {
        log.info("Searching for groups");
        List<Group> groups = groupRepository.findAllByNameContainingIgnoreCase(query);
        for (Group group : groups) {
            if(!group.isActive())
                groups.remove(group);
        }
        return groups;
    }

    public void addNewGroup(CreateGroupDTO groupDTO) throws InvalidClassException {

        try {
            userServiceProxy.getUser(groupDTO.getCreatorId());
        } catch (Exception e) {
            throw new EntityNotFoundException("User not found");
        }

        log.info("Checking for group name conflicts");

        if (noNameConflict(groupDTO.getName())) {
            log.info("There is no group name conflicts");
            Group group = new Group();
            modelMapper.map(groupDTO, group);
            log.info("Saving group...");
            int groupId = groupRepository.save(group).getId();
            GroupMember groupMember = new GroupMember();
            GroupMemberCK groupMemberCK = new GroupMemberCK();
            groupMemberCK.setUserId(groupDTO.getCreatorId());
            groupMemberCK.setGroup(group);
            groupMember.setCompositeKey(groupMemberCK);

            List<Integer> creator = new ArrayList<>();
            creator.add(groupDTO.getCreatorId());
            addNewMembersToGroup(groupId, creator);

        } else {
            log.error("There already exists group with this name");
            throw (new InvalidClassException("There already exists group with this name"));
        }
    }

    public void deleteGroup(int groupId, int userId) throws IllegalAccessException {

        Optional<Group> groupOptional = groupRepository.findById(groupId);
        log.info("Retrieving group...");
        if (groupOptional.isPresent()) {
            log.info("Group retrieved");
            Group group = groupOptional.get();
            log.info("Deleting group...");
            if (group.getCreatorId() == userId) {
                Set<Request> requests = group.getRequests();
                requestService.deleteRequests(requests);
                groupMemberService.removeGroupMembers(group.getMembers());
                groupRepository.delete(group);
                photoServiceProxy.deleteGroupsPhotos(groupId);
            } else {
                log.error("Not authorized to perform this action");
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }
        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }
    }

    void updateGroup(Group group) {
        log.info("Updating group...");
        groupRepository.save(group);
    }

    void addNewMembersToGroup(int group_id, List<Integer> userIds) {
        Optional<Group> groupOptional = groupRepository.findById(group_id);
        log.info("Retrieving group...");
        if (groupOptional.isPresent()) {

            log.info("Group retrieved");
            Group group = groupOptional.get();
            Set<GroupMember> groupMembers = group.getMembers();

            log.info("Adding new members to group");
            for (int userId : userIds) {
                try {
                    userServiceProxy.getUser(userId);
                } catch (Exception e) {
                    throw new EntityNotFoundException("Could not find user");
                }
                GroupMemberCK compositeKey = new GroupMemberCK();
                compositeKey.setGroup(group);
                compositeKey.setUserId(userId);
                GroupMember groupMember = new GroupMember();
                groupMember.setCompositeKey(compositeKey);

                groupMembers.add(groupMember);
            }

            groupMemberService.addGroupMembers(groupMembers);
            group.setMembers(groupMembers);
            groupRepository.save(group);

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));

        }
    }

    public void removeGroupMembersFromGroup(int groupId, int removerId, List<Integer> userIds) throws IllegalAccessException {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        log.info("Retrieving group...");
        if (groupOptional.isPresent()) {
            log.info("Group retrieved");
            Group group = groupOptional.get();
            if (group.getCreatorId() == removerId) {
                Set<GroupMember> groupMembers = group.getMembers();
                Set<GroupMember> groupMembersToRemove = new HashSet<>();

                log.info("Removing groups");
                for (int userId : userIds) {
                    if (userId == group.getCreatorId()) {
                        log.warn("You cannot remove creator from their group");

                    } else {
                        GroupMember groupMember = groupMemberService.getGroupMember(groupId, userId);
                        groupMembers.remove(groupMember);
                        groupMembersToRemove.add(groupMember);
                        photoServiceProxy.deleteUsersGroupPhotos(userId, groupId);
                    }
                }
                group.setMembers(groupMembers);
                groupRepository.save(group);

                groupMemberService.removeGroupMembers(groupMembersToRemove);

            } else {
                log.error("Not authorized to perform this action.");
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }
    }

    private void renameGroup(int groupId, String newName) throws InvalidClassException {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        log.info("Retrieving group");
        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            log.info("Checking for name conflict");
            if (noNameConflict(newName)) {
                log.info("Creating group");
                group.setName(newName);
                groupRepository.save(group);

            } else {
                log.error("Name already taken by another group");
                throw (new InvalidClassException("Name already taken by another group"));

            }
        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }
    }

    private void changeGroupDescription(int groupId, String newDescription) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        log.info("Retrieving group");
        if (groupOptional.isPresent()) {
            log.info("Group retrieved");
            Group group = groupOptional.get();
            group.setDescription(newDescription);
            groupRepository.save(group);
            log.info("Updating group");

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }

    }

    public List<Group> getUserGroups(int userId) {
        log.info("Getting groups");
        return groupMemberService.getGroupsWithUser(userId);
    }

    public List<Integer> getUserGroupIds(int userId) {

        List<Group> groups = getUserGroups(userId);
        List<Integer> groupIds = new ArrayList<>();

        for (Group group : groups) {
            groupIds.add(group.getId());
        }
        return groupIds;
    }

    public void activateOrDeactivateGroupsOfUser(int user_id, boolean activate) {
        List<Group> groups = getUserGroups(user_id);
        log.info("Retrieving groups..");
        for (Group group : groups) {
            group.setActive(activate);
        }
        groupRepository.saveAll(groups);
    }

    private void activateOrDeactivateGroup(int groupId, boolean activate) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        log.info("Retrieving group...");

        if (groupOptional.isPresent()) {
            log.info("Group found");
            Group group = groupOptional.get();
            group.setActive(activate);
            log.info("Updating group");
            groupRepository.save(group);

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }
    }

    public void patchService(int userId, int groupId, GroupPatchDTO groupDTO) throws IllegalAccessException, InvalidClassException {

        Optional<Group> groupOptional = groupRepository.findById(groupId);
        log.info("Retrieving group...");

        if (groupOptional.isPresent()) {
            log.info("Group retrieved");
            Group group = groupOptional.get();
            if (group.getCreatorId() == userId) {

                //renaming option
                if (groupDTO.getName() != null)
                    renameGroup(groupId, groupDTO.getName());

                //adding members
                if (groupDTO.getGroupMembersIds() != null)
                    addNewMembersToGroup(groupId, groupDTO.getGroupMembersIds());

                //changing description
                if (groupDTO.getDescription() != null)
                    changeGroupDescription(groupId, groupDTO.getDescription());

                //activating or deactivating groups
                if (groupDTO.getActivate() != -1)
                    activateOrDeactivateGroup(groupId, groupDTO.getActivate() != 0);


            } else {
                log.error("Not authorized to perform this action");
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }
    }

    private boolean noNameConflict(String groupName) {
        return (groupRepository.countAllByName(groupName) == 0);
    }

    public void deleteUsersOwnedGroups(int userId) throws IllegalAccessException {
        List<Group> userGroups = groupRepository.findAllByCreatorId(userId);
        for (Group group : userGroups)
            deleteGroup(group.getId(), userId);
    }

}
