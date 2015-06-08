package com.lge.lgreplaymaker;

import java.io.*;
import java.util.*;

public class LogReader
{
    String [] logTypes = {SYSTEM_LOG, KERNEL_LOG, MAIN_LOG, EVENT_LOG};

    LogExtractor logExtractor = null;
    
    static final String LOG_FILE_PATH = "./log/";
    static final String KERNEL_LOG = LOG_FILE_PATH + "kernel.log";
    static final String SYSTEM_LOG = LOG_FILE_PATH +"system.log";
    static final String MAIN_LOG = LOG_FILE_PATH +"main.log";
    static final String EVENT_LOG = LOG_FILE_PATH +"events.log";        

    public LogReader(LogExtractor logExtractor) {
        this.logExtractor = logExtractor;
    }

    public LogReader() {
    }

    public void read() { 
        System.out.println("Reading & Extracting..." );
        for (String  logType: logTypes) {
            findAndReadLog(logType);
        }
    }

    private void findAndReadLog(String logType) {
        File file = null;
        int numberOfLogs = 0;
        String tailOfLog = "";

        do {
            try {
                file = null;
                String path = logType + tailOfLog;                
                file = getFile(path);
                if (file != null) {
                    System.out.println(path);
                    readFile(file);
                    numberOfLogs++;
                    tailOfLog = "." + String.valueOf(numberOfLogs);
                }
            } catch (Exception e) {
                System.out.println("e:" + e);
            }            
        } while (file != null);
    }    

    private File getFile(String path) {
        File mFile = new File(path);

        if (mFile != null && mFile.exists()) {
            return mFile;
        }
        return null;
    }

    public void readFile(File f) {
        FileReader in = null; 

        try {
            BufferedReader br =  new BufferedReader(in = new FileReader(f));
            String line;
            
            while ((line = br.readLine()) != null) {
                logExtractor.extract(line.trim());
            }
            in.close();
        } catch( IOException e){}
    }   
}


