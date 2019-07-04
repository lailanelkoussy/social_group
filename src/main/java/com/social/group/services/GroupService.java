package com.social.group.services;

import com.social.group.entities.Group;
import com.social.group.entities.GroupMember;
import com.social.group.entities.GroupMemberCK;
import com.social.group.repos.GroupMemberRepository;
import com.social.group.repos.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupMemberService groupMemberService;

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroup(int groupId) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {

            return groupOptional.get();

        } else {
            log.error("Group not found");
            return null;
        }

    }

    public List<Group> searchForGroups(String query) {
        return groupRepository.findAllByNameContainingIgnoreCase(query);
    }

    public boolean addNewGroup(Group group) {
        if (noNameConflict(group.getName())) {
            groupRepository.save(group);

            return true;

        } else {
            log.error("There already exists group with this name");
            return false;
        }
    }

    public boolean deleteGroup(int groupId, int userId) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            if (group.getCreatorId() == userId) {
                groupRepository.delete(group);
                return true;
            } else {
                log.error("Not authorized to perform this action");
                return false;
            }


        } else {
            log.error("Group not found");
            return false;
        }

    }

    public void updateGroup(Group group) {
        groupRepository.save(group);
    }

    public boolean addNewMembersToGroup(int group_id, List<Integer> userIds) {
        Optional<Group> groupOptional = groupRepository.findById(group_id);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            List<GroupMember> groupMembers = group.getMembers();

            for(int userId : userIds){
                GroupMemberCK compositeKey = new GroupMemberCK();
                compositeKey.setGroup(group);
                compositeKey.setUserId(userId);
                GroupMember groupMember = new GroupMember();
                groupMember.setCompositeKey(compositeKey);

                groupMembers.add(groupMember);
            }

            group.setMembers(groupMembers);
            groupRepository.save(group);


        } else {
            log.error("Group not found");
            return false;
        }

        return true;
    }

    public boolean removeGroupMembersFromGroup(int groupId, int removerId, List<Integer> userIds) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            if (group.getCreatorId() == removerId) {
                List<GroupMember> groupMembers = group.getMembers();


                for (int userId : userIds) {
                    GroupMember groupMember = groupMemberService.getGroupMember(groupId, userId);
                    groupMembers.remove(groupMember);
                }
                group.setMembers(groupMembers);
                groupRepository.save(group);

                return true;
            } else {
                log.error("Not authorized to perform this action.");
                return false;
            }

        } else {
            log.error("Group not found");
            return false;
        }
    }

    public boolean renameGroup(int groupId, String newName) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            if (noNameConflict(group.getName())) {
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

    public boolean changeGroupDescription(int groupId, String newDescription) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {

            Group group = groupOptional.get();
            group.setDescription(newDescription);
            groupRepository.save(group);
            return true;

        } else {
            log.error("Group not found");
            return false;
        }

    }

    public List<Group> getUserGroups(int userId) {
        return groupMemberService.getGroupsWithUser(userId);
    }

    public void activateOrDeactivateGroupsOfUser(int user_id, boolean activate) {
        List<Group> groups = getUserGroups(user_id);

        for (Group group : groups) {
            group.setActive(activate);
        }
        groupRepository.saveAll(groups);
    }

    private boolean noNameConflict(String groupName) {
        return (groupRepository.countAllByName(groupName) == 0);
    }


}
