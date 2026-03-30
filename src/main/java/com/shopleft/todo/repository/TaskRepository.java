package com.shopleft.todo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	Page<Task> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

	// Using native query
	@Query(value = "SELECT * FROM tasks WHERE task LIKE CONCAT('%',:description,'%') AND user_id = :userId",nativeQuery = true)
	Page<Task> findByTaskContains(@Param("userId") Long userId, @Param("description") String description, Pageable pageable);

	@Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.createdAt >= :minDate AND t.createdAt <= :maxDate ORDER BY t.createdAt DESC")
	Page<Task> findByTaskBetween(@Param("userId") Long userId, @Param("minDate") LocalDate minDate, @Param("maxDate") LocalDate maxDate, Pageable pageable);
	
	@Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.createdAt <= :maxDate ORDER BY t.createdAt DESC")
	Page<Task> findByTaskBefore(@Param("userId") Long userId, @Param("maxDate") LocalDate maxDate, Pageable pageable);
	
	@Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.createdAt >= :minDate ORDER BY t.createdAt DESC")
	Page<Task> findByTaskAfter(@Param("userId") Long userId, @Param("minDate") LocalDate minDate, Pageable pageable);
}
