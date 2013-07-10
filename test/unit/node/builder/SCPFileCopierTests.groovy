package node.builder


class SCPFileCopierTests {
    def testLocalCopy(){
        def temp = File.createTempFile("test",".txt")
        temp.write("test")

        def dest = new File(System.getProperty("java.io.tmpdir") + '/test.txt')
        dest.delete()
        assert !dest.exists()
        new SCPFileCopier().copyTo(temp, "localhost", dest, "", "")
        assert  dest.exists()
    }
}
