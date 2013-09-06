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
