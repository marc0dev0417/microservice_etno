package com.etno.microservice.model

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "phones")
data class Phone(
    @Id
    @Type(type = "uuid-char")
    var idPhone: UUID ? = UUID.randomUUID(),

    @Column(name = "username")
    var username: String ? = null,

    @Column(name = "category")
    var category: String ? = null,

    @Column(name = "owner")
    var owner: String ? = null,

    @Column(name = "number")
    var number: String ? = null
)
