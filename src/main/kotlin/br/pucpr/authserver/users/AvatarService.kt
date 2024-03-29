package br.pucpr.authserver.users

import br.pucpr.authserver.exception.UnsupportedMediaTypeException
import br.pucpr.authserver.files.FileStorage
import br.pucpr.authserver.files.S3Storage
import br.pucpr.authserver.users.controller.responses.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AvatarService(@Qualifier("fileStorage") val storage: FileStorage) {
    fun save(user: User, avatar: MultipartFile): String =
        try {
            val contentType = avatar.contentType!!
            val extension = when(contentType) {
                "image/jpeg" -> "jpg"
                "image/png" -> "png"
                else -> throw UnsupportedMediaTypeException("jpeg", "png")
            }
            val name = "${user.id}/${user.id}"
            storage.save(user, "$FOLDER/$name.$extension", avatar)
            "${user.id}/m_${user.id}.png"
        } catch(exception: Error) {
            log.error("Unable to store avatar of user ${user.id}! Using default.", exception)
            DEFAULT_AVATAR
        }

    fun urlFor(path: String) = storage.urlFor("$FOLDER/$path")

    fun load(name: String) = storage.load(name)

    companion object {
        const val FOLDER = "avatars"
        const val DEFAULT_AVATAR = "default.jpg"
        private val log = LoggerFactory.getLogger(AvatarService::class.java)
    }
}