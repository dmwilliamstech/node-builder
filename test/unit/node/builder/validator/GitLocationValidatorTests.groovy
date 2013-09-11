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

package node.builder.validator

import org.junit.Test


class GitLocationValidatorTests {

    @Test
    void shouldDetermineInValidLocation(){
        def validator = new GitLocationValidator()

        assert !validator.isValid("git@github.com:OpenDX/notreal.git")
        assert !validator.isValid("https://notreal.com/OpenDX/notreal.git")
        assert !validator.isValid("https://github.com/OpenDX/notreal.git")
    }

    @Test
    void shouldDetermineValidLocation(){
        def validator = new GitLocationValidator()

        assert validator.isValid("git@github.com:OpenDX/node-builder.git")
        assert validator.isValid("https://github.com/OpenDX/node-builder.git")
        assert validator.isValid("https://kellyp@github.com/OpenDX/node-builder")
    }
}
