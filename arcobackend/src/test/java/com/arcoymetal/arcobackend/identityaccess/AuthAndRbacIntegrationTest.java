package com.arcoymetal.arcobackend.identityaccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.arcoymetal.arcobackend.TestcontainersConfiguration;
import com.arcoymetal.arcobackend.identityaccess.internal.application.command.CreateUserCommand;
import com.arcoymetal.arcobackend.identityaccess.internal.application.port.in.CreateUserUseCase;
import com.arcoymetal.arcobackend.identityaccess.internal.domain.vo.Role;

import jakarta.servlet.http.Cookie;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AuthAndRbacIntegrationTest {

    private static final String VALID_DISTRICT_ID = "AH0101";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreateUserUseCase createUserUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        jdbcTemplate.execute("DELETE FROM user_roles");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM employment_change");
        jdbcTemplate.execute("DELETE FROM employment_period");
        jdbcTemplate.execute("DELETE FROM employee");
    }

    @Test
    void shouldAuthenticateWithJwtAndHttpOnlyCookieEnforceRbacAndPopulateAuditor() throws Exception {
        UUID adminEmpId = insertEmployeeDirectly("admin.emp@arcoymetal.com.sv", "061111111");
        UUID managerEmpId = insertEmployeeDirectly("manager.emp@arcoymetal.com.sv", "062222222");

        UUID adminUserId = createUserUseCase.createUser(new CreateUserCommand(
                "AdminPass123!", "admin@arcoymetal.com.sv", adminEmpId, Set.of(Role.ADMINISTRATOR)
        )).value();
        createUserUseCase.createUser(new CreateUserCommand(
                "ManagerPass123!", "manager@arcoymetal.com.sv", managerEmpId, Set.of(Role.MANAGER)
        ));

        // 1. Unauthenticated access to protected endpoints -> 401 Unauthorized
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());

        // 2. Login as MANAGER -> 200 OK + HttpOnly Refresh Cookie
        MvcResult managerLogin = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "manager@arcoymetal.com.sv",
                                  "password": "ManagerPass123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("Path=/api/v1/auth")))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("manager@arcoymetal.com.sv"))
                .andReturn();

        String managerToken = extractField(managerLogin, "accessToken");
        Cookie managerRefreshCookie = managerLogin.getResponse().getCookie("arco_refresh_token");
        assertThat(managerRefreshCookie).isNotNull();

        // 3. MANAGER can access /api/v1/auth/me and /api/v1/employees, but gets 403 on /api/v1/users/**
        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("manager@arcoymetal.com.sv"));

        mockMvc.perform(get("/api/v1/employees")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + managerToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + managerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        // 4. Login as ADMINISTRATOR -> can access /api/v1/users and JpaAuditingConfig populates created_by
        MvcResult adminLogin = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@arcoymetal.com.sv",
                                  "password": "AdminPass123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();
        String adminToken = extractField(adminLogin, "accessToken");

        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));

        MvcResult createdEmployee = mockMvc.perform(post("/api/v1/employees")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Audited",
                                  "lastName": "Employee",
                                  "email": "audited@arcoymetal.com.sv",
                                  "phoneNumber": "71234567",
                                  "gender": "MALE",
                                  "birthDate": "1990-01-15",
                                  "addressLine": "Calle Principal #10",
                                  "districtId": "%s",
                                  "dui": "06333333-3",
                                  "pensionFundInstitution": "AFP_CONFIA"
                                }
                                """.formatted(VALID_DISTRICT_ID)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID auditedEmployeeId = UUID.fromString(extractField(createdEmployee, "employeeId"));
        UUID createdBy = jdbcTemplate.queryForObject(
                "SELECT created_by FROM employee WHERE id = ?",
                UUID.class,
                auditedEmployeeId
        );
        assertThat(createdBy).isEqualTo(adminUserId);

        // 5. Refresh works with valid cookie
        MvcResult refreshed = mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(managerRefreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();
        Cookie refreshedCookie = refreshed.getResponse().getCookie("arco_refresh_token");
        assertThat(refreshedCookie).isNotNull();

        // 6. Logout WITHOUT Authorization header (using only refresh cookie) revokes token_version and clears cookie
        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(refreshedCookie))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("Max-Age=0")));

        // Reusing old refresh cookie after logout -> 401 Unauthorized
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(refreshedCookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLockUserAfterFiveFailedLoginAttemptsWithGenericErrorAndUnlockViaAdmin() throws Exception {
        UUID adminEmpId = insertEmployeeDirectly("admin.lock@arcoymetal.com.sv", "071111111");
        UUID targetEmpId = insertEmployeeDirectly("target.lock@arcoymetal.com.sv", "072222222");

        createUserUseCase.createUser(new CreateUserCommand(
                "AdminPass123!", "admin.lock@arcoymetal.com.sv", adminEmpId, Set.of(Role.ADMINISTRATOR)
        ));
        UUID targetUserId = createUserUseCase.createUser(new CreateUserCommand(
                "CorrectPass123!", "target.lock@arcoymetal.com.sv", targetEmpId, Set.of(Role.MANAGER)
        )).value();

        // Non-existent email returns same generic 401 message
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "nonexistent@arcoymetal.com.sv",
                                  "password": "WrongPassword1!"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas."));

        // 5 consecutive wrong passwords lock the account
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "email": "target.lock@arcoymetal.com.sv",
                                      "password": "WrongPassword1!"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Credenciales inválidas."));
        }

        String statusInDb = jdbcTemplate.queryForObject(
                "SELECT status FROM users WHERE id = ?",
                String.class,
                targetUserId
        );
        assertThat(statusInDb).isEqualTo("LOCKED");

        // Next attempt even with correct password is rejected with 403 Forbidden
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "target.lock@arcoymetal.com.sv",
                                  "password": "CorrectPass123!"
                                }
                                """))
                .andExpect(status().isForbidden());

        // Admin unlocks account -> failed_login_attempts resets and user can login again
        MvcResult adminLogin = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin.lock@arcoymetal.com.sv",
                                  "password": "AdminPass123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();
        String adminToken = extractField(adminLogin, "accessToken");

        mockMvc.perform(put("/api/v1/users/{id}", targetUserId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "target.lock@arcoymetal.com.sv",
                                  "roles": ["MANAGER"],
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "target.lock@arcoymetal.com.sv",
                                  "password": "CorrectPass123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    private UUID insertEmployeeDirectly(String email, String dui) {
        UUID employeeId = UUID.randomUUID();
        jdbcTemplate.update(
                """
                INSERT INTO employee (
                    id, first_name, last_name, email, phone_number, gender, birth_date,
                    address_line, district_id, dui, pension_fund_institution, created_at, updated_at
                ) VALUES (?, 'Test', 'User', ?, '71234567', 'MALE', DATE '1992-01-01',
                          'Calle 1', ?, ?, 'AFP_CONFIA', NOW(), NOW())
                """,
                employeeId, email, VALID_DISTRICT_ID, dui
        );
        return employeeId;
    }

    private String extractField(MvcResult result, String field) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get(field).asString();
    }
}
