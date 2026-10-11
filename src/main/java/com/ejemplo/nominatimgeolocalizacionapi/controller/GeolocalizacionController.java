package com.ejemplo.nominatimgeolocalizacionapi.controller;

import com.ejemplo.nominatimgeolocalizacionapi.dto.UbicacionResponseDTO;
import com.ejemplo.nominatimgeolocalizacionapi.service.GeolocalizacionService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geolocalizacion")
@Validated
public class GeolocalizacionController {

    private static final Logger logger = LoggerFactory.getLogger(GeolocalizacionController.class);

    private final GeolocalizacionService geolocalizacionService;

    public GeolocalizacionController(GeolocalizacionService geolocalizacionService) {
        this.geolocalizacionService = geolocalizacionService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UbicacionResponseDTO>> buscar(
            @RequestParam
            @NotBlank(message = "La direccion es obligatoria")
            @Size(min = 3, max = 200, message = "La direccion debe tener entre 3 y 200 caracteres")
            String direccion) {

        logger.info("Peticion GET recibida en /api/v1/geolocalizacion/buscar?direccion={}", direccion);

        List<UbicacionResponseDTO> resultados = geolocalizacionService.buscarPorDireccion(direccion);

        logger.info("Retornando {} resultados", resultados.size());
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/reversa")
    public ResponseEntity<UbicacionResponseDTO> reversa(
            @RequestParam
            @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
            @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
            Double latitud,

            @RequestParam
            @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
            @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
            Double longitud) {

        logger.info("Peticion GET recibida en /api/v1/geolocalizacion/reversa?latitud={}&longitud={}", latitud, longitud);

        UbicacionResponseDTO resultado = geolocalizacionService.buscarPorCoordenadas(latitud, longitud);

        logger.info("Direccion encontrada: {}", resultado.direccionCompleta());
        return ResponseEntity.ok(resultado);
    }
}