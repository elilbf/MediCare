package br.com.grupo.ClassInsight.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SwaggerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnSwaggerUiPage() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    void shouldReturnOpenApiJson() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        
        assert content.contains("\"openapi\"");
        assert content.contains("\"info\"");
        assert content.contains("\"ClassInsight API\"");
        assert content.contains("\"paths\"");
        assert content.contains("\"components\"");
    }

    @Test
    void shouldContainFeedbackEndpointsInOpenApi() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        
        assert content.contains("/api/feedbacks");
        assert content.contains("criarFeedback");
        assert content.contains("obterFeedbackPorId");
        assert content.contains("listarFeedbackPorTurma");
        assert content.contains("listarFeedbackPorAluno");
    }

    @Test
    void shouldContainUsuarioEndpointsInOpenApi() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        
        assert content.contains("/api/usuarios");
        assert content.contains("criarUsuario");
        assert content.contains("obterUsuarioPorId");
        assert content.contains("listarTodos");
    }

    @Test
    void shouldContainSchemasInOpenApi() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        
        assert content.contains("\"FeedbackDTO\"");
        assert content.contains("\"FeedbackCriacaoDTO\"");
        assert content.contains("\"UsuarioDTO\"");
        assert content.contains("\"UsuarioCriacaoDTO\"");
    }

    @Test
    void shouldContainTagsInOpenApi() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        
        assert content.contains("\"Feedbacks\"");
        assert content.contains("\"Usuários\"");
        assert content.contains("API para gerenciamento de feedbacks educacionais");
        assert content.contains("API para gerenciamento de usuários do sistema");
    }

    @Test
    void shouldReturnSwaggerConfigRedirect() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isFound());
    }
}
