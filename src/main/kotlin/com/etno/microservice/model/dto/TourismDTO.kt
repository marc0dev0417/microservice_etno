package com.etno.microservice.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class TourismDTO(
    @JsonProperty("idTourism") var idTourism: UUID? = UUID.randomUUID(),

    @JsonProperty("title") var title: String? = null,

    @JsonProperty("description") var description: String? = null,

    @JsonProperty("images") var images: MutableList<ImageDTO>?= mutableListOf(),

    @JsonProperty("latitude") var latitude: String? = null,

    @JsonProperty("longitude") var longitude: String? = null
)