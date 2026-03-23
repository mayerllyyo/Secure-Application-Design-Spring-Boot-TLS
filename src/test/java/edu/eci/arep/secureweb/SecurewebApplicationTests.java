package edu.eci.arep.secureweb;

import edu.eci.arep.secureweb.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurewebApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    // Port helper

    @Test
    void getPort_returnsDefaultWhenEnvNotSet() {
        // PORT env variable is not set in the test environment.
        assertThat(SecurewebApplication.getPort()).isEqualTo(5000);
    }

    // UserService

    @Test
    void passwordIsStoredAsHash() {
        userService.findByUsername("admin").ifPresent(user -> {
            String hash = user.getHashedPassword();
            // BCrypt hashes start with "$2a$" or "$2b$"
            assertThat(hash).startsWith("$2");
            assertThat(hash).doesNotContain("admin123");
        });
    }

    @Test
    void passwordValidationSucceedsForCorrectPassword() {
        userService.findByUsername("admin").ifPresent(user ->
            assertThat(userService.validatePassword("admin123", user.getHashedPassword())).isTrue()
        );
    }

    @Test
    void passwordValidationFailsForWrongPassword() {
        userService.findByUsername("admin").ifPresent(user ->
            assertThat(userService.validatePassword("wrong", user.getHashedPassword())).isFalse()
        );
    }

    // LoginController 

    @Test
    void loginWithValidCredentialsReturns200() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.user").value("admin"));
    }

    @Test
    void loginWithInvalidCredentialsReturns401() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithMissingFieldsReturns400() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\"}"))
            .andExpect(status().isBadRequest());
    }

    // HelloController 

    @Test
    void helloRequiresAuthentication() throws Exception {
        mvc.perform(get("/hello"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void helloReturnsGreetingWhenAuthenticated() throws Exception {
        mvc.perform(get("/hello")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Greetings")));
    }

    // Static assets

    @Test
    void indexPageIsPubliclyAccessible() throws Exception {
        mvc.perform(get("/index.html"))
            .andExpect(status().isOk());
    }
}
