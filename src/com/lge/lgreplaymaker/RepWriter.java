package com.lge.lgreplaymaker;

import com.lge.lgreplaymaker.event.*;
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;

public class RepWriter
{  
    TreeMap <LocalDateTime, Event>  eventTreeMap;
    
    static final String REP_DIR_PATH = "./rep/";
    static final String REP_FILE_NAME = "record.rep";

    private RandomAccessFile repFile = null;
    
    public RepWriter() {
    }

    public void write(TreeMap <LocalDateTime, Event>  eventTreeMap) {
        System.out.println("Writing REP file...");
        try {
            repFile = makeRepFile(REP_FILE_NAME);

            Set <LocalDateTime> keySet = eventTreeMap.keySet();
            Iterator <LocalDateTime> iterator = keySet.iterator();            
            while (iterator.hasNext()) {
                LocalDateTime time = iterator.next();
                String line = (eventTreeMap.get(time)).toString();
                writeRep(line);
                //System.out.println(eventTreeMap.get(time));
            }

            repFile.close();
        } catch(IOException e){
            System.out.println(e);
        }
    }

    private RandomAccessFile makeRepFile(String name) {
        try {
            File repDir = new File(REP_DIR_PATH);
            repDir.mkdir();

            File checkFile = new File(REP_DIR_PATH + name);

            if (checkFile.exists()) {
                checkFile.delete();
            }

            RandomAccessFile raf = new RandomAccessFile(REP_DIR_PATH + name, "rw");            
            return raf;
        } catch(Exception e){
            System.out.println("e:" + e);
            return null;
        }
    }

    private void writeRep(String repLine) {
        try {
            repFile.seek(repFile.length()); //�Ǹ����� ��ġ�� Ŀ�� �̵�              
            //String r_str = new String(str.getBytes("KSC5601"),"8859_1");//�׳� str�� ����ϰԵǸ� �ѱ��� �����µ� 
            // ���� �ʿ� �� Encoding �۾��� �ʿ��ϸ� ���
            String outString = repLine + "\r\n";
            repFile.writeBytes(outString);
        }catch(IOException e){
            System.out.println("e:" + e);
        }
    }
}


