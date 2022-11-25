package com.etno.microservice.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class EventDTO(
    @JsonProperty("idEvent") var idEvent: UUID? = UUID.randomUUID(),

    @JsonProperty("title") var title: String? = null,

    @JsonProperty("address") var address: String? = null,

    @JsonProperty("description") var description: String? = null,

    @JsonProperty("organization") var organization: String? = null,

    @JsonProperty("link") var link: String? = null,

    @JsonProperty("startDate") var startDate: String? = null,

    @JsonProperty("endDate") var endDate: String? = null,

    @JsonProperty("publicationDate") var publicationDate: String? = null,

    @JsonProperty("time") var time: String? = null,

    @JsonProperty("lat") var latitude: String? = null,

    @JsonProperty("long") var longitude: String? = null,

    @JsonProperty("images") var images: MutableList<ImageDTO>? = mutableListOf(),

    @JsonProperty("videos") var videos: MutableList<VideoDTO>? = mutableListOf()
)
