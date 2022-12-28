package com.etno.microservice.controller.implementation

import com.etno.microservice.controller.UserControllerInterface
import com.etno.microservice.model.dto.*
import com.etno.microservice.service.implementation.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
):UserControllerInterface {
    override fun getUsers(): ResponseEntity<List<UserDTO>>? {
        return ResponseEntity.ok().body(userService.getUsers())
    }

    override fun saveUser(userDTO: UserDTO): ResponseEntity<UserDTO>? {
        return ResponseEntity.ok().body(userService.signUp(userDTO))
    }

    override fun login(username: String, password: String): ResponseEntity<*>? {
        return userService.login(username, password)
    }

    override fun findUserByUsernameToVillager(username: String): ResponseEntity<UserVillagerDTO> {
        return ResponseEntity.ok().body(userService.findUserByUsername(username))
    }

    override fun updateUser(name: String, username: String, password: String): ResponseEntity<UserDTO>? {
        return ResponseEntity.ok().body(userService.updateUserCredentials(name, username, password))
    }

    override fun addEventInUser(username: String, eventDTO: EventDTO): ResponseEntity<UserDTO>? {
        return ResponseEntity.ok().body(userService.addEventInUser(username, eventDTO))
    }

    override fun addImageToEventInUser(username: String, title: String, image: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.addImageToEventInUser(username, title, image))
    }

    override fun deleteEventInUser(username: String, title: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.deleteEventInUser(username, title))
    }

    override fun deleteImageToEventInUser(username: String, title: String, imageName: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.deleteImageToEventInUser(username, title, imageName))
    }

    override fun addPharmacyInUser(username: String, pharmacyDTO: PharmacyDTO): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.addPharmacyInUser(username, pharmacyDTO))
    }

    override fun deletePharmacyInUser(username: String, name: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.deletePharmacyInUser(username, name))
    }

    override fun addImageToPharmacyInUser(username: String, name: String, imageName: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.addImageToPharmacyInUser(username, name, imageName))
    }

    override fun deleteImageToPharmacyInUser(
        username: String,
        name: String,
        imageName: String
    ): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.deleteImageToPharmacyInUser(username, name, imageName))
    }

    override fun addTourismInUser(username: String, tourismDTO: TourismDTO): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.addTourismInUser(username, tourismDTO))
    }

    override fun addImageToTourismInUser(username: String, title: String, imageName: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.addImageToTourismInUser(username, title, imageName))
    }

    override fun deleteTourismInUser(username: String, title: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.deleteTourismInUser(username, title))
    }

    override fun deleteImageToTourismInUser(
        username: String,
        title: String,
        imageName: String
    ): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.deleteImageToTourismInUser(username, title, imageName))
    }

    override fun addDeathInUser(username: String, deathDTO: DeathDTO): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.addDeathInUser(username, deathDTO))
    }

    override fun deleteDeathInUser(username: String, name: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.deleteDeathInUser(username, name))
    }

    override fun addImageToDeathInUser(username: String, name: String, imageName: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.addImageToDeathInUser(username, name, imageName))
    }

    override fun deleteImageToDeathInUser(username: String, name: String, imageName: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok().body(userService.deleteImageToDeathInUser(username, name, imageName))
    }
}