package Pooling;


import Pooling.PoolSimulator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class generateReferences {
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException
     {
        int n = 10;
        File folder = new File("NCNH");
        System.out.println(folder.exists());
        File[] list_files = folder.listFiles();
        ArrayList<String> addrs = new ArrayList();
        for (int i = 0; i < list_files.length; i++)
            addrs.add(list_files[i].getPath());
        PoolSimulator ps = new PoolSimulator();
        ps.createReferences(addrs, n);
        
     }
    
}
