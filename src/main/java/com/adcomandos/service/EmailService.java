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
            System.err.println("Falha ao enviar e-mail para " + destinatario + ": " + e.getMessage());
        }
    }

    /**
     * Constrói o corpo do e-mail em formato de texto, usando os nomes de campos atualizados do DTO.
     */
    private String buildEmailBody(OrcamentoResponseDTO response) {
        // NOTA: O OrcamentoResponseDTO não tem o campo "valorTotal" ou "valorMaterial"
        // Vamos somar os custos de serviço (Mão de Obra + Deslocamento + Ajudante) para obter o total de SERVIÇO.
        java.math.BigDecimal totalServico = response.getCustoTotalMaoDeObra()
                .add(response.getValorDeslocamento() != null ? response.getValorDeslocamento() : java.math.BigDecimal.ZERO)
                .add(response.getValorAjudante() != null ? response.getValorAjudante() : java.math.BigDecimal.ZERO);

        String valorTotalFormatado = formatCurrency(totalServico);

        // O valor do material (custo) só será listado para referência interna.
        String custoMaterialFormatado = formatCurrency(response.getCustoTotalMaterial());

        return String.format(
                "Olá %s,\n\n" +
                        "Seu orçamento solicitado foi calculado com sucesso. Abaixo estão os detalhes:\n\n" +
                        "--------------------------------------------------\n" +
                        "DETALHES DO ORÇAMENTO\n" +
                        "--------------------------------------------------\n" +
                        "Complexidade Aplicada: %s\n\n" +
                        "Valores Parciais do Serviço (Sem Material):\n" +
                        "  - Custo Ref. Materiais: R$ %s (Material NÃO incluso no valor total. Apenas para referência de custo)\n" +
                        "  - Mão de Obra: R$ %s\n" +
                        "  - Deslocamento: R$ %s\n" +
                        "  - Ajudante: R$ %s\n\n" +
                        "VALOR TOTAL DO SERVIÇO ESTIMADO: R$ %s\n" +
                        "--------------------------------------------------\n\n" +
                        "Qualquer dúvida, entre em contato.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe AdComandos",
                // Linha 81 no EmailService.java: CORRETA
                response.getClienteNome(),
                response.getNivelComplexidade(), // ⬅️ CORRIGIDO (era getComplexidadeAplicada)
                custoMaterialFormatado, // ⬅️ CORRIGIDO (usando getCustoTotalMaterial e novo nome de variável)
                formatCurrency(response.getCustoTotalMaoDeObra()), // ⬅️ CORRIGIDO (era getValorMaoObra)
                formatCurrency(response.getValorDeslocamento()), // ⬅️ ASSUMIDO que este e o próximo ainda existem
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