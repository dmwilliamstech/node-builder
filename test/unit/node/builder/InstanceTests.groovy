package node.builder

import grails.converters.JSON
import grails.test.mixin.*
import grails.test.mixin.web.ControllerUnitTestMixin
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Instance)
@Mock([Manifest, Image])
@TestMixin(ControllerUnitTestMixin)
class InstanceTests {

    void testHasManifest() {
        def image = new Image(name: "Image Name", imageId: "Image Id", progress: 100, minDisk: "1", minRam: "2", status: "Good")
        image.save()
        assert !image.hasErrors()

        def manifest = new Manifest()
        manifest.manifest = [manifest: [fun: "oh yeah!"]]
        manifest.save()
        assert !manifest.hasErrors()

        def instance = new Instance(
               name:"Some Instance",
               status: "Awesome",
               hostId: "A Host",
               privateIP: "numbers",
               keyName: "A Key",
               flavorId: "1",
               instanceId: "This Instance",
               userId: "Blah",
               tenantId: "5",
               progress: 100,
               configDrive: "What?",
               metadata: ([blah: "blah"] as JSON).toString(true),
               image: image
        )
        instance.save()
        assert !instance.hasErrors()

        instance.manifest = manifest
        instance.save()
        assert !instance.hasErrors()

        assert Instance.first().manifest.manifest == manifest.manifest

        manifest.addToInstances(instance)
        manifest.save()

        assert manifest.instances.first().name == instance.name
    }
}
