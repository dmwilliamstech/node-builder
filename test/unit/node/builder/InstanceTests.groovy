/*
{
    "server": {
        "status": "BUILD",
        "updated": "2013-06-18T00:30:55Z",
        "hostId": "",
        "addresses": {},
        "links": [
            {
                "href": "http://107.2.16.122:8774/v2/2ba2d60c5e8d4d1b86549d988131fe48/servers/6a2c3cb5-046a-4f9e-b8ac-a1050f05cd74",
                "rel": "self"
            },
            {
                "href": "http://107.2.16.122:8774/2ba2d60c5e8d4d1b86549d988131fe48/servers/6a2c3cb5-046a-4f9e-b8ac-a1050f05cd74",
                "rel": "bookmark"
            }
        ],
        "key_name": "opendx_demo",
        "image": {
            "id": "3012812c-c178-408b-9b54-b292525834c3",
            "links": [
                {
                    "href": "http://107.2.16.122:8774/2ba2d60c5e8d4d1b86549d988131fe48/images/3012812c-c178-408b-9b54-b292525834c3",
                    "rel": "bookmark"
                }
            ]
        },
        "OS-EXT-STS:task_state": "scheduling",
        "OS-EXT-STS:vm_state": "building",
        "flavor": {
            "id": "1",
            "links": [
                {
                    "href": "http://107.2.16.122:8774/2ba2d60c5e8d4d1b86549d988131fe48/flavors/1",
                    "rel": "bookmark"
                }
            ]
        },
        "id": "6a2c3cb5-046a-4f9e-b8ac-a1050f05cd74",
        "security_groups": [
            {
                "name": "default"
            }
        ],
        "user_id": "ce51d53b37264954aa3473c72c0bf8b7",
        "name": "instance",
        "created": "2013-06-18T00:30:55Z",
        "tenant_id": "2ba2d60c5e8d4d1b86549d988131fe48",
        "OS-DCF:diskConfig": "MANUAL",
        "accessIPv4": "",
        "accessIPv6": "",
        "progress": 0,
        "OS-EXT-STS:power_state": 0,
        "config_drive": "",
        "metadata": {}
    }
}
*/


package node.builder

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Instance)
@Mock([Manifest, Image])
@TestMixin(ControllerUnitTestMixin)
class InstanceTests {

    @Test
    void shouldReturnInstancesFromList(){
        (1..5).each {
            def instance = new Instance(
                    name: "$it",
                    instanceId: "$it",
                    hostId: "$it",
                    status: "good",
                    tenantId: "$it",
                    userId: "$it"
            )
            instance.save()
            assert instance.errors.errorCount == 0
        }

        assert Instance.count == 5

        def criteria = Instance.createCriteria()
        def instances = criteria.list {
            'in'("name",["1","2","3","4","5"])
        }
        assert instances.size() == 5

        instances = criteria.list {
            'in'("name",["2","3","4","5"])
        }
        assert instances.size() == 4

        instances = criteria.list {
            'in'("name",["2","5"])
        }
        assert instances.size() == 2
    }

}
