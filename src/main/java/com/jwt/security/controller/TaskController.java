package com.jwt.security.controller;

import com.jwt.security.auth.AuthenticationService;
import com.jwt.security.config.JwtService;
import com.jwt.security.entity.Task;
import com.jwt.security.entity.User;
import com.jwt.security.service.TaskService;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;

import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("api/v1/task")
public class TaskController {


    private TaskService taskService;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;


    @GetMapping("/tasks")
    public List<Task> getTasks(){
        return taskService.getTasks();
    }

    @GetMapping("/task/{id}")
    public Task getTask(@PathVariable Long id){
        return taskService.getTaskById(id).
                orElseThrow(
                        ()-> new EntityNotFoundException("requested task not found")
                );
    }

    @PostMapping("/task")
    public ResponseEntity<Task> createTask(@RequestBody Task task, HttpServletRequest request) {
        // Extract the user ID from the JWT token
        String token = extractTokenFromRequest(request);
        String email = jwtService.extractUsername(token);

        User user = authenticationService.getUserByEmail(email);


        if (user != null) {

            task.setUser(user); // Set the user object for the task

            Task createdTask = taskService.createTask(task);


        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        }else {
            // Handle the case when the user is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @PutMapping ("/task/{id}")
    public ResponseEntity<?> createTask(@RequestBody Task task ,@PathVariable Long id) {
        if (taskService.existsById(id)) {
            Task task1 = taskService.getTaskById(id).
                    orElseThrow(
                            ()-> new EntityNotFoundException("requested task not found")
                    );
            task1.setTitle(task.getTitle());
            task1.setDescription(task.getDescription());
            task1.setDueDate(task.getDueDate());
            task1.setType(task.getType());
            taskService.createTask(task);

            return ResponseEntity.ok().body(task1);
        }
        else {
            HashMap<String,String> message = new HashMap<>();
            message.put("message" , id + " task not found matched") ;

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        if (taskService.existsById(id)) {
            taskService.deleteTask(id);

            HashMap<String,String> message = new HashMap<>();
            message.put("message" , id + " task is deleted successfully") ;

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        else {
            HashMap<String,String> message = new HashMap<>();
            message.put("message" , id + " task not found matched") ;

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
    }

}
