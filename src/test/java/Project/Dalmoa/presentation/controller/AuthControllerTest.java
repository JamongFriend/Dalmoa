package Project.Dalmoa.presentation.controller;

import Project.Dalmoa.application.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void login_성공_200() throws Exception {
    }

    @Test
    void login_이메일없음_400() throws Exception {
    }

    @Test
    void login_비밀번호없음_400() throws Exception {
    }

    @Test
    void reissue_성공_200() throws Exception {
    }

    @Test
    void reissue_토큰없음_400() throws Exception {
    }

    @Test
    void logout_성공_204() throws Exception {
    }

    @Test
    void logout_인증없음_401() throws Exception {
    }
}
