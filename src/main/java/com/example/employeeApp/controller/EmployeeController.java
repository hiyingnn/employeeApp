package com.example.employeeApp.controller;

import com.example.employeeApp.model.Employee;
import com.example.employeeApp.model.EmployeeRepository;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Collection<Employee> getAllEmployees() {
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
            method=RequestMethod.PATCH)
    @ResponseBody
    ResponseEntity<String> updateEmployeeInformation(@RequestBody Map<String, String> data) {
        if (getEmployeeById(data.get("id")).isEmpty()) {
            return new ResponseEntity<>("Cannot update non-existing employer id " + data.get("id"), HttpStatus.BAD_REQUEST);
        }

        Employee existingEmp = getEmployeeById(data.get("id")).get();
        Optional<Employee> otherLogin = empRepository.findByLogin(data.get("login"));
        if (otherLogin.isPresent()  && data.get("id") != otherLogin.get().getId()) {
            return new ResponseEntity<>("Cannot update to login id, already exist in database: " + data.get("login"), HttpStatus.BAD_REQUEST);
        }

        if (Double.parseDouble(data.get("salary")) < 0) {
            return new ResponseEntity<>("Cannot update salary to negative: ", HttpStatus.BAD_REQUEST);
        }
        existingEmp.updateInfo(data.get("login"), data.get("name"), Double.parseDouble(data.get("salary")));
        empRepository.save(existingEmp);
        return new ResponseEntity<>("Updated existing employee with id: "+ data.get("id"), HttpStatus.OK);
    }

    @RequestMapping(value = "/users",
            produces = "application/json",
            method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addNewEmployee(@RequestBody Map<String, String> data) {
        if (getEmployeeById(data.get("id")).isPresent()) {
            return new ResponseEntity<>("Cannot add new employee with id, already exist in database:" + data.get("id"), HttpStatus.BAD_REQUEST);
        }

        if (empRepository.findByLogin(data.get("login")).isPresent()) {
            return new ResponseEntity<>("Cannot add new employee with login, already exist in database:" + data.get("login"), HttpStatus.BAD_REQUEST);
        }

        if (Double.parseDouble(data.get("salary")) < 0) {
            return new ResponseEntity<>("Cannot update salary to negative: ", HttpStatus.BAD_REQUEST);
        }

        empRepository.save(new Employee(data.get("id"), data.get("login"), data.get("name"), Double.parseDouble(data.get("salary"))));
        return new ResponseEntity<>("Added new employee with id:"+ data.get("id"), HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        empRepository.deleteById(id);
        return new ResponseEntity<>("Deleted employee with id: " + id, HttpStatus.OK);
    }

    @PostMapping(value="/upload")
    public ResponseEntity<String> uploadCSVFile(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is  = file.getInputStream();
        List<String> validHeader = Arrays.asList("id" ,"login","name","salary");

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            if (!csvParser.getHeaderNames().equals(validHeader)) {
                return new ResponseEntity<>("Invalid headers", HttpStatus.BAD_REQUEST);
            }

            Map<String, Employee> empIdMap = new HashMap<>();
            Set<String> loginSet = new HashSet<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                try {
                    Employee emp = new Employee(
                            csvRecord.get("id"),
                            csvRecord.get("login"),
                            csvRecord.get("name"),
                            Double.parseDouble(csvRecord.get("salary"))
                    );

                    // check if salary is negative
                    if (emp.getSalary() < 0) {
                        return new ResponseEntity<>("Invalid salary, salary is negative", HttpStatus.BAD_REQUEST);
                    }


                    if (getEmployeeById(emp.getId()).isPresent()) {
                        // check if existing employee id in DB, update if necessary
                        Map<String, String> updateInfoMap = new HashMap<>();
                        updateInfoMap.put("id", emp.getId());
                        updateInfoMap.put("login", emp.getLogin());
                        updateInfoMap.put("salary", Double.toString(emp.getSalary()));
                        updateInfoMap.put("name", emp.getName());
                        ResponseEntity<String> res = updateEmployeeInformation(updateInfoMap);
                        if (!res.getStatusCode().is2xxSuccessful()) {
                            return new ResponseEntity<>("Invalid login id in CSV", HttpStatus.BAD_REQUEST);

                        }
                    } else {
                        if (empIdMap.containsKey(emp.getId())) {
                            // check if existing employee id in in current CSV file, update if necessary
                            Employee existingEmp = empIdMap.get(emp.getId());
                            existingEmp.updateInfo(emp.getLogin(), emp.getName(), emp.getSalary());
                            empIdMap.put(existingEmp.getId(), existingEmp);
                        } else {
                            // does not exist
                            empIdMap.put(emp.getId(), emp);
                        }
                        if (loginSet.contains(emp.getLogin())) {
                            return new ResponseEntity<>("Invalid login id in CSV", HttpStatus.BAD_REQUEST);
                        }
                        loginSet.add(emp.getLogin());
                    }
                } catch (IllegalArgumentException e) {
                    if (csvRecord.get(0).charAt(0) == '#') {
                        System.out.println("There exists a #, comment");
                    } else {
                        return new ResponseEntity<>("Invalid row in CSV", HttpStatus.BAD_REQUEST);
                    }
                }
            }
            empRepository.saveAll(empIdMap.values());

            return new ResponseEntity<>("CSV uploaded", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Invalid CSV file, unable to parse", HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteAllEmployees() {
        empRepository.deleteAll();
    }
}

