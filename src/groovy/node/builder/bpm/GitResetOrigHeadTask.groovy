package node.builder.bpm

import org.activiti.engine.delegate.DelegateExecution
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand

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
class GitResetOrigHeadTask extends MetricsTask{
    static final String LOCAL_PATH = 'gitResetOriginHeadLocalPath'
    static final String ORIG_HEAD = 'ORIG_HEAD'

    @Override
    void executeWithMetrics(DelegateExecution execution) {
        def result = execution.getVariable(ProcessResult.RESULT_KEY)?: new ProcessResult()
        def localPath = execution.getVariable(LOCAL_PATH)
        def workingCopy = new File(localPath)
        def git = Git.open(workingCopy)
        git.reset()
                .setMode(ResetCommand.ResetType.HARD)
                .setRef(ORIG_HEAD)
                .call()

        result.message = "Working copy reset to ORIG_HEAD (${git.log().call().first().id.name})"
        execution.setVariable(ProcessResult.RESULT_KEY, result)
    }
}
