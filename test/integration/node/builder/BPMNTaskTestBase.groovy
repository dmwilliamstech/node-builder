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

import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor
import org.activiti.engine.delegate.DelegateExecution

/**
 * Created with IntelliJ IDEA.
 * User: kelly
 * Date: 8/23/13
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
class BPMNTaskTestBase {
    def mockDelegateExecutionWithVariables(variables, variablecount, shouldSet = 0){
        def delegateExecution = new StubFor(DelegateExecution.class)


        delegateExecution.demand.getVariable(variablecount){variable -> variables[variable]}
        delegateExecution.demand.setVariable(shouldSet){name, value -> variables[name] = value}


        return delegateExecution.proxyInstance()
    }
}
