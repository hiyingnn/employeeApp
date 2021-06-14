package com.example.employeeApp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findAll();
    Optional<Employee> findById(String id);
}
