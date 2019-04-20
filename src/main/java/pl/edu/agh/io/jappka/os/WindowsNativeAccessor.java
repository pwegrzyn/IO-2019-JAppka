package pl.edu.agh.io.jappka.os;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import java.util.LinkedList;
import java.util.List;


public class WindowsNativeAccessor implements NativeAccessor {

    private static final int MAX_TITLE_LENGTH = 1024;

    private static long time(FILETIME ft) {
        long t = (((long)ft.dwHighDateTime) << 32) + ft.dwLowDateTime;
        return t;
    }

    private interface Psapi extends StdCallLibrary {
        Psapi INSTANCE = (Psapi) Native.load("Psapi", Psapi.class);
        boolean EnumProcesses(int[] ProcessIDsOut, int size, WinDef.DWORD BytesReturned);
        WinDef.DWORD GetModuleBaseNameW(Pointer hProcess, Pointer hModule, char[] lpBaseName, int nSize);
    }

    private interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = (Kernel32) Native.load("Kernel32", Kernel32.class);
        int PROCESS_QUERY_INFORMATION = 0x0400;
        int PROCESS_VM_READ = 0x0010;
        int GetLastError();
        Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
        Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);
        boolean GetProcessTimes(int processHdl, FILETIME creation, FILETIME exit, FILETIME kernel, FILETIME user);
        int GetCurrentProcess();
        boolean CloseHandle(Pointer hObject);
    }

    private interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.load("User32", User32.class);
        int GetWindowThreadProcessId(WinDef.HWND hWnd, PointerByReference pref);
        WinDef.HWND GetForegroundWindow();
        int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);
    }

    @Override
    public String getActiveWindowText() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowTextW(hwnd, buffer, MAX_TITLE_LENGTH);
        return Native.toString(buffer);
    }

    @Override
    public String getActiveWindowProcessName() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        PointerByReference pointer = new PointerByReference();
        User32.INSTANCE.GetWindowThreadProcessId(User32.INSTANCE.GetForegroundWindow(), pointer);
        Pointer process = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION |
                Kernel32.PROCESS_VM_READ, false, pointer.getValue());
        Psapi.INSTANCE.GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
        Kernel32.INSTANCE.CloseHandle(process);
        return Native.toString(buffer);
    }

    @Override
    public List<String> getActiveProcessesNames() {
        int[] processlist = new int[1024];
        WinDef.DWORD bytesReturned = new WinDef.DWORD();
        Psapi.INSTANCE.EnumProcesses(processlist, processlist.length, bytesReturned);
        List<String> processesNames = new LinkedList<>();
        for (int pid : processlist) {
            Pointer ph = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION |
                    Kernel32.PROCESS_VM_READ, false, pid);
            if (ph != null) {
                char[] filename = new char[MAX_TITLE_LENGTH * 2];
                Psapi.INSTANCE.GetModuleBaseNameW(ph, null, filename, MAX_TITLE_LENGTH);
                processesNames.add(Native.toString(filename));
                Kernel32.INSTANCE.CloseHandle(ph);
            }

        }
        return processesNames;
    }
}
