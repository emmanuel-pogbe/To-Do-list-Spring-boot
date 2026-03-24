package com.shopleft.todo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopleft.todo.model.Task;

import jakarta.transaction.Transactional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
	List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);

	// using JPQL
	@Transactional
	@Modifying
	@Query("DELETE FROM Task t WHERE t.id = :id")
	void deleteTaskById(@Param("id") Long id);


	// Using native query
	@Query(value = "SELECT * FROM tasks WHERE task LIKE CONCAT('%',:description,'%') AND user_id = :userId",nativeQuery = true)
	List<Task> findByTaskContains(@Param("userId") Long userId, @Param("description") String description);
}
