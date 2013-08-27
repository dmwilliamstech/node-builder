package node.builder

import groovy.mock.interceptor.MockFor
import org.activiti.engine.delegate.DelegateExecution

/**
 * Created with IntelliJ IDEA.
 * User: kelly
 * Date: 8/23/13
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
class BPMNTaskTestBase {
    def mockDelegateExecutionWithVariables(variables, variablecount, shouldSet){
        def delegateExecution = new MockFor(DelegateExecution.class)

        (0..variablecount).each {
            delegateExecution.demand.getVariable(){variable -> variables[variable]}
        }


        (0..shouldSet).each {
            delegateExecution.demand.setVariable(){name, value -> variables[name] = value}
        }

        return delegateExecution.proxyInstance()
    }
}
