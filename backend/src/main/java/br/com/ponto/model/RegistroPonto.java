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
import java.time.LocalTime;

@Entity
@Table(name = "registros_ponto")
public class RegistroPonto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Funcionario funcionario;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMarcacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigemPonto origem;

    @Enumerated(EnumType.STRING)
    private MotivoManual motivo;

    protected RegistroPonto() {
    }

    public RegistroPonto(Funcionario funcionario, LocalDate data, LocalTime hora, TipoMarcacao tipo,
                         OrigemPonto origem, MotivoManual motivo) {
        this.funcionario = funcionario;
        this.data = data;
        this.hora = hora;
        this.tipo = tipo;
        this.origem = origem;
        this.motivo = motivo;
    }

    public Long getId() { return id; }
    public Funcionario getFuncionario() { return funcionario; }
    public LocalDate getData() { return data; }
    public LocalTime getHora() { return hora; }
    public TipoMarcacao getTipo() { return tipo; }
    public OrigemPonto getOrigem() { return origem; }
    public MotivoManual getMotivo() { return motivo; }
}
