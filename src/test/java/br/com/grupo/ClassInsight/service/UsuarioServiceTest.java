package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.model.Usuario;
import br.com.grupo.ClassInsight.model.TipoUsuario;
import br.com.grupo.ClassInsight.dto.UsuarioCriacaoDTO;
import br.com.grupo.ClassInsight.dto.UsuarioDTO;
import br.com.grupo.ClassInsight.repository.UsuarioRepository;
import br.com.grupo.ClassInsight.exception.ResourceNotFoundException;
import br.com.grupo.ClassInsight.exception.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioCriacaoDTO usuarioCriacaoDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@example.com");
        usuario.setNome("João Silva");
        usuario.setSenha("senha123");
        usuario.setTipoUsuario(TipoUsuario.ALUNO);
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());

        usuarioCriacaoDTO = new UsuarioCriacaoDTO(
            "teste@example.com",
            "João Silva",
            "senha123",
            TipoUsuario.ALUNO
        );
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void testCriarUsuarioComSucesso() {
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO resultado = usuarioService.criarUsuario(usuarioCriacaoDTO);

        assertNotNull(resultado);
        assertEquals("teste@example.com", resultado.email());
        assertEquals("João Silva", resultado.nome());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email duplicado")
    void testCriarUsuarioComEmailDuplicado() {
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(DuplicateResourceException.class, () -> {
            usuarioService.criarUsuario(usuarioCriacaoDTO);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve obter usuário por ID com sucesso")
    void testObterUsuarioPorIdComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioDTO resultado = usuarioService.obterUsuarioPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("teste@example.com", resultado.email());
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter usuário que não existe")
    void testObterUsuarioPorIdNaoExiste() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.obterUsuarioPorId(999L);
        });
    }

    @Test
    @DisplayName("Deve obter usuário por email com sucesso")
    void testObterUsuarioPorEmailComSucesso() {
        when(usuarioRepository.findByEmail("teste@example.com")).thenReturn(Optional.of(usuario));

        UsuarioDTO resultado = usuarioService.obterUsuarioPorEmail("teste@example.com");

        assertNotNull(resultado);
        assertEquals("teste@example.com", resultado.email());
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter usuário por email que não existe")
    void testObterUsuarioPorEmailNaoExiste() {
        when(usuarioRepository.findByEmail("naoexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.obterUsuarioPorEmail("naoexiste@example.com");
        });
    }

    @Test
    @DisplayName("Deve listar todos os usuários ativos")
    void testListarTodos() {
        List<Usuario> usuarios = List.of(usuario);
        when(usuarioRepository.findByAtivoTrue()).thenReturn(usuarios);

        List<UsuarioDTO> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve listar usuários por tipo")
    void testListarPorTipo() {
        List<Usuario> usuarios = List.of(usuario);
        when(usuarioRepository.findByTipoUsuario(TipoUsuario.ALUNO)).thenReturn(usuarios);

        List<UsuarioDTO> resultado = usuarioService.listarPorTipo(TipoUsuario.ALUNO);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testAtualizarUsuarioComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioCriacaoDTO dtoAtualizado = new UsuarioCriacaoDTO(
            "newemail@example.com",
            "João Silva Santos",
            "novaSenha123",
            TipoUsuario.PROFESSOR
        );

        UsuarioDTO resultado = usuarioService.atualizarUsuario(1L, dtoAtualizado);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void testDeletarUsuarioComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.deletarUsuario(1L);

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário que não existe")
    void testDeletarUsuarioNaoExiste() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.deletarUsuario(999L);
        });
    }
}
