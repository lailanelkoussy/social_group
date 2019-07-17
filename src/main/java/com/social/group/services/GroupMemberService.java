package com.social.group.services;

import com.social.group.entities.Group;
import com.social.group.entities.GroupMember;
import com.social.group.repos.GroupMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class GroupMemberService {
    @Autowired
    GroupMemberRepository groupMemberRepository;

    List<Group> getGroupsWithUser(int user_id) {

        log.info("Retrieving groups...");
        List<GroupMember> groupMembers = groupMemberRepository.findAllByCompositeKey_UserId(user_id);
        List<Group> groups = new ArrayList<>();

        for (GroupMember groupMember : groupMembers) {
            groups.add(groupMember.getCompositeKey().getGroup());
        }

        return groups;
    }

    GroupMember getGroupMember(int groupId, int userId) {
        log.info("Retrieving group member...");

        Optional<GroupMember> groupMemberOptional = groupMemberRepository.findByCompositeKey_GroupIdAndCompositeKey_UserId(groupId, userId);
        if(!groupMemberOptional.isPresent())
            throw (new EntityNotFoundException("User is not member of group"));
        return groupMemberOptional.get();
    }

    void addGroupMembers(Set<GroupMember> groupMembers) {
        log.info("Adding group members");
        groupMemberRepository.saveAll(groupMembers);
    }

    void removeGroupMembers(Set<GroupMember> groupMembers) {
        log.info("Deleting group members");
        groupMemberRepository.deleteAll(groupMembers);
    }


}
