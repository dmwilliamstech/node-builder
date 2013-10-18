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
/**
 * DependencyReportController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class DependencyReportController {
    static scaffold = false

    def index() {
        def text = this.class.classLoader.getResourceAsStream("dependency-report.xml").text
        render(text:text,contentType:"text/xml",encoding:"UTF-8")
    }
}
