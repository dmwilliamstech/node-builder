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

import org.apache.commons.io.FileUtils
import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.optional.ssh.Scp

class SCPFileCopier {

    def copyTo(File src, String host, File dest,String user, String password){
        if(host =~ /localhost/ || host =~ /127\.0\.0\.1/){
            FileUtils.copyFile(src, dest)
        }else{
            def remote = "${user}@${host}:${dest.path}"
            log.info "Copying file ${src} to ${remote}"
            Scp scp = new Scp();

            scp.setLocalFile(src.path)
            scp.setRemoteTofile(remote)

            scp.setPassword(password)

            scp.setProject(new Project()); // prevent a NPE (Ant works with projects)
            scp.setTrust(true); // workaround for not supplying known hosts file

            scp.execute();
        }
    }

    def copyTo(File src, String host, File dest,String user, File key){
        if(host =~ /localhost/ || host =~ /127\.0\.0\.1/){
            FileUtils.copyFile(src, dest)
        }else{
            def remote = "${user}@${host}:${dest.path}"
            log.info "Copying file ${src} to ${remote}"
            Scp scp = new Scp();

            scp.setLocalFile(src.path)
            scp.setRemoteTofile(remote)

            scp.setKeyfile(key.path.replaceAll("\\~", System.getenv()["HOME"]))

            scp.setProject(new Project()); // prevent a NPE (Ant works with projects)
            scp.setTrust(true); // workaround for not supplying known hosts file

            scp.execute();
        }
    }

    def createKeyFile(String keyName, String keyContents){

        def fileName = System.getenv()["HOME"] + "/.opendx/" + keyName
        File file = new File(fileName)
        if(!file.exists())
            file.write(keyContents)
        file.setReadable(true)
        return file

    }
}
