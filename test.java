/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pooling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * @author kki8
 */
public class test {
    public static void main(String[] args) throws IOException
    {
        File folder = new File("Clean_by_cutoffs_meta10_split_new_1");
        File[] list_files = folder.listFiles();
        for (int i = 0; i < list_files.length; i++)
        {
            if (list_files[i].getName().startsWith("LYBP"))
            {
                StringTokenizer st = new StringTokenizer(list_files[i].getName(),"_");
                System.out.println(list_files[i].getName());
                String name = st.nextToken();
                FileWriter fw = new FileWriter(folder + File.separator + list_files[i].getName() + "_new.fas");
                BufferedReader br = new BufferedReader(new FileReader(folder + File.separator + list_files[i].getName()));
                String s = br.readLine();
                int count = 0;
                while (s != null)
                {
                    if (s.startsWith(">P"))
                    {
                        count++;
                        st = new StringTokenizer(s,"_");
                        String s1 = name + "_" + count;
                        st.nextToken();
                        while (st.hasMoreTokens())
                            s1 = s1 + "_" + st.nextToken();
                        s = s1;
                    }
                    fw.write(s + "\n");
                    s = br.readLine();
                }
                fw.close();
            }

        }
    }
    
}
