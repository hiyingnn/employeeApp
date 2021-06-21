package com.example.employeeApp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.example.employeeApp.controller.EmployeeController;
import com.example.employeeApp.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeRestControllerIntegrationTests {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private EmployeeController employeeController;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		this.mvc =  MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@BeforeEach
	public void clear() {
		this.employeeController.deleteAllEmployees();
		Assert.assertEquals(this.employeeController.getAllEmployees().size(), 0);
	}

	@Test
	public void givenWac_whenServletContext_thenItProvidesGreetController() {
		ServletContext servletContext = webApplicationContext.getServletContext();

		Assert.assertNotNull(servletContext);
		Assert.assertTrue(servletContext instanceof MockServletContext);
		Assert.assertNotNull(webApplicationContext.getBean("employeeController"));
	}

	/*************************** Test Get request ******************************/

	@Test
	public void givenEmployees_whenGetEmployees_thenReturnJsonArray()
			throws Exception {

		Employee alex = new Employee("a001", "alex1", "alex", 145.3);
		employeeController.addNewEmployee(asMap(alex));


		mvc.perform(get("/users")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name", is(alex.getName())))
				.andExpect(jsonPath("$[0].id", is(alex.getId())))
				.andExpect(jsonPath("$[0].login", is(alex.getLogin())))
				.andExpect(jsonPath("$[0].salary", is(alex.getSalary())));

		Assert.assertEquals(employeeController.getAllEmployees().size(), 1);
	}

	@Test
	public void givenEmployees_whenGetEmployeesQueryParams_thenReturnJsonArray()
			throws Exception {

		Employee alex = new Employee("a001", "alex5", "alex", 5607);
		Employee alexia = new Employee("a002", "alex1", "alexia", 703);
		Employee bob = new Employee("b001", "bob", "bob", 100);
		Employee charlie = new Employee("c004", "c5", "charlie", 57);
		Employee callie = new Employee("c005", "cz2", "callie", 2357);

		employeeController.addNewEmployee(asMap(bob));
		employeeController.addNewEmployee(asMap(charlie));
		employeeController.addNewEmployee(asMap(callie));
		employeeController.addNewEmployee(asMap(alex));
		employeeController.addNewEmployee(asMap(alexia));

		Assert.assertEquals(employeeController.getAllEmployees().size(), 5);

		/** test sort option: id and order: asc **/
		String sortIdAsc = String.format("/users/sortOption=%s&sortOrder=%s&filterValue=%s-%s&searchOption=%s&searchValue=%s", "id", "asc", "0", "6000", "id", "");

		mvc.perform(get(sortIdAsc)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[0].id", is(alex.getId())))
				.andExpect(jsonPath("$[1].id", is(alexia.getId())))
				.andExpect(jsonPath("$[2].id", is(bob.getId())))
				.andExpect(jsonPath("$[3].id", is(charlie.getId())))
				.andExpect(jsonPath("$[4].id", is(callie.getId())));

		/** test sort option: id and order: asc **/
		String sortIdDesc = String.format("/users/sortOption=%s&sortOrder=%s&filterValue=%s-%s&searchOption=%s&searchValue=%s", "id", "desc", "0", "6000", "id", "");

		mvc.perform(get(sortIdDesc)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[4].id", is(alex.getId())))
				.andExpect(jsonPath("$[3].id", is(alexia.getId())))
				.andExpect(jsonPath("$[2].id", is(bob.getId())))
				.andExpect(jsonPath("$[1].id", is(charlie.getId())))
				.andExpect(jsonPath("$[0].id", is(callie.getId())));


		/** test sort option: login and order: asc **/
		String sortLoginDesc = String.format("/users/sortOption=%s&sortOrder=%s&filterValue=%s-%s&searchOption=%s&searchValue=%s", "login", "asc", "0", "6000", "id", "");

		mvc.perform(get(sortLoginDesc)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[1].login", is(alex.getLogin())))
				.andExpect(jsonPath("$[0].login", is(alexia.getLogin())))
				.andExpect(jsonPath("$[2].login", is(bob.getLogin())))
				.andExpect(jsonPath("$[3].login", is(charlie.getLogin())))
				.andExpect(jsonPath("$[4].login", is(callie.getLogin())));

		/** test filter and sort option: name and order: desc **/
		String filterSortNameDesc = String.format("/users/sortOption=%s&sortOrder=%s&filterValue=%s-%s&searchOption=%s&searchValue=%s", "name", "desc", "0", "5000", "id", "");

		mvc.perform(get(filterSortNameDesc)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(4)))
				.andExpect(jsonPath("$[3].name", is(alexia.getName())))
				.andExpect(jsonPath("$[2].name", is(bob.getName())))
				.andExpect(jsonPath("$[0].name", is(charlie.getName())))
				.andExpect(jsonPath("$[1].name", is(callie.getName())));

		/** test search name and sort option: salary and order: asc **/
		String searchSortSalaryAsc = String.format("/users/sortOption=%s&sortOrder=%s&filterValue=%s-%s&searchOption=%s&searchValue=%s", "salary", "asc", "0", "6000", "name", "alex");

		mvc.perform(get(searchSortSalaryAsc)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].salary", is(alexia.getSalary())))
				.andExpect(jsonPath("$[1].salary", is(alex.getSalary())));
	}

	/*************************** Test Put request ******************************/
	@Test
	public void givenEmployees_whenAddNewEmployee_thenReturnResponse()
			throws Exception {

		Employee bob = new Employee("b001", "bbob1", "bob", 145.3);

		mvc.perform(post("/users")
			.content(asJsonString(bob))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void givenEmployees_whenAddEmployeeSameId_thenReturnFailResponse()
			throws Exception {

		Employee bob = new Employee("b001", "bbob1", "bob", 145.3);
		Employee ben = new Employee("b001", "bben1", "ben", 300.2);

		employeeController.addNewEmployee(asMap(bob));

		mvc.perform(post("/users")
				.content(asJsonString(ben))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assert.assertEquals(employeeController.getAllEmployees().size(), 1);

	}

	@Test
	public void givenEmployees_whenAddEmployeeLogin_thenReturnFailResponse()
			throws Exception {

		Employee alex = new Employee("a001", "abc", "alex", 145.3);
		Employee ben = new Employee("b001", "abc", "ben", 300.2);

		employeeController.addNewEmployee(asMap(alex));

		mvc.perform(post("/users")
				.content(asJsonString(ben))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assert.assertEquals(employeeController.getAllEmployees().size(), 1);
	}

	@Test
	public void givenEmployees_whenAddEmployeeNegativeSalary_thenReturnFailResponse()
			throws Exception {

		Employee bob = new Employee("b001", "bbob1", "bob", -1);

		mvc.perform(post("/users")
				.content(asJsonString(bob))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assert.assertEquals(employeeController.getAllEmployees().size(), 0);
	}

	/*************************** Test Patch request ******************************/

	@Test
	public void givenEmployees_whenUpdateEmployee_thenReturnSuccess()
			throws Exception {

		Employee bob = new Employee("b001", "bbob1", "bob", 145.3);

		employeeController.addNewEmployee(asMap(bob));
		Assert.assertEquals(employeeController.getAllEmployees().size(), 1);

		Employee bobUpdate = new Employee("b001", "bbob2", "bob2", 3211.11);

		mvc.perform(patch("/users")
				.content(asJsonString(bobUpdate))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		Assert.assertEquals((( Employee) employeeController.getAllEmployees().toArray()[0]), bobUpdate);
	}

	@Test
	public void givenEmployees_whenUpdateNonExistentEmployee_thenReturnFailResponse()
			throws Exception {

		Employee bob = new Employee("b001", "bbob1", "bob", 145.3);

		employeeController.addNewEmployee(asMap(bob));
		Assert.assertEquals(employeeController.getAllEmployees().size(), 1);

		Employee bobUpdate = new Employee("b002", "bbob2", "bob2", 3211.11);

		mvc.perform(patch("/users")
				.content(asJsonString(bobUpdate))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assert.assertEquals(employeeController.getAllEmployees().size(), 1);
	}

	@Test
	public void givenEmployees_whenUpdateLoginIdExisting_thenReturnFailResponse()
			throws Exception {

		Employee bob = new Employee("b001", "bbob1", "bob", 145.3);
		Employee charlie = new Employee("c001", "ccoc1", "charlie", 571.31);

		employeeController.addNewEmployee(asMap(bob));
		employeeController.addNewEmployee(asMap(charlie));

		Assert.assertEquals(employeeController.getAllEmployees().size(), 2);

		Employee charlieUpdate = new Employee("c001", "bbob1", "charlie", 571.31);

		mvc.perform(patch("/users")
				.content(asJsonString(charlieUpdate))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assert.assertEquals((( Employee) employeeController.getAllEmployees().toArray()[1]), charlie);
	}

	@Test
	public void givenEmployees_whenUpdateEmployeeNegativeSalary_thenReturnFailResponse()
			throws Exception {

		Employee bob = new Employee("b001", "bbob1", "bob", 1831.31);
		Employee bobUpdate = new Employee("b001", "bbob1", "bob", -1831.31);

		employeeController.addNewEmployee(asMap(bob));
		Assert.assertEquals(employeeController.getAllEmployees().size(), 1);

		mvc.perform(patch("/users")
				.content(asJsonString(bobUpdate))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assert.assertEquals((( Employee) employeeController.getAllEmployees().toArray()[0]).getSalary(), 1831.31, 2);
	}

	/*************************** Test Delete request ******************************/

	@Test
	public void givenEmployees_whenDeleteEmployee_thenReturnSuccess()
			throws Exception {

		Employee bob = new Employee("b001", "bbob1", "bob", 13913);

		employeeController.addNewEmployee(asMap(bob));

		Assert.assertEquals(employeeController.getAllEmployees().size(), 1);
		mvc.perform(delete("/users/" + bob.getId()))
				.andExpect(status().isOk());
		Assert.assertEquals(employeeController.getAllEmployees().size(), 0);
	}

	/*************************** Helper functions ******************************/

	public static Map<String, String> asMap(Employee emp) {
		Map<String, String> map = new HashMap<>();
		map.put("id", emp.getId());
		map.put("login", emp.getLogin());
		map.put("name", emp.getName());
		map.put("salary", Double.toString(emp.getSalary()));
		return map;
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}