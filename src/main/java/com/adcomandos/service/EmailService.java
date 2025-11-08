package com.adcomandos.service;

import com.adcomandos.dto.OrcamentoResponseDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remetente = "seuemail@dominio.com"; // Deve ser o mesmo do application.properties

    // Formatador para garantir que os valores apareçam em formato brasileiro (R$ 1.000,00)
    private static final DecimalFormat DECIMAL_FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DECIMAL_FORMAT = new DecimalFormat("#,##0.00", symbols);
    }

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia o orçamento calculado para o cliente.
     * O e-mail usa um formato simples (SimpleMailMessage) que é suficiente para o texto.
     */
    public void enviarOrcamento(String destinatario, OrcamentoResponseDTO response) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(destinatario);
            message.setSubject("Orçamento Solicitado - AdComandos");
            message.setText(buildEmailBody(response));

            mailSender.send(message);

        } catch (Exception e) {
            // Em produção, você registraria o erro (log) aqui.
            System.err.println("Falha ao enviar e-mail para " + destinatario + ": " + e.getMessage());
            // Nota: Não relançamos a exceção para não quebrar o fluxo de cálculo do orçamento,
            // mas o log é importante para monitoramento.
        }
    }

    /**
     * Constrói o corpo do e-mail em formato de texto.
     */
    private String buildEmailBody(OrcamentoResponseDTO response) {
        String valorTotalFormatado = formatCurrency(response.getValorTotal());

        return String.format(
                "Olá %s,\n\n" +
                        "Seu orçamento solicitado foi calculado com sucesso. Abaixo estão os detalhes:\n\n" +
                        "--------------------------------------------------\n" +
                        "DETALHES DO ORÇAMENTO\n" +
                        "--------------------------------------------------\n" +
                        "Complexidade Aplicada: %s\n\n" +
                        "Valores Parciais:\n" +
                        "  - Materiais: R$ %s\n" +
                        "  - Mão de Obra: R$ %s\n" +
                        "  - Deslocamento: R$ %s\n" +
                        "  - Ajudante: R$ %s\n\n" +
                        "VALOR TOTAL ESTIMADO: R$ %s\n" +
                        "--------------------------------------------------\n\n" +
                        "Qualquer dúvida, entre em contato.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe AdComandos",
                response.getClienteNome(),
                response.getComplexidadeAplicada(),
                formatCurrency(response.getValorMaterial()),
                formatCurrency(response.getValorMaoObra()),
                formatCurrency(response.getValorDeslocamento()),
                formatCurrency(response.getValorAjudante()),
                valorTotalFormatado
        );
    }

    /**
     * Função auxiliar para formatar BigDecimal em string de moeda.
     */
    private String formatCurrency(java.math.BigDecimal value) {
        if (value == null) return "0,00";
        return DECIMAL_FORMAT.format(value);
    }
}