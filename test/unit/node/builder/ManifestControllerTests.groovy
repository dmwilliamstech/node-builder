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

/**
 * ManifestControllerTests
 * A unit test class is used to test individual methods or blocks of code without considering the surrounding infrastructure
 */
@TestFor(ManifestController)
@Mock(Manifest)
class ManifestControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/manifest/list" == response.redirectedUrl
    }

    void testList() {

        controller.list()


    }

    void testCreate() {
        controller.create()


    }

    void testShow() {
//        controller.show()
//
//        assert flash.message != null
//        assert response.redirectedUrl == '/manifest/list'
//
//
//        populateValidParams(params)
//        def manifest = new Manifest(params)
//
//        assert manifest.save() != null
//
//        params.id = manifest.id
//
//        def model = controller.show()
//
//        assert model.manifestInstance == manifest
    }


    void testUpdate() {
//        controller.update()
//
//        assert flash.message != null
//        assert response.redirectedUrl == '/manifest/list'
//
//        response.reset()
//
//
//        populateValidParams(params)
//        def manifest = new Manifest(params)
//
//        assert manifest.save() != null
//
//        // test invalid parameters in update
//        params.id = manifest.id
//        //TODO: add invalid values to params object
//
//        controller.update()
//
//        assert view == "/manifest/edit"
//        assert model.manifestInstance != null
//
//        manifest.clearErrors()
//
//        populateValidParams(params)
//        controller.update()
//
//        assert response.redirectedUrl == "/manifest/show/$manifest.id"
//        assert flash.message != null
//
//        //test outdated version number
//        response.reset()
//        manifest.clearErrors()
//
//        populateValidParams(params)
//        params.id = manifest.id
//        params.version = -1
//        controller.update()
//
//        assert view == "/manifest/edit"
//        assert model.manifestInstance != null
//        assert model.manifestInstance.errors.getFieldError('version')
//        assert flash.message != null
    }

    void testDelete() {
//        controller.delete()
//        assert flash.message != null
//        assert response.redirectedUrl == '/manifest/list'
//
//        response.reset()
//
//        populateValidParams(params)
//        def manifest = new Manifest(params)
//
//        assert manifest.save() != null
//        assert Manifest.count() == 1
//
//        params.id = manifest.id
//
//        controller.delete()
//
//        assert Manifest.count() == 0
//        assert Manifest.get(manifest.id) == null
//        assert response.redirectedUrl == '/manifest/list'
    }
}
