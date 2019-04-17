package pl.edu.agh.io.jappka.os;

import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class WindowsNativeAccessorTest {

    @Test
    public void getActiveWindowTextTest() {
        NativeAccessor windowsAccessor = new WindowsNativeAccessor();
        assertNotNull(windowsAccessor.getActiveWindowText());
        assertTrue(windowsAccessor.getActiveWindowText().length() > 0);
    }

    @Test
    public void getActiveWindowProcessNameTest() {
        NativeAccessor windowsAccessor = new WindowsNativeAccessor();
        assertNotNull(windowsAccessor.getActiveWindowProcessName());
        assertTrue(windowsAccessor.getActiveWindowProcessName().length() > 0);
    }

    @Test
    public void getActiveProcessesNamesTest() {
        NativeAccessor windowsAccessor = new WindowsNativeAccessor();
        assertNotNull(windowsAccessor.getActiveProcessesNames());
        assertFalse(windowsAccessor.getActiveProcessesNames().isEmpty());
        String focused = windowsAccessor.getActiveWindowProcessName();
        assertTrue(windowsAccessor.getActiveProcessesNames().contains(focused));
    }

}