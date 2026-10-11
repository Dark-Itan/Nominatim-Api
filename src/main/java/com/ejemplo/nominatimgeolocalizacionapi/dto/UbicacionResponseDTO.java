package com.ejemplo.nominatimgeolocalizacionapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UbicacionResponseDTO(

        @JsonProperty("place_id")
        Long placeId,

        @JsonProperty("display_name")
        String direccionCompleta,

        @JsonProperty("lat")
        String latitud,

        @JsonProperty("lon")
        String longitud,

        @JsonProperty("type")
        String tipo,

        @JsonProperty("importance")
        Double importancia
) {
}