package com.etno.microservice.repository

import com.etno.microservice.model.Pharmacy
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PharmacyRepository: JpaRepository<Pharmacy, UUID> {
        fun findPharmacyByUsername(username: String): List<Pharmacy>?
        fun findPharmacyByNameAndUsername(name: String, username: String): Pharmacy?
    fun findPharmacyPageableByUsername(username: String, pageable: PageRequest): Page<Pharmacy>?
}