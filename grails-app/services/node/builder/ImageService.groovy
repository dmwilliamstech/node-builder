/**
 * Copyright 2013 AirGap, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
