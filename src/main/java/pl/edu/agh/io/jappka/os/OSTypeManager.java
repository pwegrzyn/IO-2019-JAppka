package pl.edu.agh.io.jappka.os;

import org.apache.commons.exec.OS;


public class OSTypeManager {

    private OsType type;

    public OSTypeManager() {
        discover();
    }

    public OsType getType() {
        return this.type;
    }

    public boolean isUnsupported() {
        return this.type == OsType.UNSUPPORTED;
    }

    private void discover() {
        if(OS.isFamilyWindows())
            this.type = OsType.WINDOWS;
        else
            this.type = OsType.UNSUPPORTED;
    }

    // Add more OS types if other types become supported
    public enum OsType {

        WINDOWS,
        UNSUPPORTED

    }

}
