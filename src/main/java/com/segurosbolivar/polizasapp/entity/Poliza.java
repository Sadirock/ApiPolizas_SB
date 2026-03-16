package com.segurosbolivar.polizasapp.entity;

import com.segurosbolivar.polizasapp.entity.enums.EstadoPoliza;
import com.segurosbolivar.polizasapp.entity.enums.TipoPoliza;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
@Getter
@Setter
@NoArgsConstructor
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal canon;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prima;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Riesgo> riesgos = new ArrayList<>();
}