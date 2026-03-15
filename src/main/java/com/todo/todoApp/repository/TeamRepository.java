package com.todo.todoApp.repository;

import com.todo.todoApp.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
