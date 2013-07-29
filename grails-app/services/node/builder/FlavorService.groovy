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
