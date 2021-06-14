package com.example.employeeApp;

import com.example.employeeApp.model.Employee;
import com.example.employeeApp.model.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private EmployeeRepository empRepository;

    @Autowired
    public DataLoader(EmployeeRepository empRepository) {
        this.empRepository = empRepository;
    }

    public void run(ApplicationArguments args) {
        System.out.println("Initialising data");
        empRepository.save(new Employee("e0001","hpotter","Harry Potter",1234));
        empRepository.save(new Employee("e0002","rwesley","Ron Weasley",19234 ));
        empRepository.save(new Employee("e0003","ssnape","Severus Snape",4000));
    }
}