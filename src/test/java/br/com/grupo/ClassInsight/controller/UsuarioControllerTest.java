package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.model.TipoUsuario;
import br.com.grupo.ClassInsight.dto.UsuarioCriacaoDTO;
import br.com.grupo.ClassInsight.dto.UsuarioDTO;
import br.com.grupo.ClassInsight.service.UsuarioService;
import br.com.grupo.ClassInsight.exception.ResourceNotFoundException;
import br.com.grupo.ClassInsight.exception.DuplicateResourceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@DisplayName("Testes do UsuarioController")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioCriacaoDTO usuarioCriacaoDTO;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioCriacaoDTO = new UsuarioCriacaoDTO(
            "teste@example.com",
            "João Silva",
            "senha123",
            TipoUsuario.ALUNO
        );

        usuarioDTO = new UsuarioDTO(
            1L,
            "teste@example.com",
            "João Silva",
            TipoUsuario.ALUNO,
            LocalDateTime.now(),
            true
        );
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void testCriarUsuarioComSucesso() throws Exception {
        when(usuarioService.criarUsuario(any(UsuarioCriacaoDTO.class))).thenReturn(usuarioDTO);

        mockMvc.perform(post("/classinsight/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioCriacaoDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value("teste@example.com"));

        verify(usuarioService, times(1)).criarUsuario(any(UsuarioCriacaoDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar usuário com email duplicado")
    void testCriarUsuarioComEmailDuplicado() throws Exception {
        when(usuarioService.criarUsuario(any(UsuarioCriacaoDTO.class)))
            .thenThrow(new DuplicateResourceException("Email já existe"));

        mockMvc.perform(post("/classinsight/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioCriacaoDTO)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve obter usuário por ID com sucesso")
    void testObterUsuarioPorIdComSucesso() throws Exception {
        when(usuarioService.obterUsuarioPorId(1L)).thenReturn(usuarioDTO);

        mockMvc.perform(get("/classinsight/usuarios/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value("teste@example.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao obter usuário inexistente")
    void testObterUsuarioPorIdNaoEncontrado() throws Exception {
        when(usuarioService.obterUsuarioPorId(999L))
            .thenThrow(new ResourceNotFoundException("Usuário não encontrado"));

        mockMvc.perform(get("/classinsight/usuarios/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve obter usuário por email com sucesso")
    void testObterUsuarioPorEmailComSucesso() throws Exception {
        when(usuarioService.obterUsuarioPorEmail("teste@example.com")).thenReturn(usuarioDTO);

        mockMvc.perform(get("/classinsight/usuarios/email/teste@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("teste@example.com"));
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void testListarTodosUsuarios() throws Exception {
        List<UsuarioDTO> usuarios = List.of(usuarioDTO);
        when(usuarioService.listarTodos()).thenReturn(usuarios);

        mockMvc.perform(get("/classinsight/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].email").value("teste@example.com"));
    }

    @Test
    @DisplayName("Deve listar usuários por tipo")
    void testListarUsuariosPorTipo() throws Exception {
        List<UsuarioDTO> usuarios = List.of(usuarioDTO);
        when(usuarioService.listarPorTipo(TipoUsuario.ALUNO)).thenReturn(usuarios);

        mockMvc.perform(get("/classinsight/usuarios/tipo/ALUNO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testAtualizarUsuarioComSucesso() throws Exception {
        when(usuarioService.atualizarUsuario(eq(1L), any(UsuarioCriacaoDTO.class)))
            .thenReturn(usuarioDTO);

        mockMvc.perform(put("/classinsight/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioCriacaoDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void testDeletarUsuarioComSucesso() throws Exception {
        doNothing().when(usuarioService).deletarUsuario(1L);

        mockMvc.perform(delete("/classinsight/usuarios/1"))
            .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).deletarUsuario(1L);
    }
}
