package br.com.grupo.ClassInsight;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Classe base para testes de integração da aplicação ClassInsight.
 * Fornece configuração comum para todos os testes de integração.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // Configurações comuns para testes de integração
}
