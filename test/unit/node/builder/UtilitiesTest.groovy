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

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.codehaus.groovy.runtime.StackTraceUtils
import org.junit.Before

@TestFor(Manifest)
@TestMixin(ControllerUnitTestMixin)
@Mock([Deployment, Instance])
class UtilitiesTest {
    @Before
    public void setUp() {
        grailsApplication.config.something = "something"
    }

    def testSerialize(){
        def utilities = new Utilities()
        assert utilities.isSerializable("true") == true
        def map = new HashMap()
        map.put("hi", "hello")

        assert utilities.isSerializable(map) == true
        assert utilities.isSerializable(new Manifest(name: "test")) == false
    }

    def testSerializeDomain(){
        def utilities = new Utilities()
        def manifest = new Manifest()
        utilities.grailsApplication = grailsApplication

        assert manifest != null
        manifest.name = "test"
        manifest.save()

        def map = utilities.serializeDomain(manifest)
        assert map.name == "test"
        assert map instanceof Map

        def instance = new Instance(name: "instance")

        def deployment = new Deployment(manifest: manifest, instances: [instance])

        map = utilities.serializeDomain(deployment)

        assert map.manifest.name == "test"
        assert map.instances.first().name == "instance"
    }

    void testStacktraceHack(){
        assert (new Utilities()).wasCalledFromMethodWithName('testStacktraceHack')
        assert (new Utilities()).wasCalledFromClassAndMethodWithName(this.class.name, 'testStacktraceHack')
        assert !(new Utilities()).wasCalledFromMethodWithName('notAMethod')
        assert !(new Utilities()).wasCalledFromClassAndMethodWithName('some.other.ClassName', 'testStacktraceHack')
        assert !(new Utilities()).wasCalledFromClassAndMethodWithName(this.class.name, 'notAMethod')
    }
}
