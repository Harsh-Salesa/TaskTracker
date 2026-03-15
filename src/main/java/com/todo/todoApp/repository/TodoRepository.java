package com.todo.todoApp.repository;

import com.todo.todoApp.entity.Team;
import com.todo.todoApp.entity.Todo;
import com.todo.todoApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByStatus(String status);
    List<Todo> findByCreatedBy(User user);
    List<Todo> findByAssignedTo(User user);
    List<Todo> findByIdAndStatus(Long id, String status);
    List<Todo> findByTeam(Team team);
    List<Todo> findByTeamManager(User manager);

}