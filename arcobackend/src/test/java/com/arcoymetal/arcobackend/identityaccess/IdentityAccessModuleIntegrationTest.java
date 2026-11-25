package com.arcoymetal.arcobackend.identityaccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class IdentityAccessModuleIntegrationTest {

    private static final String VALID_DISTRICT_ID = "AH0101";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        jdbcTemplate.execute("DELETE FROM user_roles");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM employment_change");
        jdbcTemplate.execute("DELETE FROM employment_period");
        jdbcTemplate.execute("DELETE FROM employee");
    }

    @Test
    void shouldCreateGetAndPaginateUserLinkedToExistingEmployeeWithHashedPassword() throws Exception {
        UUID employeeId = createEmployee("admin.emp@arcoymetal.com.sv", "01234567-8");

        String createUserPayload = """
                {
                  "password": "Password123!",
                  "email": "admin.user@arcoymetal.com.sv",
                  "employeeId": "%s",
                  "roles": ["ADMINISTRATOR", "MANAGER"]
                }
                """.formatted(employeeId);

        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andReturn();

        UUID userId = extractUuid(createResult, "userId");

        // Verify password is stored hashed with BCrypt (not plaintext) and audit columns exist
        String storedHash = jdbcTemplate.queryForObject(
                "SELECT password_hash FROM users WHERE id = ?",
                String.class,
                userId
        );
        assertThat(storedHash).isNotEqualTo("Password123!").startsWith("$2");

        // GET by ID
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.employeeId").value(employeeId.toString()))
                .andExpect(jsonPath("$.email").value("admin.user@arcoymetal.com.sv"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.roles.length()").value(2))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        // GET paginated
        mockMvc.perform(get("/api/v1/users").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(userId.toString()));
    }

    @Test
    void shouldEnforceOneToOneEmployeeInvariantUniqueEmailAndSemanticErrors() throws Exception {
        // 1. Non-existent employeeId -> 404 Not Found
        String missingEmployeePayload = """
                {
                  "password": "Password123!",
                  "email": "ghost@arcoymetal.com.sv",
                  "employeeId": "%s",
                  "roles": ["MANAGER"]
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingEmployeePayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        // 2. Create two employees and one user for the first employee
        UUID firstEmployeeId = createEmployee("emp1@arcoymetal.com.sv", "02222222-1");
        UUID secondEmployeeId = createEmployee("emp2@arcoymetal.com.sv", "03333333-2");

        String firstUserPayload = """
                {
                  "password": "Password123!",
                  "email": "user1@arcoymetal.com.sv",
                  "employeeId": "%s",
                  "roles": ["MANAGER"]
                }
                """.formatted(firstEmployeeId);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstUserPayload))
                .andExpect(status().isCreated());

        // 3. Second user for the same employeeId -> 409 Conflict
        String duplicateEmployeePayload = """
                {
                  "password": "Password123!",
                  "email": "user1.second@arcoymetal.com.sv",
                  "employeeId": "%s",
                  "roles": ["ADMINISTRATOR"]
                }
                """.formatted(firstEmployeeId);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateEmployeePayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        // 4. Duplicate email for a different employee -> 409 Conflict
        String duplicateEmailPayload = """
                {
                  "password": "Password123!",
                  "email": "user1@arcoymetal.com.sv",
                  "employeeId": "%s",
                  "roles": ["MANAGER"]
                }
                """.formatted(secondEmployeeId);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateEmailPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        // 5. Empty roles -> 400 Bad Request
        String emptyRolesPayload = """
                {
                  "password": "Password123!",
                  "email": "user2@arcoymetal.com.sv",
                  "employeeId": "%s",
                  "roles": []
                }
                """.formatted(secondEmployeeId);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyRolesPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        // 6. Unknown userId on GET -> 404 Not Found
        mockMvc.perform(get("/api/v1/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private UUID createEmployee(String email, String dui) throws Exception {
        String payload = """
                {
                  "firstName": "Empleado",
                  "lastName": "Prueba",
                  "email": "%s",
                  "phoneNumber": "71234567",
                  "gender": "MALE",
                  "birthDate": "1993-04-12",
                  "addressLine": "Calle Principal #1",
                  "districtId": "%s",
                  "dui": "%s",
                  "pensionFundInstitution": "AFP_CONFIA"
                }
                """.formatted(email, VALID_DISTRICT_ID, dui);

        MvcResult result = mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        return extractUuid(result, "employeeId");
    }

    private UUID extractUuid(MvcResult result, String field) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(json.get(field).asString());
    }
}
