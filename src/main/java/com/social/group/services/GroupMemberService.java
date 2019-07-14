package com.social.group.services;

import com.social.group.entities.Group;
import com.social.group.entities.GroupMember;
import com.social.group.repos.GroupMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class GroupMemberService { //todo didn't we agree that there should be no service for the middle table ?

    @Autowired
    GroupMemberRepository groupMemberRepository;

    public List<Group> getGroupsWithUser(int user_id) {

        log.info("Retrieving groups...");
        List<GroupMember> groupMembers = groupMemberRepository.findAllByCompositeKey_UserId(user_id);
        List<Group> groups = new ArrayList<>();

        for (GroupMember groupMember : groupMembers) {
            groups.add(groupMember.getCompositeKey().getGroup());
        }

        return groups;
    }


    public GroupMember getGroupMember(int groupId, int userId) {
        log.info("Retrieving group member...");
        return groupMemberRepository.findByCompositeKey_GroupIdAndCompositeKey_UserId(groupId, userId);
    }

    public void addGroupMembers(Set<GroupMember> groupMembers) {
        log.info("Adding group members");
        groupMemberRepository.saveAll(groupMembers);
    }

    public void removeGroupMembers(Set<GroupMember> groupMembers) {
        log.info("Deleting group members");
        groupMemberRepository.deleteAll(groupMembers);
    }


}
