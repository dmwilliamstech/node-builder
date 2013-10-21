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

import com.mongodb.util.JSON
import groovy.text.SimpleTemplateEngine
import org.activiti.engine.delegate.DelegateExecution
import org.apache.commons.io.IOUtils
import org.xhtmlrenderer.pdf.ITextRenderer

class DownloadSonarIssuesTask extends MetricsTask{
    private static final SONAR_ISSUES_URL_VAR = 'sonarIssuesUrl'

    void executeWithMetrics(DelegateExecution delegateExecution) throws Exception {
        def result = delegateExecution.getVariable("result") == null ? new ProcessResult() : delegateExecution.getVariable("result")

        def remoteUrl = delegateExecution.getVariable(SONAR_ISSUES_URL_VAR).toString()
        if(remoteUrl == null){
            throw new NullPointerException("Sonar Issues Url missing from variables object")
        }

        remoteUrl = remoteUrl.replaceAll(/\&?pageSize\=[\-\d]+/,'') + '&pageSize=-1'
        remoteUrl = new URL(remoteUrl + "&format=json").openConnection()

        def issuesFile = File.createTempFile("issues", ".json")
        issuesFile.withOutputStream { out ->
            out << remoteUrl.getInputStream()
        }

        result.data.sonarIssuesFile = issuesFile.path
        result.data.sonarIssues = JSON.parse(issuesFile.text)
        if(delegateExecution.getVariable("sonarPrettyPrintIssues")){
            def prettyPrintFile = File.createTempFile("issues", ".html")
            def template = this.class.classLoader.getResourceAsStream("/templates/sonar_issues.html.gsp")
            if(template == null){
                template = this.class.classLoader.getResourceAsStream("resources/sonar_issues.html.gsp")
            }
            prettyPrintFile.write(processTemplate((List)result.data.sonarIssues.issues, template))

            def prettyPrintPdfFile = File.createTempFile("issues", ".pdf")
            processPdf(prettyPrintFile, prettyPrintPdfFile)

            result.data.sonarIssuesPrettyPrintFile = prettyPrintFile.path
            result.data.sonarIssuesPrettyPrintPdfFile = prettyPrintPdfFile.path
        }

        delegateExecution.setVariable(SONAR_ISSUES_URL_VAR, remoteUrl.URL.toString())
        delegateExecution.setVariable("result", result)
    }

    def processTemplate(issues, template){
        def output = new StringWriter()
        def templateText = IOUtils.toString(template, "UTF-8")
        (new SimpleTemplateEngine()).createTemplate(templateText).make([issues: issues]).writeTo(output)
        return output.getBuffer().toString().replaceAll("\\-\\>\\s+\\}", "\n}")
    }

    def processPdf(inputFile, outputFile) {
        ITextRenderer iTextRenderer = new ITextRenderer();

        iTextRenderer.setDocument(inputFile.path);
        iTextRenderer.layout();

        final FileOutputStream fileOutputStream =
            new FileOutputStream(new File(outputFile.path));

        iTextRenderer.createPDF(fileOutputStream);
        fileOutputStream.close();
    }
}
