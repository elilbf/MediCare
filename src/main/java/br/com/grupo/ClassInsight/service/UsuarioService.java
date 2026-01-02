package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.model.Usuario;
import br.com.grupo.ClassInsight.model.TipoUsuario;
import br.com.grupo.ClassInsight.dto.UsuarioCriacaoDTO;
import br.com.grupo.ClassInsight.dto.UsuarioDTO;
import br.com.grupo.ClassInsight.repository.UsuarioRepository;
import br.com.grupo.ClassInsight.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    public UsuarioDTO criarUsuario(UsuarioCriacaoDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new EntityNotFoundException("Email já cadastrado: " + dto.email());
        }
        
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setNome(dto.nome());
        usuario.setSenha(dto.senha());
        usuario.setTipoUsuario(dto.tipoUsuario());
        usuario.setAtivo(true);
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return converterParaDTO(usuarioSalvo);
    }
    
    public UsuarioDTO obterUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
        return converterParaDTO(usuario);
    }
    
    public UsuarioDTO obterUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com email: " + email));
        return converterParaDTO(usuario);
    }
    
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findByAtivoTrue().stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    public List<UsuarioDTO> listarPorTipo(TipoUsuario tipoUsuario) {
        return usuarioRepository.findByTipoUsuario(tipoUsuario).stream()
            .filter(Usuario::isAtivo)
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    public UsuarioDTO atualizarUsuario(Long id, UsuarioCriacaoDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
        
        if (!usuario.getEmail().equals(dto.email()) && usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new EntityNotFoundException("Email já cadastrado: " + dto.email());
        }
        
        usuario.setEmail(dto.email());
        usuario.setNome(dto.nome());
        usuario.setSenha(dto.senha());
        usuario.setTipoUsuario(dto.tipoUsuario());
        
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return converterParaDTO(usuarioAtualizado);
    }
    
    public void deletarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
    
    public void deletarUsuarioPermanentemente(Long id) {
        usuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
        usuarioRepository.deleteById(id);
    }
    
    private UsuarioDTO converterParaDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getNome(),
            usuario.getTipoUsuario(),
            usuario.getDataCriacao(),
            usuario.isAtivo()
        );
    }
}
