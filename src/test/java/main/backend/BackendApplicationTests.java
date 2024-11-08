package main.backend;



import com.example.demo.controller.CppCompilerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CppCompilerController.class)
public class BackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCompileCppCode_success() throws Exception {
        String cppCode = "#include <iostream>\nint main() { std::cout << \"Hello, World!\"; return 0; }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/compile")
                        .content(cppCode)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Output:\nHello, World!"));
    }

    @Test
    public void testCompileCppCode_compilationError() throws Exception {
        String cppCode = "#include <iostream>\nint main() { std::cout << \"Hello, World!\" return 0; }"; // Missing semicolon

        mockMvc.perform(MockMvcRequestBuilders.post("/api/compile")
                        .content(cppCode)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Compilation Error:")));
    }

    @Test
    public void testCompileCppCode_runtimeError() throws Exception {
        String cppCode = "#include <iostream>\nint main() { int x = 1 / 0; return 0; }"; // Division by zero

        mockMvc.perform(MockMvcRequestBuilders.post("/api/compile")
                        .content(cppCode)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Runtime Error:")));
    }
}

