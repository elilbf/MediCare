package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.dto.AvaliacaoRequestDTO;
import br.com.grupo.ClassInsight.dto.AvaliacaoResponseDTO;
import br.com.grupo.ClassInsight.exception.EntityNotFoundException;
import br.com.grupo.ClassInsight.model.Avaliacao;
import br.com.grupo.ClassInsight.model.Urgencia;
import br.com.grupo.ClassInsight.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final NotificationService notificationService;

    public AvaliacaoResponseDTO criarAvaliacao(AvaliacaoRequestDTO requestDTO) {
        log.info("Criando nova avaliação com nota: {}", requestDTO.getNota());

        Avaliacao avaliacao = Avaliacao.builder()
                .descricao(requestDTO.getDescricao())
                .nota(requestDTO.getNota())
                .build();

        avaliacao.classificarUrgencia();
        avaliacao = avaliacaoRepository.save(avaliacao);

        log.info("Avaliação criada com ID: {} e urgência: {}", avaliacao.getId(), avaliacao.getUrgencia());

        if (avaliacao.getUrgencia() == Urgencia.CRITICO) {
            log.warn("Avaliação CRÍTICA detectada! ID: {}", avaliacao.getId());
            notificationService.enviarNotificacaoCriticaAsync(avaliacao);
        }

        return AvaliacaoResponseDTO.fromEntity(avaliacao, "Avaliação criada com sucesso");
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponseDTO> listarAvaliacoes(Pageable pageable, Urgencia urgencia, 
                                                       LocalDateTime dataInicio, LocalDateTime dataFim) {
        log.info("Listando avaliações com filtros - urgência: {}, período: {} a {}", 
                urgencia, dataInicio, dataFim);

        Page<Avaliacao> avaliacoes;

        if (urgencia != null && dataInicio != null && dataFim != null) {
            avaliacoes = avaliacaoRepository.findByUrgenciaAndDataEnvioBetween(urgencia, dataInicio, dataFim, pageable);
        } else if (urgencia != null) {
            avaliacoes = avaliacaoRepository.findByUrgencia(urgencia, pageable);
        } else if (dataInicio != null && dataFim != null) {
            avaliacoes = avaliacaoRepository.findByDataEnvioBetween(dataInicio, dataFim, pageable);
        } else {
            avaliacoes = avaliacaoRepository.findAll(pageable);
        }

        return avaliacoes.map(avaliacao -> 
            AvaliacaoResponseDTO.fromEntity(avaliacao, null));
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponseDTO buscarAvaliacaoPorId(UUID id) {
        log.info("Buscando avaliação por ID: {}", id);
        
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada com ID: " + id));

        return AvaliacaoResponseDTO.fromEntity(avaliacao, null);
    }

    public void marcarNotificacaoEnviada(UUID id) {
        log.info("Marcando notificação como enviada para avaliação ID: {}", id);
        
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada com ID: " + id));

        avaliacao.setNotificacaoEnviada(true);
        avaliacaoRepository.save(avaliacao);
        
        log.info("Notificação marcada como enviada para avaliação ID: {}", id);
    }

    public void classificarUrgenciaManual(UUID id, Urgencia urgencia) {
        log.info("Classificando manualmente urgência para avaliação ID: {} como {}", id, urgencia);
        
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada com ID: " + id));

        avaliacao.setUrgencia(urgencia);
        avaliacaoRepository.save(avaliacao);
        
        log.info("Urgência atualizada para avaliação ID: {}", id);
    }
}
