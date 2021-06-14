package com.example.employeeApp.controller;

import com.example.employeeApp.model.Employee;
import com.example.employeeApp.model.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
public class EmployeeController {
    @Autowired
    private EmployeeRepository empRepository;


    @GetMapping("/users")
    List<Employee> getAllEmployees() {
        return empRepository.findAll();
    }

    @GetMapping("/user/{id}")
    Optional<Employee> getEmployeeById(String id) {
        return empRepository.findById(id);
    }
}



