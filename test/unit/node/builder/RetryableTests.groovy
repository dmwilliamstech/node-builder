package node.builder

import org.junit.Test


class RetryableTests extends Retryable{
    final shouldFail = new GroovyTestCase().&shouldFail

    @Test
    void shouldRetryOnce(){
        def count = 0
        def handler = { exception ->
            assert exception instanceof Exception
            count++
        }

        shouldFail(Exception){
            retry(handler, Exception, 1) {
                throw new Exception("Retry once only")
            }
        }
        assert count == 1
    }

    @Test
    void shouldOnlyCatchExpectedException(){
        def count = 0
        def handler = { exception ->
            assert exception instanceof Exception
            count++
        }

        shouldFail(Exception){
            retry(handler, IllegalArgumentException, 1) {
                throw new Exception("Should not retry")
            }
        }
        assert count == 0
    }

    @Test
    void shouldReturnValueReturnedByClosure(){
        def handler = { exception ->

        }

        def value = retry(handler, IllegalArgumentException, 1) {
                return 99
        }

        assert value == 99
    }
}
