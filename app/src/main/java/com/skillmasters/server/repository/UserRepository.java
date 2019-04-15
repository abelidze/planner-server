package com.skillmasters.server.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.skillmasters.server.model.User;

public interface UserRepository extends JpaRepository<User, Long>
{
    User findByUsername(String username);
}