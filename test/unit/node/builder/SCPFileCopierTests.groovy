package node.builder


import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource


class SCPFileCopierTests {

    void testCopyFileWithPassword() {
        Resource resource = new ClassPathResource("resources/empty_application.json")
        def file = resource.getFile()
        assert file.exists()

        def target = new File("/tmp/target.json")
        target.deleteOnExit()

        if(target.exists())
            target.delete()
        assert !target.exists()

        def scpFileCopier = new SCPFileCopier()
        scpFileCopier.copyTo(file, "localhost", target, "username", "password")

        assert target.exists()


    }

    void testCopyFileWithKey() {
        Resource resource = new ClassPathResource("resources/empty_application.json")
        def file = resource.getFile()
        assert file.exists()

        def target = new File("/tmp/target.json")
        target.deleteOnExit()

        if(target.exists())
            target.delete()
        assert !target.exists()

        def scpFileCopier = new SCPFileCopier()
        scpFileCopier.copyTo(file, "localhost", target, "username", new File("~/.ssh/id_rsa"))

        assert target.exists()


    }

}
