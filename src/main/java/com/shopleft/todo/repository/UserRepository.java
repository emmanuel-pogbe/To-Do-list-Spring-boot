package com.shopleft.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopleft.todo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
