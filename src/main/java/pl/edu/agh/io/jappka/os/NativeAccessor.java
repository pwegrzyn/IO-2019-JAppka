package pl.edu.agh.io.jappka.os;

import java.util.List;


public interface NativeAccessor {

    String getActiveWindowText();

    String getActiveWindowProcessName();

    List<String> getActiveProcessesNames();

}
