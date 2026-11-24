package com.arcoymetal.arcobackend.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.arcoymetal.arcobackend.TestcontainersConfiguration;
import com.arcoymetal.arcobackend.employee.api.EmployeeExistenceVerifier;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmploymentPeriodRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class EmployeeModuleIntegrationTest {

    private static final String VALID_DISTRICT_ID = "AH0101";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeExistenceVerifier employeeExistenceVerifier;

    @Autowired
    private EmploymentPeriodRepository employmentPeriodRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        jdbcTemplate.execute("DELETE FROM employment_change");
        jdbcTemplate.execute("DELETE FROM employment_period");
        jdbcTemplate.execute("DELETE FROM user_roles");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM employee");
    }

    @Test
    void shouldCreateGetPaginateAndUpdateEmployeeWithoutCreatingUser() throws Exception {
        String createPayload = """
                {
                  "firstName": "Carlos",
                  "lastName": "Mendoza",
                  "email": "carlos.mendoza@arcoymetal.com",
                  "phoneNumber": "71234567",
                  "gender": "MALE",
                  "birthDate": "1995-03-10",
                  "addressLine": "Col. Escalón #123",
                  "districtId": "%s",
                  "dui": "01234567-8",
                  "nit": "0614-100395-101-2",
                  "isss": "12345678-9",
                  "nup": "98765432-1",
                  "pensionFundInstitution": "AFP_CONFIA"
                }
                """.formatted(VALID_DISTRICT_ID);

        MvcResult createResult = mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").isNotEmpty())
                .andReturn();

        UUID employeeId = extractUuid(createResult, "employeeId");

        // Invariant: creating an Employee NEVER automatically creates a User
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertThat(userCount).isZero();

        // Public module contract verification
        assertThat(employeeExistenceVerifier.existsByEmployeeId(employeeId)).isTrue();
        assertThat(employeeExistenceVerifier.existsByEmployeeId(UUID.randomUUID())).isFalse();

        // GET by ID
        MvcResult getResult = mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(employeeId.toString()))
                .andExpect(jsonPath("$.firstName").value("Carlos"))
                .andExpect(jsonPath("$.dui").value("012345678"))
                .andExpect(jsonPath("$.nit").value("06141003951012"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andReturn();

        Instant originalCreatedAt = Instant.parse(
                objectMapper.readTree(getResult.getResponse().getContentAsString()).get("createdAt").asString()
        );

        // GET paginated
        mockMvc.perform(get("/api/v1/employees").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].employeeId").value(employeeId.toString()));

        // PUT update preserves created_at and updates fields
        String updatePayload = """
                {
                  "firstName": "Carlos Alberto",
                  "lastName": "Mendoza",
                  "email": "carlos.mendoza@arcoymetal.com",
                  "phoneNumber": "79998888",
                  "gender": "MALE",
                  "birthDate": "1995-03-10",
                  "addressLine": "Residencial Nueva #45",
                  "districtId": "%s",
                  "dui": "012345678",
                  "nit": "06141003951012",
                  "isss": "123456789",
                  "nup": "987654321",
                  "pensionFundInstitution": "AFP_CRECER"
                }
                """.formatted(VALID_DISTRICT_ID);

        mockMvc.perform(put("/api/v1/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(employeeId.toString()));

        MvcResult afterUpdateResult = mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Carlos Alberto"))
                .andExpect(jsonPath("$.phoneNumber").value("79998888"))
                .andExpect(jsonPath("$.pensionFundInstitution").value("AFP_CRECER"))
                .andReturn();

        Instant updatedCreatedAt = Instant.parse(
                objectMapper.readTree(afterUpdateResult.getResponse().getContentAsString()).get("createdAt").asString()
        );
        assertThat(updatedCreatedAt).isEqualTo(originalCreatedAt);
    }

    @Test
    void shouldReturnConflictOnDuplicateDuiWithOrWithoutHyphenAndBadRequestOnInvalidPayload() throws Exception {
        String firstPayload = """
                {
                  "firstName": "Ana",
                  "lastName": "López",
                  "email": "ana.lopez@arcoymetal.com",
                  "phoneNumber": "71112222",
                  "gender": "FEMALE",
                  "birthDate": "1994-05-20",
                  "addressLine": "Calle 1",
                  "districtId": "%s",
                  "dui": "023456789",
                  "pensionFundInstitution": "AFP_CONFIA"
                }
                """.formatted(VALID_DISTRICT_ID);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstPayload))
                .andExpect(status().isCreated());

        // Same DUI formatted with hyphen must be rejected with 409 Conflict
        String duplicateDuiPayload = """
                {
                  "firstName": "Otra",
                  "lastName": "Persona",
                  "email": "otra.persona@arcoymetal.com",
                  "phoneNumber": "73334444",
                  "gender": "FEMALE",
                  "birthDate": "1990-01-01",
                  "addressLine": "Calle 2",
                  "districtId": "%s",
                  "dui": "02345678-9",
                  "pensionFundInstitution": "ISP"
                }
                """.formatted(VALID_DISTRICT_ID);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateDuiPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        // Unknown ID returns 404 Not Found
        mockMvc.perform(get("/api/v1/employees/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        // Underage employee returns 400 Bad Request
        String underagePayload = """
                {
                  "firstName": "Joven",
                  "lastName": "Prueba",
                  "email": "joven@arcoymetal.com",
                  "phoneNumber": "74445555",
                  "gender": "MALE",
                  "birthDate": "2015-01-01",
                  "addressLine": "Calle 3",
                  "districtId": "%s",
                  "dui": "05555555-5",
                  "pensionFundInstitution": "AFP_CONFIA"
                }
                """.formatted(VALID_DISTRICT_ID);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(underagePayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldCreateEmploymentPeriodPersistHistoryAndRejectSecondActivePeriod() throws Exception {
        String createEmployeePayload = """
                {
                  "firstName": "Roberto",
                  "lastName": "Díaz",
                  "email": "roberto.diaz@arcoymetal.com",
                  "phoneNumber": "76667777",
                  "gender": "MALE",
                  "birthDate": "1988-11-05",
                  "addressLine": "Pasaje Central #8",
                  "districtId": "%s",
                  "dui": "07777777-1",
                  "pensionFundInstitution": "AFP_CONFIA"
                }
                """.formatted(VALID_DISTRICT_ID);

        MvcResult empResult = mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createEmployeePayload))
                .andExpect(status().isCreated())
                .andReturn();

        UUID employeeId = extractUuid(empResult, "employeeId");

        String createPeriodPayload = """
                {
                  "employeeId": "%s",
                  "startDate": "2026-01-15",
                  "jobTitle": "Soldador de Estructuras",
                  "contractType": "FULL_TIME",
                  "baseSalary": 650.50
                }
                """.formatted(employeeId);

        MvcResult periodResult = mockMvc.perform(post("/api/v1/employment-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPeriodPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employmentPeriodId").isNotEmpty())
                .andReturn();

        UUID employmentPeriodId = extractUuid(periodResult, "employmentPeriodId");

        // Reconstitute from PostgreSQL via EmploymentPeriodRepository.findById
        EmploymentPeriod loaded = employmentPeriodRepository.findById(employmentPeriodId);
        assertThat(loaded.getEmployeeId().value()).isEqualTo(employeeId);
        assertThat(loaded.getStatus()).isEqualTo(EmploymentStatus.ACTIVE);
        assertThat(loaded.getEmploymentChanges()).hasSize(1);
        assertThat(loaded.getEmploymentChanges().getFirst().getJobTitle().value())
                .isEqualTo("Soldador de Estructuras");
        assertThat(loaded.getEmploymentChanges().getFirst().getContractType())
                .isEqualTo(ContractType.FULL_TIME);
        assertThat(loaded.getEmploymentChanges().getFirst().getBaseSalary().value())
                .isEqualByComparingTo(new BigDecimal("650.50"));

        // Second ACTIVE period for the same employee must return 409 Conflict
        mockMvc.perform(post("/api/v1/employment-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPeriodPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        // Non-existent employee must return 404 Not Found
        String missingEmployeePeriod = """
                {
                  "employeeId": "%s",
                  "startDate": "2026-02-01",
                  "jobTitle": "Operario",
                  "contractType": "TEMPORARY",
                  "baseSalary": 400.00
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/employment-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingEmployeePeriod))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private UUID extractUuid(MvcResult result, String field) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(json.get(field).asString());
    }
}
