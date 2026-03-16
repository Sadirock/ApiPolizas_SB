package com.segurosbolivar.polizasapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.segurosbolivar.polizasapp.entity.Poliza;
import com.segurosbolivar.polizasapp.entity.enums.EstadoPoliza;
import com.segurosbolivar.polizasapp.entity.enums.TipoPoliza;

import java.util.List;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    List<Poliza> findByTipo(TipoPoliza tipo);

    List<Poliza> findByEstado(EstadoPoliza estado);

    List<Poliza> findByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);
}
