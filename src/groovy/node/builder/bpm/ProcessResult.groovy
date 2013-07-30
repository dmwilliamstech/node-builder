package node.builder.bpm

class ProcessResult extends HashMap{

    def ProcessResult(){
    }

    def ProcessResult(String message){
        init(message)
    }

    def ProcessResult(String message, ProcessResultError error){
        init(message, null, error)
    }

    def ProcessResult(String message, data, ProcessResultError error){
        init(message, data, error)
    }

    def ProcessResult(String message, data){
        init(message, data, null)
    }

    private def init(message, data, error){
        this.message = message
        this.data = data
        this.error = error
    }

    public class ProcessResultError extends HashMap{

        def ProcessResultError(message, data){
            this.message = message
            this.data = data
        }

    }




    public Boolean wasSuccessfull(){
        return error == null
    }
}
