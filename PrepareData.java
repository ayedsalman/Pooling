package Pooling;


import ErrorCorrection.DataSet;
import ErrorCorrection.Read;
import java.io.File;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class PrepareData {
    public static void main(String[] args) throws IOException
    {
        String folderName = "Clean_by_cutoffs_meta10_split_new";
        File folder = new File(folderName);
        System.out.println(folder.exists());
        File[] list_files = folder.listFiles();
        
        for (int i = 0; i < list_files.length; i++)
        {
            String addr = list_files[i].getName();
            System.out.println(addr);
            String newaddr = addr.replaceFirst("_", "");
            DataSet ds = new DataSet(folderName + File.separator + addr,'c');
            for (Read r : ds.reads)
            {
                r.setFrequency(1);
                String s = r.name.replaceFirst("_", "");  
                r.name = s;
                System.out.println(s);
            }
            ds.PrintReadsNoCopyNumber(folderName + File.separator + newaddr);
        }
    }
    
}
