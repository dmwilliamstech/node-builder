package node.builder.bpm

import grails.converters.JSON
import groovy.text.GStringTemplateEngine
import groovy.text.SimpleTemplateEngine
import node.builder.SCPFileCopier
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.io.support.GrailsResourceUtils
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine


class DeployTask implements JavaDelegate{

    public void execute(DelegateExecution delegateExecution) throws Exception {
        def master = delegateExecution.getVariable("master")
        def manifest = delegateExecution.getVariable("manifest")
        def scpFileCopier = new SCPFileCopier()
        def key = new File(master.privateKey.replaceAll("\\~",System.getenv()["HOME"]))
        for(instance in JSON.parse(manifest.manifestAsJSON).instances){
            def node = new File(instance.name.toString().replaceAll(/\s/, '-') + '.pp')
            node.write(processTemplate(instance, this.class.classLoader.getResourceAsStream("/templates/node.pp.gsp")))
            scpFileCopier.copyTo(node, master.hostname, new File(master.remotePath), master.username, key)
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
