package com.etno.microservice.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ServiceDTO(
    @JsonProperty("idService") var idService: UUID ? = UUID.randomUUID(),

    @JsonProperty("username") var username: String ? = null,

    @JsonProperty("category") var category: String ? = null,

    @JsonProperty("owner") var owner: String ? = null,

    @JsonProperty("number") var number: String ? = null,

    @JsonProperty("description") var description: String ? = null,

    @JsonProperty("webUrl") var webUrl: String ? = null,

    @JsonProperty("schedule") var schedule: String ? = null,

    @JsonProperty("imageUrl") var imageUrl: String ? = null
)