package com.segurosbolivar.polizasapp.reposityory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.segurosbolivar.polizasapp.entity.Riesgo;
import com.segurosbolivar.polizasapp.entity.enums.EstadoRiesgo;

import java.util.List;

@Repository
public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {

    List<Riesgo> findByPolizaId(Long polizaId);

    List<Riesgo> findByPolizaIdAndEstado(Long polizaId, EstadoRiesgo estado);
}