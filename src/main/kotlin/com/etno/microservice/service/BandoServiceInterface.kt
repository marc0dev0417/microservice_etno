package com.etno.microservice.service

import com.etno.microservice.model.dto.BandoDTO
import org.springframework.stereotype.Service

@Service
interface BandoServiceInterface {
    fun getBando() : List<BandoDTO>?
    fun saveBando(bandoDTO: BandoDTO): BandoDTO
}