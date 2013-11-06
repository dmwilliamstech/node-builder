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

class MonitorFolderJob {
    def projectService

    static triggers = {
        simple(startDelay: (35l * 1000l), // start after 35 seconds
               repeatInterval: (300l * 1000l)) // execute job once in 5 minutes
    }

    def execute() {
        def folders
        try{
            folders = Project.findAllByProjectTypeAndActive(ProjectType.findByName(ProjectTypeEnum.FOLDER_MONITOR.name), true)
        }catch(e){
            log.error("Error retrieving repository data")
            return
        }

        folders.each{ Project folder ->
            projectService.run(folder)
        }
    }
}