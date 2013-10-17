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

package node.builder.domain

import grails.converters.JSON
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import node.builder.Manifest

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Manifest)
@TestMixin(ControllerUnitTestMixin)
class ManifestTests {

    void testStringifying() {
        def manifest = new Manifest('{"name":"bob"}')
        manifest.name = "Test"
        manifest.save()

        assert Manifest.first().manifest.name == "bob"
    }

    void testParsing() {
        def json = JSON.parse('{"name":"bob"}')

        def manifest = new Manifest()
        manifest.setManifest(json)
        manifest.name = "Test2"
        manifest.save()

        assert Manifest.count == 1

        assert Manifest.first().manifest.name == "bob"
    }
}
