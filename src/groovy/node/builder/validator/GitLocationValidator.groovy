package node.builder.validator

import node.builder.LocationValidator


class GitLocationValidator extends LocationValidator{
    static String PROJECT_TYPE = "GIT Repository"

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
        if(!location.matches(/.*\.git$/)){
            location += '.git'
        }
        return location
    }
}
