package node.builder.bpm

import node.builder.Config
import node.builder.Retryable
import node.builder.metrics.MetricEvents
import node.builder.metrics.MetricGroups
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate


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
abstract class MetricsTask extends Retryable implements JavaDelegate  {
    abstract public void executeWithMetrics(DelegateExecution execution)

    @Override
    public void execute(DelegateExecution execution){
        if(Config.globalConfig.get("metrics.initialized")){
            def taskKey = "${this.class.name.toLowerCase()}-${java.util.UUID.randomUUID()}"
            def start = System.currentTimeMillis()
            log.metric(MetricEvents.START, MetricGroups.TASK, "", taskKey, execution.getVariable("businessKey"), execution.getVariable("workflowName"),  execution.getVariable("result"), -1)

            this.executeWithMetrics(execution)

            def message = execution.getVariable("result")?.message ? execution.getVariable("result").message : "Message not set by task"

            def duration = System.currentTimeMillis() - start
            log.metric(MetricEvents.FINISH, MetricGroups.TASK, message, taskKey, execution.getVariable("businessKey"), execution.getVariable("workflowName"), execution.getVariable("result"), duration)


        } else {
            log.warn "Metrics was not initialized successfully, skipping log"
            this.executeWithMetrics(execution)
        }
    }
}
