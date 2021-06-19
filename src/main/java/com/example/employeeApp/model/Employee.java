package com.example.employeeApp.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

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
    @Column(unique=true)
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

    public void updateInfo(String login, String name, double salary) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Double.compare(employee.salary, salary) == 0 &&
                Objects.equals(id, employee.id) &&
                Objects.equals(login, employee.login) &&
                Objects.equals(name, employee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, name, salary);
    }
}

