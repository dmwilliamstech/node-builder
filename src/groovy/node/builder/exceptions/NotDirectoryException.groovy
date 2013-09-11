package node.builder.exceptions


class NotDirectoryException extends IOException{

    def NotDirectoryException(String file){
        super("File ${file} is not a directory")
    }

}
