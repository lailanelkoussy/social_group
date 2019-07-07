package com.social.group.services;

import com.social.group.entities.Group;
import com.social.group.entities.GroupMember;
import com.social.group.repos.GroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class GroupMemberService {//todo this is wrong

    @Autowired
    GroupMemberRepository groupMemberRepository;

    public List<Group> getGroupsWithUser(int user_id) {

        List<GroupMember> groupMembers = groupMemberRepository.findAllByCompositeKey_UserId(user_id);
        List<Group> groups = new ArrayList<>();

        for (GroupMember groupMember: groupMembers){
            groups.add(groupMember.getCompositeKey().getGroup());
        }

        return groups;
    }

    public List<GroupMember> getGroupMembers(int groupId) {

        return groupMemberRepository.findAllByCompositeKey_GroupId(groupId);

    }

    public GroupMember getGroupMember (int groupId, int userId){
        return groupMemberRepository.findByCompositeKey_GroupIdAndCompositeKey_UserId(groupId, userId);
    }

    public void addGroupMembers(Set<GroupMember> groupMembers){
        groupMemberRepository.saveAll(groupMembers);
    }

    public void removeGroupMembers(Set<GroupMember> groupMembers){
        groupMemberRepository.deleteAll(groupMembers);
    }


}
