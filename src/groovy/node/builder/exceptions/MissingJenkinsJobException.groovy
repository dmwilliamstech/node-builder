package node.builder.exceptions


class MissingJenkinsJobException extends Exception{
    def MissingJenkinsJobException(message, exception){
        super(message, exception)
    }
}
