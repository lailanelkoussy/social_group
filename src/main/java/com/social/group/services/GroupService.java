package com.social.group.services;

import com.social.group.dtos.GroupDTO;
import com.social.group.entities.Group;
import com.social.group.entities.GroupMember;
import com.social.group.entities.GroupMemberCK;
import com.social.group.entities.Request;
import com.social.group.repos.GroupRepository;
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
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupMemberService groupMemberService;

    @Autowired
    RequestService requestService;

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroup(int groupId) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {

            return groupOptional.get();

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }

    }

    public List<Group> searchForGroups(String query) {
        return groupRepository.findAllByNameContainingIgnoreCase(query);
    }

    public boolean addNewGroup(Group group) {
        if (noNameConflict(group.getName())) {
            groupRepository.save(group);
            int groupId = groupRepository.save(group).getId();

            GroupMember groupMember = new GroupMember();
            GroupMemberCK groupMemberCK = new GroupMemberCK();
            groupMemberCK.setUserId(group.getCreatorId());
            groupMemberCK.setGroup(group);
            groupMember.setCompositeKey(groupMemberCK);

            List<Integer> creator = new ArrayList<>();
            creator.add(group.getCreatorId());
            addNewMembersToGroup(groupId, creator);

            return true;

        } else {
            log.error("There already exists group with this name");
            return false;
        }
    }

    public boolean deleteGroup(int groupId, int userId) throws IllegalAccessException {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            if (group.getCreatorId() == userId) {
                Set<Request> requests = group.getRequests();
                requestService.deleteRequests(requests);
                groupMemberService.removeGroupMembers(group.getMembers());
                groupRepository.delete(group);
                return true;
            } else {
                log.error("Not authorized to perform this action");
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }
    }

    public void updateGroup(Group group) {
        groupRepository.save(group);
    }

    public boolean addNewMembersToGroup(int group_id, List<Integer> userIds) {
        Optional<Group> groupOptional = groupRepository.findById(group_id);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            Set<GroupMember> groupMembers = group.getMembers();

            for (int userId : userIds) {
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
            return false;
        }

        return true;
    }

    public boolean removeGroupMembersFromGroup(int groupId, int removerId, List<Integer> userIds) throws IllegalAccessException {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            if (group.getCreatorId() == removerId) {
                Set<GroupMember> groupMembers = group.getMembers();

                for (int userId : userIds) {
                    if (userId == group.getCreatorId()) {
                        log.warn("You cannot remove creator from their group");

                    } else {
                        GroupMember groupMember = groupMemberService.getGroupMember(groupId, userId);
                        groupMembers.remove(groupMember);
                    }
                }
                group.setMembers(groupMembers);
                groupRepository.save(group);

                groupMemberService.removeGroupMembers(groupMembers);

                return true;
            } else {
                log.error("Not authorized to perform this action.");
                throw (new IllegalAccessException("Not authorized to perform this action"));
            }

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }
    }

    private boolean renameGroup(int groupId, String newName) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            if (noNameConflict(newName)) {
                group.setName(newName);
                groupRepository.save(group);
                return true;
            } else {
                log.error("Name already taken by another group");
                return false;
            }
        } else {
            log.error("Group not found");
            return false;
        }
    }

    private boolean changeGroupDescription(int groupId, String newDescription) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {

            Group group = groupOptional.get();
            group.setDescription(newDescription);
            groupRepository.save(group);
            return true;

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }

    }

    public List<Group> getUserGroups(int userId) {
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

        for (Group group : groups) {
            group.setActive(activate);
        }
        groupRepository.saveAll(groups);
    }

    private void activateOrDeactivateGroup(int groupId, boolean activate) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            group.setActive(activate);
            groupRepository.save(group);

        } else {
            log.error("Group not found");
            throw (new EntityNotFoundException("Group not found"));
        }
    }

    public void patchService(int userId, int groupId, GroupDTO groupDTO) throws IllegalAccessException {

        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
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

    private boolean isCreatorOrMember(int userId, Group group) {

        if (group.getCreatorId() == userId)
            return true;

        for (GroupMember groupMember : group.getMembers())
            if (groupMember.getCompositeKey().getUserId() == userId)
                return true;
        return false;
    }
}
