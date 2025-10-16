public class FileData {

    private final String fileName;
    private final String encodedFile;

    public FileData(String fileName, String encodedFile) {
        this.fileName = fileName;
        this.encodedFile = encodedFile;
    }

    public String getFileName() {
        return fileName;
    }

    public String getEncodedFile() {
        return encodedFile;
    }
}
