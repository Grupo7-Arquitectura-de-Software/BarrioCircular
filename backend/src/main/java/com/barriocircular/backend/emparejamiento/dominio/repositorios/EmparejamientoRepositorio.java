package com.barriocircular.backend.emparejamiento.dominio.repositorios;

import com.barriocircular.backend.emparejamiento.dominio.modelo.agregado.ResultadoEmparejamiento;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.CompradorId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface EmparejamientoRepositorio {

    ResultadoEmparejamiento guardar(ResultadoEmparejamiento resultado);

    Optional<ResultadoEmparejamiento> buscarPorId(UUID id);

    List<ResultadoEmparejamiento> listarPorComprador(CompradorId compradorId);
}
