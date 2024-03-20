package edu.unam.springsecurity.auth.repository;

import edu.unam.springsecurity.auth.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    List<UserInfo> findAllByOrderByUseIdAsc();
    UserInfo findByUseEmail(String email);
    boolean existsByUseEmail(String email);
}
