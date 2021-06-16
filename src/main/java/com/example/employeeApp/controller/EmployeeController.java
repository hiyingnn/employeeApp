package com.example.employeeApp.controller;

import com.example.employeeApp.model.Employee;
import com.example.employeeApp.model.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class EmployeeController {
    @Autowired
    private EmployeeRepository empRepository;


    @GetMapping("/users")
    Collection<Employee> getAllEmployees() {
       return empRepository.findAll();
    }

    @GetMapping("/users/sortOption={option}&sortOrder={order}&filterValue={lower}-{upper}&searchOption={soption}&searchValue={sval}")
    Collection<Employee> getAllEmployees(@PathVariable String option, @PathVariable String order, @PathVariable long lower, @PathVariable long upper, @PathVariable String soption, @PathVariable String sval) {
        List<Employee> empList;
        if (option.equals("id")) {
            if (order.equals("asc")) {
                empList = empRepository.findAllByOrderByIdAsc();
            } else {
                empList = empRepository.findAllByOrderByIdDesc();
            }
        } else if (option.equals("login")) {
            if (order.equals("asc")) {
                empList = empRepository.findAllByOrderByLoginAsc();
            } else {
                empList = empRepository.findAllByOrderByLoginDesc();
            }
        } else if (option.equals("name")) {
            if (order.equals("asc")) {
                empList = empRepository.findAllByOrderByNameAsc();
            } else {
                empList = empRepository.findAllByOrderByNameDesc();
            }
        } else {
            if (order.equals("asc")) {
                empList = empRepository.findAllByOrderBySalaryAsc();
            } else {
                empList = empRepository.findAllByOrderBySalaryDesc();
            }
        }

        System.out.println(sval);
        System.out.println(sval.equals(""));
        System.out.println(sval.equals("\r"));
        System.out.println(sval.equals("\n"));
        System.out.println(sval.length() == 0);

        return sval.length() == 0
                ? empList.stream().filter(e -> e.getSalary() >= lower && e.getSalary() <= upper).collect(Collectors.toList())
                : empList.stream().filter( e ->
                    (e.getSalary() >= lower && e.getSalary() <= upper
                            && (soption.equals("id") && e.getId().toLowerCase().contains(sval.toLowerCase())
                            || (soption.equals("login") && e.getLogin().toLowerCase().contains(sval.toLowerCase()))
                            || (soption.equals("name") && e.getName().toLowerCase().contains(sval.toLowerCase()))
                            || (soption.equals("salary") && e.getSalary() == Long.parseLong(sval))
                    ))
                ).collect(Collectors.toList());
    }

    @GetMapping("/user/{id}")
    Optional<Employee> getEmployeeById(String id) {
        return empRepository.findById(id);
    }

    @PutMapping("/user/{id}")
    Optional<Employee> updateEmployeeInformation(@PathVariable String id) {
        return empRepository.findById(id);
    }
}

