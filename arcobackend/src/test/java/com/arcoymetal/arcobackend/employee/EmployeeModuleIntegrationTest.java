package com.arcoymetal.arcobackend.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.arcoymetal.arcobackend.TestcontainersConfiguration;
import com.arcoymetal.arcobackend.employee.api.ActiveEmployeeCompensationProvider;
import com.arcoymetal.arcobackend.employee.api.EmployeeCompensationSnapshot;
import com.arcoymetal.arcobackend.employee.api.EmployeeExistenceVerifier;
import com.arcoymetal.arcobackend.employee.api.EmploymentPeriodTerminatedEvent;
import com.arcoymetal.arcobackend.employee.internal.application.port.out.EmploymentPeriodRepository;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.ContractType;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentPeriod;
import com.arcoymetal.arcobackend.employee.internal.domain.employment.EmploymentStatus;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@RecordApplicationEvents
@WithMockUser(roles = "MANAGER")
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
    private ActiveEmployeeCompensationProvider activeEmployeeCompensationProvider;

    @Autowired
    private EmploymentPeriodRepository employmentPeriodRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
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

    @Test
    void shouldManageFullEmploymentLifecycleHistoryChangesTerminationAndRehire() throws Exception {
        String createEmployeePayload = """
                {
                  "firstName": "Elena",
                  "lastName": "Vásquez",
                  "email": "elena.vasquez@arcoymetal.com",
                  "phoneNumber": "78889999",
                  "gender": "FEMALE",
                  "birthDate": "1991-07-14",
                  "addressLine": "Colonia Médica #22",
                  "districtId": "%s",
                  "dui": "08888888-2",
                  "pensionFundInstitution": "AFP_CRECER"
                }
                """.formatted(VALID_DISTRICT_ID);

        UUID employeeId = extractUuid(
                mockMvc.perform(post("/api/v1/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createEmployeePayload))
                        .andExpect(status().isCreated())
                        .andReturn(),
                "employeeId"
        );

        // 1. Create initial ACTIVE employment period
        String firstPeriodPayload = """
                {
                  "employeeId": "%s",
                  "startDate": "2025-01-10",
                  "jobTitle": "Auxiliar de Taller",
                  "contractType": "FULL_TIME",
                  "baseSalary": 500.00
                }
                """.formatted(employeeId);

        UUID firstPeriodId = extractUuid(
                mockMvc.perform(post("/api/v1/employment-periods")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(firstPeriodPayload))
                        .andExpect(status().isCreated())
                        .andReturn(),
                "employmentPeriodId"
        );

        // 2. Register salary/job change (201 Created)
        String validChangePayload = """
                {
                  "jobTitle": "Supervisora de Taller",
                  "contractType": "FULL_TIME",
                  "baseSalary": 780.50,
                  "effectiveFrom": "2025-06-01"
                }
                """;

        mockMvc.perform(post("/api/v1/employment-periods/{id}/changes", firstPeriodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validChangePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employmentChangeId").isNotEmpty());

        // 3. Duplicate effectiveFrom -> 409 Conflict
        mockMvc.perform(post("/api/v1/employment-periods/{id}/changes", firstPeriodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validChangePayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        // 4. effectiveFrom before startDate -> 400 Bad Request
        String beforeStartPayload = """
                {
                  "jobTitle": "Inválido",
                  "contractType": "FULL_TIME",
                  "baseSalary": 520.00,
                  "effectiveFrom": "2024-12-31"
                }
                """;
        mockMvc.perform(post("/api/v1/employment-periods/{id}/changes", firstPeriodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beforeStartPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        // 5. GET employment history returns 1 period with 2 changes ordered effectiveFrom DESC
        mockMvc.perform(get("/api/v1/employees/{id}/employment-periods", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].changes.length()").value(2))
                .andExpect(jsonPath("$[0].changes[0].jobTitle").value("Supervisora de Taller"))
                .andExpect(jsonPath("$[0].changes[0].baseSalary").value(780.5))
                .andExpect(jsonPath("$[0].changes[1].jobTitle").value("Auxiliar de Taller"));

        // 6. Terminate period (200 OK)
        String terminatePayload = """
                {
                  "endDate": "2025-12-31",
                  "terminationReason": "RESIGNATION"
                }
                """;
        mockMvc.perform(post("/api/v1/employment-periods/{id}/terminate", firstPeriodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(terminatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employmentPeriodId").value(firstPeriodId.toString()));

        // 7. Registering a change or terminating again on TERMINATED period -> 409 Conflict
        String postTermChange = """
                {
                  "jobTitle": "No permitido",
                  "contractType": "FULL_TIME",
                  "baseSalary": 800.00,
                  "effectiveFrom": "2026-01-01"
                }
                """;
        mockMvc.perform(post("/api/v1/employment-periods/{id}/changes", firstPeriodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postTermChange))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/v1/employment-periods/{id}/terminate", firstPeriodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(terminatePayload))
                .andExpect(status().isConflict());

        // 8. Rehire with overlapping startDate (<= 2025-12-31) -> 409 Conflict
        String overlappingRehirePayload = """
                {
                  "employeeId": "%s",
                  "startDate": "2025-12-31",
                  "jobTitle": "Jefa de Producción",
                  "contractType": "FULL_TIME",
                  "baseSalary": 950.00
                }
                """.formatted(employeeId);
        mockMvc.perform(post("/api/v1/employment-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(overlappingRehirePayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        // 9. Rehire with valid startDate (> 2025-12-31) -> 201 Created
        String validRehirePayload = """
                {
                  "employeeId": "%s",
                  "startDate": "2026-02-01",
                  "jobTitle": "Jefa de Producción",
                  "contractType": "FULL_TIME",
                  "baseSalary": 950.00
                }
                """.formatted(employeeId);
        mockMvc.perform(post("/api/v1/employment-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRehirePayload))
                .andExpect(status().isCreated());

        // 10. History now has 2 periods ordered most recent first (ACTIVE first, TERMINATED second)
        mockMvc.perform(get("/api/v1/employees/{id}/employment-periods", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].startDate").value("2026-02-01"))
                .andExpect(jsonPath("$[1].status").value("TERMINATED"))
                .andExpect(jsonPath("$[1].terminationReason").value("RESIGNATION"));

        // 11. History for unknown employee -> 404 Not Found
        mockMvc.perform(get("/api/v1/employees/{id}/employment-periods", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateNitIsssNupUniquenessFilterServerSideAndExposePayrollApiAndEvents() throws Exception {
        // 1. Create employee A (will have ACTIVE period with salary raise)
        String empA = """
                {
                  "firstName": "Mario",
                  "lastName": "Castro",
                  "email": "mario.castro@arcoymetal.com",
                  "phoneNumber": "71110001",
                  "gender": "MALE",
                  "birthDate": "1990-02-15",
                  "addressLine": "Colonia Centro #1",
                  "districtId": "%s",
                  "dui": "01110001-1",
                  "nit": "0614-150290-101-1",
                  "isss": "11110001-1",
                  "nup": "22220001-1",
                  "pensionFundInstitution": "AFP_CONFIA"
                }
                """.formatted(VALID_DISTRICT_ID);

        UUID empAId = extractUuid(
                mockMvc.perform(post("/api/v1/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(empA))
                        .andExpect(status().isCreated())
                        .andReturn(),
                "employeeId"
        );

        // Duplicate NIT / ISSS / NUP -> 409 Conflict
        String duplicateNit = empA
                .replace("mario.castro@arcoymetal.com", "dup.nit@arcoymetal.com")
                .replace("01110001-1", "01110002-2")
                .replace("11110001-1", "11110002-2")
                .replace("22220001-1", "22220002-2");
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateNit))
                .andExpect(status().isConflict());

        String duplicateIsss = empA
                .replace("mario.castro@arcoymetal.com", "dup.isss@arcoymetal.com")
                .replace("01110001-1", "01110003-3")
                .replace("0614-150290-101-1", "0614-150290-101-3")
                .replace("22220001-1", "22220003-3");
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateIsss))
                .andExpect(status().isConflict());

        String duplicateNup = empA
                .replace("mario.castro@arcoymetal.com", "dup.nup@arcoymetal.com")
                .replace("01110001-1", "01110004-4")
                .replace("0614-150290-101-1", "0614-150290-101-4")
                .replace("11110001-1", "11110004-4");
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateNup))
                .andExpect(status().isConflict());

        // 2. Create employee B (will have TERMINATED period) and employee C (WITHOUT_PERIOD)
        String empB = """
                {
                  "firstName": "Lucía",
                  "lastName": "Pineda",
                  "email": "lucia.pineda@arcoymetal.com",
                  "phoneNumber": "71110002",
                  "gender": "FEMALE",
                  "birthDate": "1992-08-10",
                  "addressLine": "Colonia Centro #2",
                  "districtId": "%s",
                  "dui": "01110009-9",
                  "pensionFundInstitution": "AFP_CRECER"
                }
                """.formatted(VALID_DISTRICT_ID);
        UUID empBId = extractUuid(
                mockMvc.perform(post("/api/v1/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(empB))
                        .andExpect(status().isCreated())
                        .andReturn(),
                "employeeId"
        );

        String empC = """
                {
                  "firstName": "Diego",
                  "lastName": "Morales",
                  "email": "diego.morales@arcoymetal.com",
                  "phoneNumber": "71110003",
                  "gender": "MALE",
                  "birthDate": "1996-01-20",
                  "addressLine": "Colonia Centro #3",
                  "districtId": "%s",
                  "dui": "01110010-0",
                  "pensionFundInstitution": "ISP"
                }
                """.formatted(VALID_DISTRICT_ID);
        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(empC))
                .andExpect(status().isCreated());

        // Update employee B with employee A's NIT -> 409 Conflict
        String updateBWithANit = """
                {
                  "firstName": "Lucía",
                  "lastName": "Pineda",
                  "email": "lucia.pineda@arcoymetal.com",
                  "phoneNumber": "71110002",
                  "gender": "FEMALE",
                  "birthDate": "1992-08-10",
                  "addressLine": "Colonia Centro #2",
                  "districtId": "%s",
                  "dui": "011100099",
                  "nit": "0614-150290-101-1",
                  "pensionFundInstitution": "AFP_CRECER"
                }
                """.formatted(VALID_DISTRICT_ID);
        mockMvc.perform(put("/api/v1/employees/{id}", empBId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBWithANit))
                .andExpect(status().isConflict());

        // 3. Period + salary raise for A; Period + termination for B
        UUID periodAId = extractUuid(
                mockMvc.perform(post("/api/v1/employment-periods")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "employeeId": "%s",
                                          "startDate": "2026-01-01",
                                          "jobTitle": "Soldador I",
                                          "contractType": "FULL_TIME",
                                          "baseSalary": 600.00
                                        }
                                        """.formatted(empAId)))
                        .andExpect(status().isCreated())
                        .andReturn(),
                "employmentPeriodId"
        );

        mockMvc.perform(post("/api/v1/employment-periods/{id}/changes", periodAId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jobTitle": "Soldador II",
                                  "contractType": "FULL_TIME",
                                  "baseSalary": 750.00,
                                  "effectiveFrom": "2026-06-01"
                                }
                                """))
                .andExpect(status().isCreated());

        UUID periodBId = extractUuid(
                mockMvc.perform(post("/api/v1/employment-periods")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "employeeId": "%s",
                                          "startDate": "2025-01-01",
                                          "jobTitle": "Contadora",
                                          "contractType": "FULL_TIME",
                                          "baseSalary": 900.00
                                        }
                                        """.formatted(empBId)))
                        .andExpect(status().isCreated())
                        .andReturn(),
                "employmentPeriodId"
        );

        mockMvc.perform(post("/api/v1/employment-periods/{id}/terminate", periodBId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "endDate": "2026-03-31",
                                  "terminationReason": "RESIGNATION"
                                }
                                """))
                .andExpect(status().isOk());

        // Verify EmploymentPeriodTerminatedEvent was published
        List<EmploymentPeriodTerminatedEvent> events = applicationEvents
                .stream(EmploymentPeriodTerminatedEvent.class)
                .toList();
        assertThat(events).anyMatch(ev ->
                ev.employeeId().equals(empBId)
                        && ev.employmentPeriodId().equals(periodBId)
                        && ev.endDate().equals(LocalDate.of(2026, 3, 31))
        );

        // 4. Server-side search and employmentStatus filters
        mockMvc.perform(get("/api/v1/employees").param("employmentStatus", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].employeeId").value(empAId.toString()));

        mockMvc.perform(get("/api/v1/employees").param("employmentStatus", "TERMINATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].employeeId").value(empBId.toString()));

        mockMvc.perform(get("/api/v1/employees").param("employmentStatus", "WITHOUT_PERIOD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Diego"));

        mockMvc.perform(get("/api/v1/employees").param("search", "01110001-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Mario"));

        // 5. Public payroll API resolves effective salary at referenceDate
        Optional<EmployeeCompensationSnapshot> beforeRaise =
                activeEmployeeCompensationProvider.findCompensationAt(empAId, LocalDate.of(2026, 5, 15));
        assertThat(beforeRaise).isPresent();
        assertThat(beforeRaise.get().jobTitle()).isEqualTo("Soldador I");
        assertThat(beforeRaise.get().baseSalary()).isEqualByComparingTo("600.00");

        List<EmployeeCompensationSnapshot> afterRaise =
                activeEmployeeCompensationProvider.findActiveCompensationsAt(LocalDate.of(2026, 6, 15));
        assertThat(afterRaise).hasSize(1);
        assertThat(afterRaise.getFirst().employeeId()).isEqualTo(empAId);
        assertThat(afterRaise.getFirst().jobTitle()).isEqualTo("Soldador II");
        assertThat(afterRaise.getFirst().baseSalary()).isEqualByComparingTo("750.00");
        assertThat(afterRaise.getFirst().pensionFundInstitution()).isEqualTo("AFP_CONFIA");
    }

    @Test
    void shouldPersistEmergencyContactExposeEmploymentSummaryAndRemoveLatestNonInitialChange() throws Exception {
        // 1. Create employee with emergency contact
        String createPayload = """
                {
                  "firstName": "Rosa",
                  "lastName": "Alvarado",
                  "email": "rosa.alvarado@arcoymetal.com",
                  "phoneNumber": "72223333",
                  "gender": "FEMALE",
                  "birthDate": "1991-09-10",
                  "addressLine": "Colonia Escalón #42",
                  "districtId": "%s",
                  "dui": "04445555-6",
                  "pensionFundInstitution": "AFP_CRECER",
                  "emergencyContactName": "José Alvarado",
                  "emergencyContactPhone": "79998888"
                }
                """.formatted(VALID_DISTRICT_ID);

        UUID employeeId = extractUuid(
                mockMvc.perform(post("/api/v1/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createPayload))
                        .andExpect(status().isCreated())
                        .andReturn(),
                "employeeId"
        );

        mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emergencyContactName").value("José Alvarado"))
                .andExpect(jsonPath("$.emergencyContactPhone").value("79998888"))
                .andExpect(jsonPath("$.employmentStatus").value("WITHOUT_PERIOD"))
                .andExpect(jsonPath("$.currentJobTitle").doesNotExist());

        // 2. Open employment period -> ACTIVE with currentJobTitle
        UUID periodId = extractUuid(
                mockMvc.perform(post("/api/v1/employment-periods")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "employeeId": "%s",
                                          "startDate": "2026-01-10",
                                          "jobTitle": "Operadora CNC",
                                          "contractType": "FULL_TIME",
                                          "baseSalary": 650.00
                                        }
                                        """.formatted(employeeId)))
                        .andExpect(status().isCreated())
                        .andReturn(),
                "employmentPeriodId"
        );

        // 3. Register a second change (mistaken entry)
        UUID latestChangeId = extractUuid(
                mockMvc.perform(post("/api/v1/employment-periods/{id}/changes", periodId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "jobTitle": "Supervisora CNC",
                                          "contractType": "FULL_TIME",
                                          "baseSalary": 999.00,
                                          "effectiveFrom": "2026-06-01"
                                        }
                                        """))
                        .andExpect(status().isCreated())
                        .andReturn(),
                "employmentChangeId"
        );

        mockMvc.perform(get("/api/v1/employees").param("search", "04445555-6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].employmentStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].currentJobTitle").value("Supervisora CNC"));

        // Extract initial change ID from history
        MvcResult historyResult = mockMvc.perform(get("/api/v1/employees/{id}/employment-periods", employeeId))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode historyJson = objectMapper.readTree(historyResult.getResponse().getContentAsString());
        UUID initialChangeId = UUID.fromString(historyJson.get(0).get("changes").get(1).get("employmentChangeId").asString());

        // 4. Attempting to delete initial change -> 409 Conflict
        mockMvc.perform(delete("/api/v1/employment-periods/{periodId}/changes/{changeId}", periodId, initialChangeId))
                .andExpect(status().isConflict());

        // 5. Attempting to delete unknown change -> 404 Not Found
        mockMvc.perform(delete("/api/v1/employment-periods/{periodId}/changes/{changeId}", periodId, UUID.randomUUID()))
                .andExpect(status().isNotFound());

        // 6. Deleting latest non-initial change -> 204 No Content
        mockMvc.perform(delete("/api/v1/employment-periods/{periodId}/changes/{changeId}", periodId, latestChangeId))
                .andExpect(status().isNoContent());

        // 7. Verify history now has only the initial change and currentJobTitle reverted
        mockMvc.perform(get("/api/v1/employees/{id}/employment-periods", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].changes.length()").value(1))
                .andExpect(jsonPath("$[0].changes[0].jobTitle").value("Operadora CNC"));

        mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentJobTitle").value("Operadora CNC"));
    }

    private UUID extractUuid(MvcResult result, String field) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(json.get(field).asString());
    }
}
