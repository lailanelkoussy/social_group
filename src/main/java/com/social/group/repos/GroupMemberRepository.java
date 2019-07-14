package com.social.group.repos;

import com.social.group.entities.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> { //todo this should be removed

    List<GroupMember> findAllByCompositeKey_UserId (int compositeKey_UserId);

    GroupMember findByCompositeKey_GroupIdAndCompositeKey_UserId(int compositeKey_groupId, int compositeKey_userId);


}
