package com.ejemplo.nominatimgeolocalizacionapi.service.impl;

import com.ejemplo.nominatimgeolocalizacionapi.dto.UbicacionResponseDTO;
import com.ejemplo.nominatimgeolocalizacionapi.exception.GeolocalizacionException;
import com.ejemplo.nominatimgeolocalizacionapi.service.GeolocalizacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class GeolocalizacionServiceImpl implements GeolocalizacionService {

    private static final Logger logger = LoggerFactory.getLogger(GeolocalizacionServiceImpl.class);

    private final RestClient nominatimRestClient;

    public GeolocalizacionServiceImpl(RestClient nominatimRestClient) {
        this.nominatimRestClient = nominatimRestClient;
    }

    @Override
    public List<UbicacionResponseDTO> buscarPorDireccion(String direccion) {
        logger.info("Buscando ubicacion por direccion: {}", direccion);

        try {
            List<UbicacionResponseDTO> resultados = nominatimRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", direccion)
                            .queryParam("format", "json")
                            .queryParam("limit", 5)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UbicacionResponseDTO>>() {});

            if (resultados == null || resultados.isEmpty()) {
                logger.warn("No se encontraron resultados para la direccion: {}", direccion);
                return List.of();
            }

            logger.info("Se encontraron {} resultados para la direccion: {}", resultados.size(), direccion);
            return resultados;

        } catch (RestClientException ex) {
            logger.error("Error al buscar ubicacion por direccion {}: {}", direccion, ex.getMessage());
            throw new GeolocalizacionException("No se pudo obtener la ubicacion para la direccion: " + direccion, ex);
        }
    }

    @Override
    public UbicacionResponseDTO buscarPorCoordenadas(Double latitud, Double longitud) {
        logger.info("Buscando direccion por coordenadas: lat={}, lon={}", latitud, longitud);

        try {
            UbicacionResponseDTO resultado = nominatimRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reverse")
                            .queryParam("lat", latitud)
                            .queryParam("lon", longitud)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(UbicacionResponseDTO.class);

            if (resultado == null) {
                logger.warn("No se encontraron resultados para las coordenadas: lat={}, lon={}", latitud, longitud);
                throw new GeolocalizacionException("No se encontro direccion para las coordenadas proporcionadas");
            }

            logger.info("Direccion encontrada: {}", resultado.direccionCompleta());
            return resultado;

        } catch (RestClientException ex) {
            logger.error("Error al buscar direccion por coordenadas lat={}, lon={}: {}", latitud, longitud, ex.getMessage());
            throw new GeolocalizacionException("No se pudo obtener la direccion para las coordenadas proporcionadas", ex);
        }
    }
}