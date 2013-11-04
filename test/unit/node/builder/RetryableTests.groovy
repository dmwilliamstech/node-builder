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

import org.activiti.engine.ActivitiOptimisticLockingException
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
            retry(handler, Exception, 5) {
                throw new Exception("Retry 5 times")
            }
        }
        assert count == 5
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

    @Test
    void shouldRunHandlerClosureAndProcessLocalVariables(){
        def count = 0
        def string = "somestring"
        def handler = { exception ->
            string = string.replaceAll(/\-\d$/, '') + "-${++count}"
            assert exception instanceof ActivitiOptimisticLockingException
        }

        shouldFail(ActivitiOptimisticLockingException){
            retry(handler, ActivitiOptimisticLockingException, 5) {
                throw new ActivitiOptimisticLockingException("Let's retry this")
            }
        }
        assert count == 5
        assert string == "somestring-5"
    }
}
