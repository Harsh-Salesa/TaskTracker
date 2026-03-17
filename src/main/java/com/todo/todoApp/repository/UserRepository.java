package com.todo.todoApp.repository;

import com.todo.todoApp.entity.Team;
import com.todo.todoApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findByTeamManager(User manager);

    List<User> findByRole(String role);
    List<User> findByTeam(Team team);
}