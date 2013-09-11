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

import node.builder.validator.GitLocationValidator
import org.apache.commons.logging.LogFactory


abstract class LocationValidator {

    static def validators =[:]

    private static def getValidatorForProjectType(String type){
        if(validators.isEmpty()){
            loadValidators()
        }

        return validators[type]
    }

    private static def loadValidators(){
        validators.put(GitLocationValidator.PROJECT_TYPE, new GitLocationValidator())
    }


    static def validateLocationForProjectType(location, String type){
        LocationValidator validator = getValidatorForProjectType(type)
        if(validator == null){
            LogFactory.getLog(this)
                    .error "Validator for ${type} is not installed"
            return false
        }
        return validator.isValid(location)
    }

    abstract Boolean isValid(location)
}
