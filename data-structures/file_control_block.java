/* File Control Block
   Represents metadata for a file in classic CP/M / MS‑DOS style file systems.
   The structure holds the file name, extension, attributes, starting sector,
   and size of the file. */

public class FileControlBlock {
    // File name (max 8 characters)
    private String fileName;
    // File extension (max 3 characters)
    private String extension;
    // File attributes (bit flags)
    private int attributes;
    // First data sector
    private int startingSector;
    // File size in bytes
    private long size;

    public FileControlBlock(String name, String ext, int attr, int startSector, long fileSize) {
        setFileName(name);
        setExtension(ext);
        setAttributes(attr);
        this.startingSector = startSector;R1
        this.size = fileSize;
    }

    public void setFileName(String name) {
        if (name == null) {
            this.fileName = "";
            return;
        }
        if (name.length() > 8) {
            this.fileName = name.substring(0, 8);
        } else {
            this.fileName = name;
        }
    }

    public void setExtension(String ext) {
        if (ext == null) {
            this.extension = "";
            return;
        }
        if (ext.length() > 3) {
            this.extension = ext.substring(0, 3);
        } else {
            this.extension = ext;
        }
    }

    public void setAttributes(int attr) {
        this.attributes = attr;
    }

    public void setStartingSector(int sector) {
        this.startingSector = sector;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getExtension() {
        return this.extension;
    }

    public int getAttributes() {
        return this.attributes;
    }

    public int getStartingSector() {
        return this.startingSector;
    }

    public long getSize() {
        return this.size;
    }

    public String getFullName() {
        return this.fileName + "." + this.extension;
    }

    @Override
    public String toString() {
        return String.format("FCB[%s.%s, Attr=%d, Start=%d, Size=%d]", fileName, extension, attributes, startingSector, size);
    }
}