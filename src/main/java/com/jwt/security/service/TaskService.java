package com.jwt.security.service;

import com.jwt.security.entity.Task;
import com.jwt.security.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {

    private TaskRepository taskRepository;

    public List<Task> getTasks(){
        return taskRepository.findAll() ;
    }
    public Optional<Task> getTaskById(Long id){
        return taskRepository.findById(id) ;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);

    }

    public boolean existsById(Long id) {
        return taskRepository.existsById(id);

    }
    public void deleteTask(Long id) {
         taskRepository.deleteById(id);
    }
}
