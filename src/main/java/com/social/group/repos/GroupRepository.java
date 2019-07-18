package com.social.group.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.social.group.entities.Group;

import javax.transaction.Transactional;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    long countAllByName(String name);

    List<Group> findAllByNameContainingIgnoreCase(String query);

    List<Group> findAllByCreatorId(int creatorId);

    @Transactional
    void delete(Group group);
}
