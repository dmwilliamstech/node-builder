package node.builder

/**
 * ImageService
 * A service class encapsulates the core business logic of a Grails application
 */
class ImageService {

    static transactional = true


    def loadImages(connection){

        def images = connection.images()
        def savedImages = Image.all
        images.each { imageData ->

            def imageInstance = Image.findByImageId(imageData.id)

            savedImages = savedImages.findAll({ i ->
                i.imageId != imageData.id
            })

            if(imageInstance){
                imageInstance.name = imageData.name
                imageInstance.imageId = imageData.id
                imageInstance.progress = imageData.progress
                imageInstance.minDisk = imageData.minDisk
                imageInstance.minRam = imageData.minRam
                imageInstance.status = imageData.minRam
            }else{

                imageInstance = new Image(
                        name: imageData.name,
                        imageId: imageData.id,
                        progress: imageData.progress,
                        minDisk: imageData.minDisk,
                        minRam: imageData.minRam,
                        status: imageData.minRam
                )
            }
            imageInstance.save()
        }
        savedImages.each{ savedImage ->
            savedImage.instances.each{ instance ->
                instance.delete()
            }
            savedImage.delete()
        }

    }
}
