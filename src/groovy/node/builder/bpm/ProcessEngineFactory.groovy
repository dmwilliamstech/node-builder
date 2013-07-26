package node.builder.bpm

import org.activiti.engine.ProcessEngine
import org.activiti.engine.ProcessEngineConfiguration


class ProcessEngineFactory {

    static ProcessEngine processEngine

    private ProcessEngineFactory(){
    }

    public static ProcessEngine defaultProcessEngine(){
        if(processEngine == null)
            processEngine = ProcessEngineConfiguration
                    .createStandaloneInMemProcessEngineConfiguration()
                    .buildProcessEngine();
        return processEngine
    }

}
