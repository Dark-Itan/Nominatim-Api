package com.ejemplo.nominatimgeolocalizacionapi.service;

import com.ejemplo.nominatimgeolocalizacionapi.dto.UbicacionResponseDTO;

import java.util.List;

public interface GeolocalizacionService {

    List<UbicacionResponseDTO> buscarPorDireccion(String direccion);

    UbicacionResponseDTO buscarPorCoordenadas(Double latitud, Double longitud);
}