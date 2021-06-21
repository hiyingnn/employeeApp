package com.example.employeeApp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findAll();

    List<Employee> findAllByOrderByIdAsc();
    List<Employee> findAllByOrderByIdDesc();

    List<Employee> findAllByOrderByLoginAsc();
    List<Employee> findAllByOrderByLoginDesc();

    List<Employee> findAllByOrderByNameAsc();
    List<Employee> findAllByOrderByNameDesc();

    List<Employee> findAllByOrderBySalaryAsc();
    List<Employee> findAllByOrderBySalaryDesc();

    Optional<Employee> findById(String id);
    Optional<Employee> findByLogin(String id);

    Employee save(Employee e);
    <S extends Employee> List<S> saveAll(Iterable<S> iterable);

    void deleteById(String s);

    void deleteAll();
}
