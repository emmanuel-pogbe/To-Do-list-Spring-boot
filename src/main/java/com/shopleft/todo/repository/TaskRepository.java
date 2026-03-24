package com.shopleft.todo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopleft.todo.model.Task;

import jakarta.transaction.Transactional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
	List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);

	// practicing with JPA 
	@Transactional
	@Modifying
	@Query("DELETE FROM Task t WHERE t.id = :id")
	void deleteTaskById(Long id);
}
