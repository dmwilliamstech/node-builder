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

import grails.converters.JSON
import groovy.text.SimpleTemplateEngine
import node.builder.SCPFileCopier
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate
import org.apache.commons.io.IOUtils

class DeployTask implements JavaDelegate{

    public void execute(DelegateExecution delegateExecution) throws Exception {
        def master = delegateExecution.getVariable("master")
        def manifest = delegateExecution.getVariable("manifest")
        def scpFileCopier = new SCPFileCopier()
        def key = new File(master.privateKey.replaceAll("\\~",System.getenv()["HOME"]))
        for(instance in JSON.parse(manifest.manifestAsJSON).instances){
            def nodeName = instance.name.toString().replaceAll(/\s/, '-')
            def node = new File( System.getProperty("java.io.tmpdir") + "/" + nodeName + '.pp')
            def template = this.class.classLoader.getResourceAsStream("/templates/node.pp.gsp")
            if(template == null){
                template = this.class.classLoader.getResourceAsStream("resources/node.pp.gsp")
            }
            node.write(processTemplate(instance, template))
            scpFileCopier.copyTo(node, master.hostname, new File(master.remotePath + '/' + nodeName + '.pp'), master.username, key)
        }
    }

    def processTemplate(instance, template){
        def output = new StringWriter()
        def templateText = IOUtils.toString(template, "UTF-8")
        (new SimpleTemplateEngine()).createTemplate(templateText).make([manifest: instance, service: this]).writeTo(output)
        return output.getBuffer().toString().replaceAll("\\-\\>\\s+\\}", "\n}")
    }

    def processConfigurationValue(value){
        if(value.class == java.lang.String){
            return "\"${value}\""
        }else{
            String string = "[ "
            for(config in value){
                string += "\"" + config + "\", "
            }
            string = string.replaceAll(/\,\s$/, "")
            string += " ]"
            return string
        }
    }
}
