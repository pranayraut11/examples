package com.security.jwtexample.repository;

import com.security.jwtexample.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserInfo, String> {
    Optional<UserInfo> findByUsername(String username);

}
