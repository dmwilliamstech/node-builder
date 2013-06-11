package node.builder

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;


class SCPFileCopier {

    def copyTo(File src, String host, File dest,String user, String password){
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

    def copyTo(File src, String host, File dest,String user, File key){
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

    def createKeyFile(String keyName, String keyContents){

        def fileName = System.getenv()["HOME"] + "/.opendx/" + keyName
        File file = new File(fileName)
        if(!file.exists())
            file.write(keyContents)
        file.setReadable(true)
        return file

    }
}
