package com.etno.microservice.service.implementation

import com.etno.microservice.model.dto.ImageDTO
import com.etno.microservice.repository.ImageRepository
import com.etno.microservice.service.ImageServiceInterface
import com.etno.microservice.util.DataConverter
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Service
class ImageService(
    private val imageRepository: ImageRepository
): ImageServiceInterface {
    override fun saveImage(multipartFile: MultipartFile): ImageDTO? {
        val routeBase = "http://192.168.137.1:8080/images/"
        val converterFile = File("src\\main\\resources\\images\\${multipartFile.originalFilename}")
        converterFile.createNewFile()

        val fos = FileOutputStream(converterFile)
        fos.write(multipartFile.bytes)
        fos.close()

        val itemImage = com.etno.microservice.model.Image()
        itemImage.idImage = UUID.randomUUID()
        itemImage.name = multipartFile.originalFilename
        itemImage.link = "$routeBase${multipartFile.originalFilename}"

        val itemToSave = imageRepository.save(itemImage)

        return DataConverter.imageToDTO(itemToSave)
    }

    override fun getImages(): List<ImageDTO>? {
        return imageRepository.findAll().map { DataConverter.imageToDTO(it) }
    }

    override fun deleteImage(name: String): ImageDTO? {
        val imageItem = imageRepository.findImageByName(name)
        val file = File("src\\main\\resources\\images\\${imageItem.name}")

        if(imageItem.name != null){
            imageRepository.delete(imageItem)
            file.delete()
        }
        return DataConverter.imageToDTO(imageItem)
    }
}


