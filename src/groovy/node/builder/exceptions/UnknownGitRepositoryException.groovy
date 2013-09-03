package node.builder.exceptions


class UnknownGitRepositoryException extends Exception{
    def UnknownGitRepositoryException(String message, Exception exception){
        super(message, exception)
    }
}
