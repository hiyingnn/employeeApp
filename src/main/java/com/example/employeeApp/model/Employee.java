package com.example.employeeApp.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "Employee")
public class Employee {
    @Id
    @CsvBindByName
    private String id;

    @NonNull
    @CsvBindByName
    private String login;

    @NonNull
    @CsvBindByName
    private String name;

    @NonNull
    @CsvBindByName
    private double salary;

    public Employee(String id, String login, String name, double salary) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }
}

