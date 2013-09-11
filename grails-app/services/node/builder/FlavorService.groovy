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
 * FlavorService
 * A service class encapsulates the core business logic of a Grails application
 */
class FlavorService {

    static transactional = true

    def loadFlavors(connection){

        def flavors = connection.flavors()
        def savedFlavors = Flavor.all
        flavors.each { flavorData ->

            def flavorInstance = Flavor.findByFlavorId(flavorData.id)

            savedFlavors = savedFlavors.findAll({ i ->
                i.flavorId != flavorData.id
            })

            if(flavorInstance){
                flavorInstance.name = flavorData.name
                flavorInstance.flavorId = flavorData.id
            }else{

                flavorInstance = new Flavor(
                        name: flavorData.name,
                        flavorId: flavorData.id,
                        progress: flavorData.progress,
                        minDisk: flavorData.minDisk,
                        minRam: flavorData.minRam,
                        status: flavorData.minRam
                )
            }
            flavorInstance.save()
        }
        Flavor.deleteAll(savedFlavors)
    }
}
