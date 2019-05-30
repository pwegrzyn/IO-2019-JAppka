package pl.edu.agh.io.jappka.activity;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CustomEventManager {

    private final static Logger LOGGER = Logger.getLogger(CustomEventManager.class.getName());
    private String dataDirectoryPath = "data/";
    private String appStateFilePath = dataDirectoryPath + "custom_events.dat";
    private String activityDataDirectoryPath = dataDirectoryPath + "activity/";

    public CustomEventManager() {
        if(!checkIfAppStateFileExist()) {
            try {
                createAppStateFile();
            } catch (IOException e) {
                LOGGER.warning("Error while creating custom events file!");
                return;
            }
            this.persist(Collections.emptyList());
        }
    }

    public void persist(List<AbstractActivityPeriod> periods) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(this.appStateFilePath);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(periods);
            outputStream.close();
            fileOutputStream.close();
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE,"Fatal error occurred while persisting custom events!", e);
        }
    }

    public List<AbstractActivityPeriod> load() {
        List<AbstractActivityPeriod> periods = null;
        try {
            FileInputStream inputStream = new FileInputStream(this.appStateFilePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            periods = (List<AbstractActivityPeriod>) objectInputStream.readObject();
            objectInputStream.close();
            inputStream.close();
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, "Fatal error occurred while recovering custom events!", e);
        }
        return periods;
    }

    private boolean checkIfAppStateFileExist() {
        File file = new File(this.appStateFilePath);
        return file.exists() && !file.isDirectory();
    }

    private void createAppStateFile() throws IOException {
        new File(this.activityDataDirectoryPath).mkdirs();
        File file = new File(this.appStateFilePath);
        file.createNewFile();
    }

}
