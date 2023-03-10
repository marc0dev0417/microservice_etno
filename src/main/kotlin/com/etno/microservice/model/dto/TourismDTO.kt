package com.etno.microservice.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class TourismDTO(
    @JsonProperty("idTourism") var idTourism: UUID ? = UUID.randomUUID(),

    @JsonProperty("type") var type: String ? = null,

    @JsonProperty("username") var username: String ? = null,

    @JsonProperty("title") var title: String ? = null,

    @JsonProperty("description") var description: String ? = null,

    @JsonProperty("imageUrl") var imageUrl: String ? = null,

    @JsonProperty("longitude") var longitude: Double ? = null,

    @JsonProperty("latitude") var latitude: Double ? = null
)