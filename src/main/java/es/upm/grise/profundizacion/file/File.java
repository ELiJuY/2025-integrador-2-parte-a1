// File.java
package es.upm.grise.profundizacion.file;
import es.upm.grise.profundizacion.exceptions.*;

import java.util.ArrayList;
import java.util.List;

public class File {

    private FileType type;
    private List<Character> content;

    /*
     * Constructor
     */
    public File() {
        // content must be empty but not null according to specification
        this.content = new ArrayList<Character>();
    }

    /*
     * Method to code / test
     */
    public void addProperty(char[] newcontent) {
        if (newcontent == null) {
            throw new InvalidContentException("newcontent is null");
        }
        if (this.type == FileType.IMAGE) {
            throw new WrongFileTypeException("Cannot add property to IMAGE file");
        }
        // append newcontent to existing content
        for (char c : newcontent) {
            this.content.add(c);
        }
    }

    /*
     * Method to code / test
     */
    public long getCRC32() {
        if (this.content == null || this.content.isEmpty()) {
            // specification: if content is empty, return 0
            return 0L;
        }
        // transform content (List<Character>) into byte[] using least significant byte
        byte[] bytes = new byte[this.content.size()];
        for (int i = 0; i < this.content.size(); i++) {
            char c = this.content.get(i);
            bytes[i] = (byte) (c & 0x00FF);
        }
        FileUtils fu = new FileUtils();
        return fu.calculateCRC32(bytes);
    }

    /*
     * Setters/getters
     */
    public void setType(FileType type) {

        this.type = type;

    }

    public List<Character> getContent() {

        return content;

    }

}
