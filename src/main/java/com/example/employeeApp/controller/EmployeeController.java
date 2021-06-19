package com.example.employeeApp.controller;

import com.example.employeeApp.model.Employee;
import com.example.employeeApp.model.EmployeeRepository;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
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

    @RequestMapping(value = "/users",
            produces = "application/json",
            method=RequestMethod.PUT)
    @ResponseBody
    HttpStatus updateEmployeeInformation(@RequestBody Map<String, String> data) {
        //TODO: handle id updates...
        System.out.println("updating");
        System.out.println(data);
        empRepository.save(new Employee(data.get("id"), data.get("login"), data.get("name"), Double.parseDouble(data.get("salary"))));
        return HttpStatus.OK;
    }

    @DeleteMapping("/users/{id}")
    HttpStatus deleteEmployeeById(@PathVariable String id) {
        empRepository.deleteById(id);
        return HttpStatus.OK;
    }

    @PostMapping(value="/upload")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is  = file.getInputStream();
        List<String> validHeader = Arrays.asList("id" ,"login","name","salary");

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            if (!csvParser.getHeaderNames().equals(validHeader)) {
                return "invalid headers";
            }

            List<Employee> employees = new ArrayList<Employee>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                try {
                    System.out.println("adding new row");
                    Employee emp = new Employee(
                            csvRecord.get("id"),
                            csvRecord.get("login"),
                            csvRecord.get("name"),
                            Double.parseDouble(csvRecord.get("salary"))
                    );
                    employees.add(emp);
                } catch (IllegalArgumentException e) {
                    System.out.println(e);
                    if (csvRecord.get(0).charAt(0) == '#') {
                        System.out.println("THere exists a #, comment");
                    } else {
                        return "csv file error";
                    }
                }
            }
            empRepository.saveAll(employees);

            return "csv file success";
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}

