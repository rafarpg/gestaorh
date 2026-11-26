package br.com.ponto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "solicitacoes_ferias")
public class SolicitacaoFerias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Funcionario funcionario;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFerias status = StatusFerias.PENDENTE;

    protected SolicitacaoFerias() {
    }

    public SolicitacaoFerias(Funcionario funcionario, LocalDate dataInicio, LocalDate dataFim) {
        this.funcionario = funcionario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public Long getId() { return id; }
    public Funcionario getFuncionario() { return funcionario; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public StatusFerias getStatus() { return status; }
    public void setStatus(StatusFerias status) { this.status = status; }
}
