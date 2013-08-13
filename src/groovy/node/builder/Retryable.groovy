package node.builder


abstract class Retryable {
    def retry(handler, clazz = Exception, retries = 1, c) {
        try {
            return c()
        } catch(e) {
            retries -= 1
            if(e.class == clazz && retries >= 0){
                handler(e)
                retry(handler, retries, c)
            }else{
                throw e
            }
        }
    }
}
