package com.social.group.repos;

import com.social.group.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    long countAllByUserIdAndGroupId(int userId, int groupId);

    List<Request> findAllByGroupId(int groupId);

    @Transactional
    void deleteAllByUserId(int userId);
}
