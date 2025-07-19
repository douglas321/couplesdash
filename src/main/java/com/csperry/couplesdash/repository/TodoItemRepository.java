package com.csperry.couplesdash.repository;

import com.csperry.couplesdash.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    List<TodoItem> findByCoupleId(Long coupleId);
}