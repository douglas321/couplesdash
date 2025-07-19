package com.csperry.couplesdash.controller;

import com.csperry.couplesdash.model.TodoItem;
import com.csperry.couplesdash.model.User;
import com.csperry.couplesdash.repository.TodoItemRepository;
import com.csperry.couplesdash.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

import static com.csperry.couplesdash.constants.APIConstants.TODO_URL;
import static com.csperry.couplesdash.constants.APIConstants.USER_URL;

@RestController
@RequestMapping(TODO_URL)
public class TodoController {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);


    @Autowired
    private TodoItemRepository todoRepo;

    @GetMapping("/{coupleId}")
    public List<TodoItem> getTodos(@PathVariable Long coupleId) {
        logger.info("/todo get hit");
        return todoRepo.findByCoupleId(coupleId);
    }

    @PostMapping
    public TodoItem createTodo(@RequestBody TodoItem todo, Principal principal) {
        logger.info("/todo create hit");

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        todo.setCoupleId(user.getCouple().getId());
        return todoRepo.save(todo);
    }

    @PutMapping("/{id}")
    public TodoItem updateTodo(@PathVariable Long id, @RequestBody TodoItem updatedTodo) {
        logger.info("/todo update endpoint hit");
        TodoItem todo = todoRepo.findById(id).orElseThrow();
        todo.setText(updatedTodo.getText());
        todo.setCompleted(updatedTodo.isCompleted());
        return todoRepo.save(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        logger.info("/todo endpoint hit");
        todoRepo.deleteById(id);
    }
}

