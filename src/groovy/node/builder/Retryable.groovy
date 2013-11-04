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


abstract class Retryable {
    def retry(Class clazz = Exception, Integer retries = 1, c) {
        retry({}, clazz, retries, c)
    }

    def retry(handler, Class clazz = Exception, Integer retries = 1, c) {
        retry(handler, clazz, retries, 500l, c)
    }

    def retry(handler, Class clazz = Exception, Integer retries = 1, Long timeout, c) {
        try {
            return c()
        } catch(e) {
            retries -= 1
            if(e.class == clazz && retries >= 0){
                handler(e)
                sleep(timeout)
                retry(handler, clazz, retries, timeout, c)
            }else{
                throw e
            }
        }
    }
}
