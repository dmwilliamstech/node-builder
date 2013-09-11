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

package node.builder.bpm

class ProcessResult extends HashMap{

    def ProcessResult(){
        init("", null, null)
    }

    def ProcessResult(String message){
        init(message, null, null)
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
        this.message = message ?: ""
        this.data = data ?: [:]
        this.error = error ?: new ProcessResultError(null, null)
        log.info "created ${this.data} ${this.error} ${this.message}"
    }

    public class ProcessResultError extends HashMap{

        def ProcessResultError(message, data){
            this.message = message ?: ""
            this.data = data ?: [:]
        }

    }

    public Boolean wasSuccessfull(){
        return error == null
    }
}
