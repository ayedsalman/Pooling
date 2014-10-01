/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pooling;

import ErrorCorrection.DataSet;
import ErrorCorrection.Read;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author kki8
 */
public class Testing {
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
         int[] ns = {10,20,30,40,50,60,70,80,90,100,110,120,130,140,150};
         int[] maxPoolSizes = {15,25,35};
         int nTests = 10;
         int[] clcoeffs = {3,4};
         
          for (int sz = 0; sz < maxPoolSizes.length; sz++)
         {
              for (int in = 0; in < ns.length; in++)
              {
                  for (int cl = 0; cl < clcoeffs.length; cl++)
                  {
                     for (int it = 0; it < nTests; it++)
                     {
                        String outdir = "Test_" + maxPoolSizes[sz] + "_" + ns[in] + "_" + clcoeffs[cl] + "_" + it;
                        System.out.println(outdir);
                        File folder = new File(outdir);
                        File[] list_files = folder.listFiles();
                        for (int i = 0; i < list_files.length; i++)
                        {
                            System.out.println(list_files[i].getName());
                            StringTokenizer st = new StringTokenizer(list_files[i].getName(),"_");
                            String pref = st.nextToken();
                            if (pref.equalsIgnoreCase("clust"))
                            {
                                FileUtils.deleteDirectory(list_files[i]);
                            }
                            if (pref.equalsIgnoreCase("u"))
                                list_files[i].delete();
                            if (pref.startsWith("a"))
                                list_files[i].delete();
                           
                        }
                        System.out.println();
                     }
                 }
              }
             
         
         }
    }
    
}
