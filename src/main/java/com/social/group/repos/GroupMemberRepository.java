package com.social.group.repos;

import com.social.group.entities.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {

    List<GroupMember> findAllByCompositeKey_UserId (int compositeKey_UserId);

    Optional<GroupMember> findByCompositeKey_GroupIdAndCompositeKey_UserId(int compositeKey_groupId, int compositeKey_userId);

    @Transactional
    void deleteAllByCompositeKey_UserId(int userId);

}
