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

import com.offbytwo.jenkins.JenkinsServer
import groovy.mock.interceptor.MockFor
import node.builder.bpm.CreateJenkinsJobTask
import node.builder.bpm.DeleteJenkinsJobTask
import org.activiti.engine.delegate.DelegateExecution
import org.junit.After
import org.junit.Ignore
import org.junit.Test


class CreateJenkinsJobTaskTests extends BPMNTaskTestBase{
    final shouldFail = new GroovyTestCase().&shouldFail

    void testSomething(){
        assert true
    }


    @Ignore
    @Test
    void connectToJenkins(){
        def delegateExecution = mockDelegateExecutionWithVariables([jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99"], 5, 0)
        def createJenkinsJobTask = new CreateJenkinsJobTask()
        shouldFail(Exception){
            createJenkinsJobTask.execute(delegateExecution)
        }
    }


    @Ignore
    @Test
    void createJobInJenkins(){
        def name = "test-job-" + UUID.randomUUID().toString()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:name, jenkinsJobXml:getJobXml()]
        def delegateExecution = mockDelegateExecutionWithVariables(variables , 6, 1)
        def createJenkinsJobTask = new CreateJenkinsJobTask()
        createJenkinsJobTask.execute(delegateExecution)
        assert variables.result.data.jenkinsJob.displayName == name
    }

    @Ignore
	@Test
    void verifyMasterComputer(){
        JenkinsServer jenkins = new JenkinsServer(new URI("http://stackbox:9999/"),
                "admin", "foobar99")
        def computers = jenkins.computers
        assert computers.size() > 0
        assert computers.containsKey("master")
    }

    @Ignore
	@Test
    void verifyMasterLabel(){
        JenkinsServer jenkins = new JenkinsServer(new URI("http://stackbox:9999/"),
                "admin", "foobar99")
        def label = jenkins.getLabel("master")
        assert label.nodes.size() == 1
        assert label.name == "master"
    }

    @After
    void tearDown(){
        def jenkinsTask = new DeleteJenkinsJobTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"test\\-job\\-.*"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 1)
        jenkinsTask.execute(delegateExecution)
    }

    def getJobXml(){
        return """\
<?xml version='1.0' encoding='UTF-8'?>
<project>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders/>
  <publishers/>
  <buildWrappers/>
</project>
"""
    }
}
