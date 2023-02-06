package com.etno.microservice.service.implementation

import com.etno.microservice.model.dto.*
import com.etno.microservice.repository.*
import com.etno.microservice.security.JwtTokenUtil
import com.etno.microservice.service.UserServiceInterface
import com.etno.microservice.service.implementation.jwt.JwtUserDetailsService
import com.etno.microservice.util.DataConverter
import com.etno.microservice.util.Urls
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.DateFormat
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val subscriptionUser: SubscriptionUserRepository,
    private val imageRepository: ImageRepository,
    private val pharmacyRepository: PharmacyRepository,
    private val tourismRepository: TourismRepository,
    private val deathRepository: DeathRepository,
    private val phoneRepository: PhoneRepository,
    private val newRepository: NewRepository,
    private val bandoRepository: BandoRepository,
    private val fcmTokenRepository: FCMTokenRepository,
    private val incidentRepository: IncidenceRepository,
    private val linkRepository: LinkRepository,
    private val sponsorRepository: SponsorRepository,
    private val adRepository: AdRepository,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: JwtUserDetailsService,
    private val jwtTokenUtil: JwtTokenUtil
): UserServiceInterface {

    override fun getUsers(): List<UserDTO>? {
        return userRepository.findAll().map { DataConverter.userToDTO(it) }
    }

    override fun signUp(userDTO: UserDTO): UserDTO? {
        val userItem = DataConverter.userFromDTO(userDTO)
        userItem.idUser = UUID.randomUUID()
        userItem.password = BCryptPasswordEncoder().encode(userItem.password)
        val userToSave = userRepository.save(userItem)
        return DataConverter.userToDTO(userToSave)
    }

    override fun login(username: String, password: String): ResponseEntity<*> {
        val responseMap: MutableMap<String, Any> = mutableMapOf()
        val user = userRepository.findUserByUsername(username)
        val dataFormatMedium = DateFormat.getDateInstance(DateFormat.MEDIUM)

        try {
            val auth: Authentication =
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                        username,
                        password
                    )
                )
            if (auth.isAuthenticated) {
                val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
                val token: String = jwtTokenUtil.generateToken(userDetails) ?: ""
                responseMap["error"] = false
                responseMap["message"] = "Logged In"
                responseMap["token"] = token
                responseMap["token_expired"] = jwtTokenUtil.isTokenExpired(token).toString()
                responseMap["expired_date"] =
                    dataFormatMedium.format(jwtTokenUtil.getExpirationDateFromToken(token)).toString()
                responseMap["user"] = DataConverter.userLoginToDTO(user!!)

                return ResponseEntity.ok(responseMap)
            } else {
                responseMap["error"] = true
                responseMap["message"] = "Invalid Credentials"
                return ResponseEntity.status(401).body(responseMap)
            }

        } catch (e: DisabledException) {
            e.printStackTrace();
            responseMap["error"] = true
            responseMap["message"] = "User is disabled"
            return ResponseEntity.status(500).body(responseMap);
        } catch (e: BadCredentialsException) {
            responseMap["error"] = true
            responseMap["message"] = "Invalid Credentials"
            return ResponseEntity.status(401).body(responseMap);
        } catch (e: Exception) {
            e.printStackTrace();
            responseMap["error"] = true
            responseMap["message"] = "Something went wrong";
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    override fun findUserByUsername(username: String): UserVillagerDTO? {
        return DataConverter.userVillagerToDTO(userRepository.findUserByUsername(username)!!)
    }

    override fun updateUserCredentials(name: String, username: String, password: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(name)
        itemUser?.username = username
        itemUser?.password = BCryptPasswordEncoder().encode(password)
        val itemUserSaved = userRepository.save(itemUser!!)

        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun addEventInUser(username: String, eventDTO: EventDTO): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemEvent = eventRepository.findEventByTitleAndUsername(eventDTO.title!!, username)

        if(itemEvent == null){
            eventDTO.username = itemUser?.username
            eventDTO.seats = eventDTO.capacity
            val itemEventSaved = eventRepository.save(DataConverter.eventFromDTO(eventDTO))
            itemUser?.events?.add(DataConverter.eventFromDTO(DataConverter.eventToDTO(itemEventSaved)))
            userRepository.save(itemUser!!)

            /*
            if (fcmTokenRepository.findAll().any { it.username == username }){
                val restTemplate = RestTemplate()
                val map: Map<String, String> = mapOf("subject" to "Nuevo evento", "content" to "Evento ${itemEventSaved.title} disponible", "username" to "${itemUser.username}")

                val response: ResponseEntity<Void> = restTemplate.postForEntity(Urls.urlSendNotification, map, Void::class.java)
            }
             */

        }else{
            val checkIfExistEvent = itemUser?.events?.find { it.title == itemEvent.title }
            if(checkIfExistEvent == null){
                eventDTO.username = itemUser?.username
                eventDTO.seats = eventDTO.capacity
                itemUser?.events?.add(DataConverter.eventFromDTO(eventDTO))
                userRepository.save(itemUser!!)
                /*
                if (fcmTokenRepository.findAll().any { it.username == username }){
                    val restTemplate = RestTemplate()
                    val map: Map<String, String> = mapOf(
                        "subject" to "Nuevo evento",
                        "content" to "Evento ${eventDTO.title} disponible",
                        "username" to "${itemUser.username}")

                    val response: ResponseEntity<Void> = restTemplate.postForEntity(Urls.urlSendNotification, map, Void::class.java)
                }
                 */
            }
        }
        return DataConverter.userToDTO(itemUser)
    }
    override fun deleteEventInUser(username: String, title: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemEvent = eventRepository.findEventByTitleAndUsername(title, username)

        itemUser?.events?.remove(itemEvent!!)

        itemEvent?.images?.forEach {
           val itemImage = imageRepository.findImageByLink(it.link!!)
           imageRepository.delete(itemImage!!)
        }

        val itemSaved = userRepository.save(itemUser!!)
        eventRepository.delete(itemEvent!!)
        return DataConverter.userToDTO(itemSaved)
    }

    override fun addImageToEventInUser(username: String, title: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemEvent = eventRepository.findEventByTitleAndUsername(title, username)
        val itemImage = imageRepository.findImageByName(imageName)

        itemEvent?.imageUrl = itemImage?.link
        itemUser?.events?.find { it.title == itemEvent?.title }?.images?.add(itemImage!!)
        val itemUserSaved = userRepository.save(itemUser!!)

        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun deleteImageToEventInUser(username: String, title: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemEvent = eventRepository.findEventByTitleAndUsername(title, username)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.events?.find{ it.title == itemEvent?.title }?.images?.remove(itemImage)
        val itemUserSaved = userRepository.save(itemUser!!)
        imageRepository.delete(itemImage!!)

        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun addSubscriptionToUser(
        username: String,
        title: String,
        subscriptionUserDTO: SubscriptionUserDTO
    ): SubscriptionUserDTO {
        var itemUser = userRepository.findUserByUsername(username)
        val itemEvent = eventRepository.findEventByTitleAndUsername(title, username)
        val subscriptionUser = subscriptionUser.findSubscriptionByFcmTokenAndTitle(subscriptionUserDTO.fcmToken!!, title)
        subscriptionUserDTO.title = title
        subscriptionUserDTO.isSubscribe = true

        if(subscriptionUser == null){
            when(itemEvent?.seats){
                in 1 .. itemEvent?.capacity!! -> {
                    itemEvent.seats = (itemEvent.seats!! - 1)
                    subscriptionUserDTO.seats = itemEvent.seats
                    itemUser?.events?.find { it.title == title }?.userSubscriptions?.add(
                        DataConverter.subscriptionUserFromDTO(subscriptionUserDTO)
                    )
                    itemUser = userRepository.save(itemUser!!)
                }
            }
        }else{
            when(itemEvent?.seats){
                in 1 .. itemEvent?.capacity!! -> {
                    itemEvent.seats = (itemEvent.seats!! - 1)
                    subscriptionUserDTO.seats = itemEvent.seats
                    itemUser?.events?.find { it.title == title }?.userSubscriptions?.find { it.fcmToken == subscriptionUserDTO.fcmToken }.also {
                        it?.title = subscriptionUserDTO.title
                        it?.name = subscriptionUserDTO.name
                        it?.seats = subscriptionUserDTO.seats
                        it?.phone = subscriptionUserDTO.phone
                        it?.wallet = subscriptionUserDTO.wallet
                        it?.isSubscribe = subscriptionUserDTO.isSubscribe
                    }
                    itemUser = userRepository.save(itemUser!!)
                }
            }
        }
        val findEvent = itemUser?.events?.find { it.title == title }
        val findUserSubscription = findEvent?.userSubscriptions?.find { it.fcmToken == subscriptionUserDTO.fcmToken }
        return DataConverter.subscriptionUserToDTO(findUserSubscription!!)
    }

    override fun dropOutSubscription(
        username: String,
        title: String,
        subscriptionUserDTO: SubscriptionUserDTO
    ): SubscriptionUserDTO? {
        var itemUser = userRepository.findUserByUsername(username)
        val itemEvent = eventRepository.findEventByTitleAndUsername(title, username)
        val subscriptionUser =
            subscriptionUser.findSubscriptionByFcmTokenAndTitle(subscriptionUserDTO.fcmToken!!, title)

        if (subscriptionUser != null) {
            when (itemEvent?.seats) {
                in 0..itemEvent?.capacity!! -> {
                    itemEvent.seats = (itemEvent.seats!! + 1)
                    subscriptionUserDTO.seats = itemEvent.seats
                    itemUser?.events?.find { it.title == title }?.userSubscriptions?.find { it.fcmToken == subscriptionUserDTO.fcmToken }
                        .also {
                            it?.seats = subscriptionUserDTO.seats
                            it?.isSubscribe = false
                        }
                    itemUser = userRepository.save(itemUser!!)
                }
            }
        }
        val findEvent = itemUser?.events?.find { it.title == title }
        val findUserSubscription = findEvent?.userSubscriptions?.find { it.fcmToken == subscriptionUserDTO.fcmToken }
        return DataConverter.subscriptionUserToDTO(findUserSubscription!!)
    }

    override fun addPharmacyInUser(username: String, pharmacyDTO: PharmacyDTO): UserDTO? {

        var itemUser = userRepository.findUserByUsername(username)
        val itemPharmacy = pharmacyRepository.findPharmacyByNameAndUsername(pharmacyDTO.name!!, username)

        if(itemPharmacy == null){
            pharmacyDTO.idPharmacy = UUID.randomUUID()
            pharmacyDTO.username = itemUser?.username
            itemUser?.pharmacies?.add(DataConverter.pharmacyFromDTO(pharmacyDTO))
            itemUser = userRepository.save(itemUser!!)
        }else{
            val checkIfExistPharmacy = itemUser?.pharmacies?.find { it.name == itemPharmacy.name }
            if(checkIfExistPharmacy == null){
                pharmacyDTO.idPharmacy = UUID.randomUUID()
                pharmacyDTO.username = itemUser?.username
                itemUser?.pharmacies?.add(DataConverter.pharmacyFromDTO(pharmacyDTO))
               itemUser = userRepository.save(itemUser!!)
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun deletePharmacyInUser(username: String, name: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemPharmacy = pharmacyRepository.findPharmacyByNameAndUsername(name, username)
        val itemImageDelete = imageRepository.findImageByLink(itemPharmacy?.imageUrl!!)

        itemUser?.pharmacies?.remove(itemPharmacy)
        imageRepository.delete(itemImageDelete!!)
        val itemSaved = userRepository.save(itemUser!!)
        pharmacyRepository.delete(itemPharmacy)
        return DataConverter.userToDTO(itemSaved)
    }

    override fun addImageToPharmacyInUser(username: String, name: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemPharmacy = pharmacyRepository.findPharmacyByNameAndUsername(name, username)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.pharmacies?.find { it.name == itemPharmacy?.name }?.imageUrl = itemImage?.link
        val itemUserSaved = userRepository.save(itemUser!!)

        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun deleteImageToPharmacyInUser(username: String, name: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemPharmacy = pharmacyRepository.findPharmacyByNameAndUsername(name, username)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.pharmacies?.find { it.name == itemPharmacy?.name }?.imageUrl = ""
        val itemUserSaved = userRepository.save(itemUser!!)

        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun addTourismInUser(username: String, tourismDTO: TourismDTO): UserDTO? {
        var itemUser = userRepository.findUserByUsername(username)
        val itemTourism = tourismRepository.findTourismByTitleAndUsername(tourismDTO.title!!, username)

        if(itemTourism == null){
            tourismDTO.idTourism = UUID.randomUUID()
            tourismDTO.username = username
            itemUser?.tourism?.add(DataConverter.tourismFromDTO(tourismDTO))
            itemUser = userRepository.save(itemUser!!)
        }else{
            val checkIfExistTourism = itemUser?.tourism?.find { it.title == itemTourism.title }
            if(checkIfExistTourism == null){
                tourismDTO.idTourism = UUID.randomUUID()
                tourismDTO.username = username
                itemUser?.tourism?.add(DataConverter.tourismFromDTO(tourismDTO))
              itemUser = userRepository.save(itemUser!!)
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun deleteTourismInUser(username: String, title: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemTourism = tourismRepository.findTourismByTitleAndUsername(title, username)
//        val itemImageDelete = imageRepository.findImageByLink(itemTourism?.imageUrl!!)

        itemUser?.tourism?.remove(itemTourism)
       // imageRepository.delete(itemImageDelete!!)
        val itemUserSaved = userRepository.save(itemUser!!)
        tourismRepository.delete(itemTourism!!)
        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun addImageToTourismInUser(username: String, title: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemTourism = tourismRepository.findTourismByTitleAndUsername(title, username)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.tourism?.find { it.title == itemTourism?.title }?.imageUrl = itemImage?.link
        val itemUserSaved = userRepository.save(itemUser!!)
        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun deleteImageToTourismInUser(username: String, title: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemTourism = tourismRepository.findTourismByTitleAndUsername(title, username)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.tourism?.find { it.title == itemTourism?.title }?.imageUrl = ""
        val itemUserSaved = userRepository.save(itemUser!!)
        imageRepository.delete(itemImage!!)

        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun addDeathInUser(username: String, deathDTO: DeathDTO): UserDTO? {
        var itemUser = userRepository.findUserByUsername(username)
        val itemDeath = deathRepository.findDeathByUsernameAndName(username, deathDTO.name!!)

        if (itemDeath == null){
            deathDTO.idDeath = UUID.randomUUID()
            deathDTO.username = username
            itemUser?.deaths?.add(DataConverter.deathFromDTO(deathDTO))
            itemUser = userRepository.save(itemUser!!)
        }else{
            val checkIfExistDeath = itemUser?.deaths?.find { it.name == itemDeath.name }
            if (checkIfExistDeath == null){
                deathDTO.idDeath = UUID.randomUUID()
                deathDTO.username = username
                itemUser?.deaths?.add(DataConverter.deathFromDTO(deathDTO))
                itemUser = userRepository.save(itemUser!!)
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun deleteDeathInUser(username: String, name: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemDeath = deathRepository.findDeathByUsernameAndName(username, name)
        val itemImageDelete = imageRepository.findImageByLink(itemDeath?.imageUrl!!)

        itemUser?.deaths?.remove(itemDeath)
        imageRepository.delete(itemImageDelete!!)
        val itemUserSaved = userRepository.save(itemUser!!)
        deathRepository.delete(itemDeath)
        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun addImageToDeathInUser(username: String, name: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemDeath = deathRepository.findDeathByUsernameAndName(username, name)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.deaths?.find { it.name == itemDeath?.name }?.imageUrl = itemImage?.link
        val itemUserSaved = userRepository.save(itemUser!!)
        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun deleteImageToDeathInUser(username: String, name: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemDeath = deathRepository.findDeathByUsernameAndName(username, name)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.deaths?.find { it.name == itemDeath?.name }?.imageUrl = ""
        val itemUserSaved = userRepository.save(itemUser!!)
        imageRepository.delete(itemImage!!)

        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun addPhoneInUser(username: String, phoneDTO: PhoneDTO): UserDTO? {
        var itemUser = userRepository.findUserByUsername(username)
        val itemPhone = phoneRepository.findPhoneByUsernameAndOwner(username, phoneDTO.owner!!)

        if(itemPhone == null){
            phoneDTO.idPhone = UUID.randomUUID()
            phoneDTO.username = itemUser?.username
            phoneDTO.category = phoneDTO.category
            itemUser?.phones?.add(DataConverter.phoneFromDTO(phoneDTO))
            itemUser = userRepository.save(itemUser!!)
        }else{
            val checkIfExistPhone = itemUser?.phones?.find { it.owner == itemPhone.owner }
            if(checkIfExistPhone == null){
                phoneDTO.idPhone = UUID.randomUUID()
                phoneDTO.username = itemUser?.username
                itemUser?.phones?.add(DataConverter.phoneFromDTO(phoneDTO))
                itemUser = userRepository.save(itemUser!!)
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun deletePhoneInUser(username: String, owner: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemPhone = phoneRepository.findPhoneByUsernameAndOwner(username, owner)
        val itemImageDelete = imageRepository.findImageByLink(itemPhone?.imageUrl!!)

        itemUser?.phones?.remove(itemPhone)
        imageRepository.delete(itemImageDelete!!)
        val itemSaved = userRepository.save(itemUser!!)
        phoneRepository.delete(itemPhone)
        return DataConverter.userToDTO(itemSaved)
    }

    override fun addImageToPhoneInUser(username: String, owner: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemPhone = phoneRepository.findPhoneByUsernameAndOwner(username, owner)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.phones?.find { it.owner == itemPhone?.owner }?.imageUrl = itemImage?.link
        val itemUserSaved = userRepository.save(itemUser!!)
        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun deleteImageToPhoneInUser(username: String, owner: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemPhone = phoneRepository.findPhoneByUsernameAndOwner(username, owner)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.phones?.find { it.owner == itemPhone?.owner }?.imageUrl = ""
        val itemUserSaved = userRepository.save(itemUser!!)
        imageRepository.delete(itemImage!!)
        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun addNewInUser(username: String, newDTO: NewDTO): UserDTO? {
        var itemUser = userRepository.findUserByUsername(username)
        val itemNew = newRepository.findNewByUsernameAndTitle(username, newDTO.title!!)

        if(itemNew == null){
            newDTO.idNew = UUID.randomUUID()
            newDTO.username = itemUser?.username
            itemUser?.news?.add(DataConverter.newFromDTO(newDTO))
            itemUser = userRepository.save(itemUser!!)
        }else{
            val checkIfExistPhone = itemUser?.news?.find { it.title == itemNew.title }
            if(checkIfExistPhone == null){
                newDTO.idNew = UUID.randomUUID()
                newDTO.username = itemUser?.username
                itemUser?.news?.add(DataConverter.newFromDTO(newDTO))
                itemUser = userRepository.save(itemUser!!)
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun deleteNewInUser(username: String, title: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemNew = newRepository.findNewByUsernameAndTitle(username, title)
        val itemImageDelete = imageRepository.findImageByLink(itemNew?.imageUrl!!)

        itemUser?.news?.remove(itemNew)
        imageRepository.delete(itemImageDelete!!)
        val itemSaved = userRepository.save(itemUser!!)
        newRepository.delete(itemNew)
        return DataConverter.userToDTO(itemSaved)
    }

    override fun addImageToNewInUser(username: String, title: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemNew = newRepository.findNewByUsernameAndTitle(username, title)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.news?.find { it.title == itemNew?.title }?.imageUrl = itemImage?.link
        val itemUserSaved = userRepository.save(itemUser!!)
        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun deleteImageToNewInUser(username: String, title: String, imageName: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemNew = newRepository.findNewByUsernameAndTitle(username, title)
        val itemImage = imageRepository.findImageByName(imageName)

        itemUser?.news?.find { it.title == itemNew?.title }?.imageUrl = ""
        val itemUserSaved = userRepository.save(itemUser!!)
        imageRepository.delete(itemImage!!)
        return DataConverter.userToDTO(itemUserSaved)
    }

    override fun addIncidentInUser(username: String, incidentDTO: IncidentDTO): UserDTO? {
        var itemUser = userRepository.findUserByUsername(username)
        val itemIncident = incidentRepository.findIncidentByUsernameAndTitle(username, incidentDTO.title!!)

        if(itemIncident == null){
            incidentDTO.idIncidence = UUID.randomUUID()
            incidentDTO.username = username
            itemUser?.incidents?.add(DataConverter.incidenceFromDTO(incidentDTO))
            itemUser = userRepository.save(itemUser!!)
        }else{
            val checkIfExistIncident = itemUser?.incidents?.find { it.title == incidentDTO.title && it.fcmToken == incidentDTO.fcmToken }
            if(checkIfExistIncident == null){
                incidentDTO.idIncidence = UUID.randomUUID()
                incidentDTO.username = username
                itemUser?.incidents?.add(DataConverter.incidenceFromDTO(incidentDTO))
                itemUser = userRepository.save(itemUser!!)
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun addBandosInUser(username: String, bandoDTO: BandoDTO): UserDTO? {
        var itemUser = userRepository.findUserByUsername(username)

        when(bandoRepository.findBandoByUsernameAndTitle(username, bandoDTO.title!!)){
            null -> {
                bandoDTO.idBando = UUID.randomUUID()
                bandoDTO.username = username
                itemUser?.bandos?.add(DataConverter.bandoFromDTO(bandoDTO))
                itemUser = userRepository.save(itemUser!!)

                if (fcmTokenRepository.findAll().any { it.username == username }){
                    val restTemplate = RestTemplate()
                    val map: Map<String, String> = mapOf(
                        "image" to "${bandoDTO.imageUrl}",
                        "subject" to "Publicación de Bando",
                        "content" to "Bando: ${bandoDTO.title}",
                        "username" to "${itemUser.username}")

                    val response: ResponseEntity<Void> = restTemplate.postForEntity(Urls.urlSendNotification, map, Void::class.java)
                }

            }
            else -> {
                val checkIfExistBando = itemUser?.bandos?.find { it.title == bandoDTO.title }
                if (checkIfExistBando == null){
                    bandoDTO.idBando = UUID.randomUUID()
                    bandoDTO.username = username
                    itemUser?.bandos?.add(DataConverter.bandoFromDTO(bandoDTO))
                    itemUser = userRepository.save(itemUser!!)

                    if (fcmTokenRepository.findAll().any { it.username == username }){
                        val restTemplate = RestTemplate()
                        val map: Map<String, String> = mapOf(
                            "image" to "${bandoDTO.imageUrl}",
                            "subject" to "Publicación de Bando",
                            "content" to "Bando: ${bandoDTO.title}",
                            "username" to "${itemUser.username}")

                        val response: ResponseEntity<Void> = restTemplate.postForEntity(Urls.urlSendNotification, map, Void::class.java)
                    }
                }
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun addLinkInUser(username: String, linkDTO: LinkDTO): UserDTO? {
        var itemUser = userRepository.findUserByUsername(username)

        when(linkRepository.findLinkByUsernameAndTitle(username = username, title = linkDTO.title!!)){
            null -> {
                linkDTO.idLink = UUID.randomUUID()
                linkDTO.username = username
                itemUser?.links?.add(DataConverter.linkFromDTO(linkDTO))
                itemUser = userRepository.save(itemUser!!)
            }
            else -> {
                val checkIfExistLink = itemUser?.links?.find { it.title == linkDTO.title }
                if (checkIfExistLink == null){
                    linkDTO.idLink = UUID.randomUUID()
                    linkDTO.username = username
                    itemUser?.links?.add(DataConverter.linkFromDTO(linkDTO))
                    itemUser = userRepository.save(itemUser!!)
                }
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun addSponsorInUser(username: String, sponsorDTO: SponsorDTO): UserDTO? {
        var itemUser = userRepository.findUserByUsername(username)

        when(sponsorRepository.findSponsorByUsernameAndTitle(username, sponsorDTO.title!!)){
            null -> {
                sponsorDTO.idSponsor = UUID.randomUUID()
                sponsorDTO.username = username
                itemUser?.sponsors?.add(DataConverter.sponsorFromDTO(sponsorDTO))
                itemUser = userRepository.save(itemUser!!)
            }
            else -> {
                val checkIfExistSponsor = itemUser?.sponsors?.find { it.title == sponsorDTO.title }
                if (checkIfExistSponsor == null){
                    sponsorDTO.idSponsor = UUID.randomUUID()
                    sponsorDTO.username = username
                    itemUser?.sponsors?.add(DataConverter.sponsorFromDTO(sponsorDTO))
                    itemUser = userRepository.save(itemUser!!)
                }
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun addAdInUser(username: String, adDTO: AdDTO): UserDTO? {
        var itemUser = userRepository.findUserByUsername(username)

        when(adRepository.findAdByUsernameAndTitle(username, adDTO.title!!)){
            null -> {
                adDTO.idAd = UUID.randomUUID()
                adDTO.username = username
                itemUser?.ads?.add(DataConverter.adFromDTO(adDTO))
                itemUser = userRepository.save(itemUser!!)
            }
            else -> {
                val checkIfExistAd = itemUser?.ads?.find { it.title == adDTO.title }
                if (checkIfExistAd == null){
                    adDTO.idAd = UUID.randomUUID()
                    adDTO.username = username
                    itemUser?.ads?.add(DataConverter.adFromDTO(adDTO))
                    itemUser = userRepository.save(itemUser!!)
                }
            }
        }
        return DataConverter.userToDTO(itemUser!!)
    }

    override fun deleteAdInUser(username: String, title: String): UserDTO? {
        val itemUser = userRepository.findUserByUsername(username)
        val itemAd = adRepository.findAdByUsernameAndTitle(username, title)
        val itemImageDelete = imageRepository.findImageByLink(itemAd?.imageUrl!!)

        itemUser?.ads?.remove(itemAd)
       // imageRepository.delete(itemImageDelete!!)
        val itemSaved = userRepository.save(itemUser!!)
        adRepository.delete(itemAd)
        return DataConverter.userToDTO(itemSaved)
    }
}