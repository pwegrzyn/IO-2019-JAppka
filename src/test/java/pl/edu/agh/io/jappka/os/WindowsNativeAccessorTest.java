package pl.edu.agh.io.jappka.os;

import org.apache.commons.exec.OS;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class WindowsNativeAccessorTest {

    @Test
    public void getActiveWindowTextTest() {
        if (!OS.isFamilyWindows()) {
            return;
        }
        NativeAccessor windowsAccessor = new WindowsNativeAccessor();
        assertNotNull(windowsAccessor.getActiveWindowText());
        assertTrue(windowsAccessor.getActiveWindowText().length() > 0);
    }

    @Test
    public void getActiveWindowProcessNameTest() {
        if (!OS.isFamilyWindows()) {
            return;
        }
        NativeAccessor windowsAccessor = new WindowsNativeAccessor();
        assertNotNull(windowsAccessor.getActiveWindowProcessName());
        assertTrue(windowsAccessor.getActiveWindowProcessName().length() > 0);
    }

    @Test
    public void getActiveProcessesNamesTest() {
        if (!OS.isFamilyWindows()) {
            return;
        }
        NativeAccessor windowsAccessor = new WindowsNativeAccessor();
        assertNotNull(windowsAccessor.getActiveProcessesNames());
        assertFalse(windowsAccessor.getActiveProcessesNames().isEmpty());
        String focused = windowsAccessor.getActiveWindowProcessName();
        assertTrue(windowsAccessor.getActiveProcessesNames().contains(focused));
    }

}
