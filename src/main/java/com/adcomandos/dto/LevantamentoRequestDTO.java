package com.adcomandos.dto;

import com.adcomandos.model.TipoServico;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class LevantamentoRequestDTO implements Serializable {

    @NotNull(message = "O tipo de serviço é obrigatório.")
    private TipoServico tipoServico;

    @Min(value = 10, message = "A metragem quadrada mínima é 10m².")
    private double metragemQuadrada;

    @Min(value = 0, message = "A quantidade de pontos de luz não pode ser negativa.")
    private int quantidadePontosLuz;

    @Min(value = 0, message = "A quantidade de tomadas TUG não pode ser negativa.")
    private int quantidadeTomadas;

    @Min(value = 0, message = "A quantidade de chuveiros não pode ser negativa.")
    private int quantidadeChuveiros;

    @Min(value = 0, message = "A quantidade de ar condicionado não pode ser negativa.")
    private int quantidadeArCondicionado;

    // --- CAMPOS ADICIONADOS PARA SINCRONIZAÇÃO E PERSISTÊNCIA ---

    @Size(min = 3, max = 100, message = "O nome do cliente deve ter entre 3 e 100 caracteres.")
    private String clienteNome;

    @Size(min = 5, max = 255, message = "O endereço é obrigatório.")
    private String endereco;

    @Size(min = 8, max = 20, message = "O telefone é obrigatório.")
    private String clienteTelefone;

    // Campo para persistência
    private String nivelComplexidade;

    // Campo para cálculo da fiação de entrada
    @Min(value = 1, message = "A distância do poste ao quadro deve ser maior que zero.")
    private double distanciaPosteQuadro;

    // --- GETTERS E SETTERS ---

    public TipoServico getTipoServico() { return tipoServico; }
    public void setTipoServico(TipoServico tipoServico) { this.tipoServico = tipoServico; }

    public double getMetragemQuadrada() { return metragemQuadrada; }
    public void setMetragemQuadrada(double metragemQuadrada) { this.metragemQuadrada = metragemQuadrada; }

    public int getQuantidadePontosLuz() { return quantidadePontosLuz; }
    public void setQuantidadePontosLuz(int quantidadePontosLuz) { this.quantidadePontosLuz = quantidadePontosLuz; }

    public int getQuantidadeTomadas() { return quantidadeTomadas; }
    public void setQuantidadeTomadas(int quantidadeTomadas) { this.quantidadeTomadas = quantidadeTomadas; }

    public int getQuantidadeChuveiros() { return quantidadeChuveiros; }
    public void setQuantidadeChuveiros(int quantidadeChuveiros) { this.quantidadeChuveiros = quantidadeChuveiros; }

    public int getQuantidadeArCondicionado() { return quantidadeArCondicionado; }
    public void setQuantidadeArCondicionado(int quantidadeArCondicionado) { this.quantidadeArCondicionado = quantidadeArCondicionado; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getClienteTelefone() { return clienteTelefone; }
    public void setClienteTelefone(String clienteTelefone) { this.clienteTelefone = clienteTelefone; }

    public String getNivelComplexidade() { return nivelComplexidade; }
    public void setNivelComplexidade(String nivelComplexidade) { this.nivelComplexidade = nivelComplexidade; }

    public double getDistanciaPosteQuadro() { return distanciaPosteQuadro; }
    public void setDistanciaPosteQuadro(double distanciaPosteQuadro) { this.distanciaPosteQuadro = distanciaPosteQuadro; }
}