package com.etno.microservice.model

import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "deaths")
data class Death(
    @Id
    @Type(type = "uuid-char")
    var idDeath: UUID ? = UUID.randomUUID(),

    @Column(name = "username")
    var username: String ? = null,

    @Column(name = "name")
    var name: String ? = null,

    @Column(name = "deathDate")
    var deathDate: String ? = null,

    @Column(name = "description")
    var description: String ? = null
)
