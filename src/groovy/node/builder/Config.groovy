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

import org.apache.commons.logging.LogFactory
import org.springframework.core.io.ClassPathResource


class Config {
    private static ConfigObject config

    synchronized static ConfigObject getGlobalConfig(){
        if(config == null){
            LogFactory.getLog(this).info("Loading config file")
            def locations = ["${System.getenv("HOME")}/.opendx/node-builder.conf", "/etc/node-builder.conf"]
            def configFile
            locations.each{path ->
                LogFactory.getLog(this).info("Looking for config file at $path")
                configFile = new File(path)
                if(configFile.exists()){
                    LogFactory.getLog(this).info("Found config file at $path")
                    return
                }
            }

            if(!configFile.exists()){
                LogFactory.getLog(this).info("Config file not found, using default")
                LogFactory.getLog(this).info("Please update config values for your environment")
                def homeDir = new File("${System.getenv("HOME")}/.opendx")
                if(!homeDir.exists()){
                    homeDir.mkdirs()
                    LogFactory.getLog(this).info("Created ${System.getenv("HOME")}/.opendx default directory")
                }
                def conf= new ClassPathResource("resources/node-builder.conf").getFile().text
                configFile = new File("${System.getenv("HOME")}/.opendx/node-builder.conf")
                configFile.write(conf)
            }

            config = new ConfigSlurper().parse(new URL("file://${configFile.absolutePath}")).flatten()
            LogFactory.getLog(this).info("Completed loading config file")
        }
        return config
    }
}
