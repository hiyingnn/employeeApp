package com.example.employeeApp.model;

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
    private String id;

    @NonNull
    private String login;

    @NonNull
    private String name;

    @NonNull
    private long salary;

    public Employee(String id, String login, String name, long salary) {
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

    public long getSalary() {
        return salary;
    }
}

