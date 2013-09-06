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
