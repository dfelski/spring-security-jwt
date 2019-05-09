package jwtexample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class JwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void testPublic() throws Exception {
        this.mockMvc
            .perform(get("/api/public"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hi, I'm public"));
    }

    @Test
    void testPrivate() throws Exception {
        this.mockMvc
            .perform(get("/api/private"))
            .andExpect(status().isForbidden());
    }

    @Test
    void testAuthentication() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(get("/api/login")
                    .param("username", "user")
                    .param("password", "password"))
            .andExpect(status().isOk())
            .andReturn();

        String authorizationToken = mvcResult.getResponse().getHeader(TokenConstants.TOKEN_HEADER);
        assertThat(authorizationToken).isNotEmpty().startsWith(TokenConstants.TOKEN_PREFIX);

        this.mockMvc
            .perform(get("/api/private")
                    .header(TokenConstants.TOKEN_HEADER, authorizationToken))
            .andExpect(status().isOk())
            .andExpect(content().string("Hi, I'm private"));

    }

}
