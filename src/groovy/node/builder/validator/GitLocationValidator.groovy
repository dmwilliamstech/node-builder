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

import node.builder.LocationValidator
import node.builder.WorkflowTypeEnum


class GitLocationValidator extends LocationValidator{
    static String PROJECT_TYPE = WorkflowTypeEnum.GIT_REPOSITORY.name

    @Override
    Boolean isValid(location) {
        location = processLocationString(location.toString())
        def git = "git ls-remote ${location}".execute()
        StringBuilder output = new StringBuilder()
        StringBuilder error = new StringBuilder()
        git.consumeProcessOutput(output, error)
        git.waitForOrKill(5000)

        if(git.exitValue() > 0){
            log.error "Failed to validate GIT url - " + error.toString()
            return false
        }else {
            log.info "GIT Location validation successful - " + output.toString()
            return true
        }
    }

    def processLocationString(String location){
        return location
    }
}
