package com.etno.microservice.model

import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "festivities")
data class Festivity(
    @Id
    @Type(type = "uuid-char")
    var idFestivity: UUID? = UUID.randomUUID(),

    @Column(name = "title")
    var title: String? = null,

    @Column(name = "address")
    var address: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "organization")
    var organization: String? = null,

    @Column(name = "link")
    var link: String? = null,

    @Column(name = "startDate")
    var startDate: String? = null,

    @Column(name = "endDate")
    var endDate: String? = null,

    @Column(name = "publicationDate")
    var publicationDate: String? = null,

    @Column(name = "time")
    var time: String? = null,

    @Column(name = "latitude")
    var lat: String? = null,

    @Column(name = "longitude")
    var long: String? = null,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var images: MutableList<Image>? = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var videos: MutableList<Video>? = mutableListOf()
    )