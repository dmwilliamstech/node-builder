package node.builder


class Config {
    static ConfigObject config

    static ConfigObject getGlobalConfig(){
        if(config == null){
            def configFile = new File("${System.getenv("HOME")}/.opendx/config")
            if(!configFile.exists())
                configFile = new File("/etc/node-builder.conf")

            config = new ConfigSlurper().parse(new URL("file://${configFile.absolutePath}")).flatten()
        }
        return config
    }
}
