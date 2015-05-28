package com.lge.lgreplaymaker;

import com.lge.lgreplaymaker.event.*;


public class LGReplayMaker {

    public LGReplayMaker() {        
    }

    public static void main(String[] args) {
        System.out.println("LGReplayMaker is started !");

        LogExtractor logExtractor = new LogExtractor();

        LogReader logReader = new LogReader(logExtractor);
        logReader.read();

        RepWriter repWriter = new RepWriter();
        repWriter.write(logExtractor.getEventTreeMap());
        System.out.println("-End-");
    }    
}
