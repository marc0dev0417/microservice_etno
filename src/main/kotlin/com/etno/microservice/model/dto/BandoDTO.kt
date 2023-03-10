package com.etno.microservice.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date
import java.util.UUID

data class BandoDTO(
    @JsonProperty("idBando") var idBando: UUID ? = null,

    @JsonProperty("username") var username: String ? = null,

    @JsonProperty("title") var title: String ? = null,

    @JsonProperty("description") var description: String ? = null,

    @JsonProperty("issuedDate") var issuedDate: String ? = null,

    @JsonProperty("imageUrl") var imageUrl: String ? = null
)