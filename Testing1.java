/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pooling;

import ErrorCorrection.DataSet;
import ErrorCorrection.Read;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author kki8
 */
public class Testing1 {
     public static void main(String[] args) throws FileNotFoundException, IOException
    {
         String[] indivSamp = {"HCNH278_MID7_run1_unique_meta_w4_unique.fas","HCNH279_MID8_run1_unique_meta_w4_unique.fas","HCNH281_MID1_run2_unique_meta_w4_unique.fas",
         "HCNH282_MID2_run2_unique_meta_w4_unique.fas","HCNH283_MID3_run2_unique_meta_w4_unique.fas","HCNH284_MID4_run2_unique_meta_w4_unique.fas",
         "HCNH285_MID5_run2_unique_meta_w4_unique.fas"};
         String indivFold = "NCNH";
         String[] poolSamp = {"a4.fas_2.fas","a10.fas_3.fas","a11.fas_4.fas","a12.fas_5.fas","a15.fas_6.fas","a14.fas_7.fas","a16.fas_8.fas"};
         String poolFolder = "Experiment";
         
         ArrayList<Read> primers = new ArrayList();
         Read prim = new Read("CTGGCACAT");
         primers.add(prim);
         
 /*        for (int i = 0; i < indivSamp.length; i++)
         {
             DataSet ds_orig = new DataSet(indivFold + File.separator + indivSamp[i],"ET",2);
             ds_orig.delPrimers(primers);
             ds_orig.delGaps();
             ds_orig.findHaplotypesAlign("ET", "Frequency", 15, 6);
             ds_orig.PrintHaplotypes(indivFold + File.separator + indivSamp[i] + "_hapl.fas", "ET");
         }*/
         
/*         for (int i = 0; i < poolSamp.length; i++)
         {
             DataSet ds_infer = new DataSet(poolFolder + File.separator + poolSamp[i],"ET");
//             ds_orig.delPrimers(primers);
//             ds_orig.delGaps();
             ds_infer.findHaplotypesAlign("ET", "Frequency", 15, 6);
             ds_infer.PrintHaplotypes(poolFolder + File.separator + poolSamp[i] + "_hapl.fas", "ET");
         } */
         
         
         for (int i = 0; i < indivSamp.length; i++)
         {
             System.out.println(poolSamp[i]);
             FileWriter fw = new FileWriter(poolFolder + File.separator + "freqStat" + (i+2) + "_indvsinf_new.txt");
             DataSet ds_orig = new DataSet(indivFold + File.separator + indivSamp[i] + "_hapl.fas","ET");
             DataSet ds_infer = new DataSet(poolFolder + File.separator + poolSamp[i] + "_hapl.fas","ET");

             ArrayList<Read> nonmatched = new ArrayList(ds_infer.reads);
              for (int j = 0; j < ds_orig.reads.size(); j++)
              {
                  Read r = ds_orig.reads.get(j);
                  System.out.println(j + "//" + ds_orig.reads.size());
                  double origFreq = (double) r.getFreq();
//                  double inferFreq = (double) ds_infer.getFrequencyAlign(r, 15, 6);                  
                  double inferFreq = 0;
                  ArrayList<Read> matched = new ArrayList();
                  for (Read s : nonmatched)
                      if (s.calcEditDistAbsAlign(r, 15, 6) == 0)
                      {
                        inferFreq += s.getFreq();
                        matched.add(s);
                      }
                  nonmatched.removeAll(matched);
                  fw.write(origFreq + " " + inferFreq + "\n");
              }
              for (Read s : nonmatched)
                  fw.write(0 + " " + s.getFreq() + "\n");
              fw.write("\n");
              fw.write(ds_orig.reads.size() + " " + ds_infer.reads.size() + "\n");
              fw.write(ds_orig.getTotalNReads() + " " + ds_infer.getTotalNReads() + "\n");

              fw.close();
         }
/*         for (int i = 0; i < indivSamp.length; i++)
         {
             System.out.println(poolSamp[i]);
             FileWriter fw = new FileWriter(poolFolder + File.separator + "freqStat" + (i+2) + "_infvsind.txt");
             DataSet ds_orig = new DataSet(indivFold + File.separator + indivSamp[i] + "_hapl.fas","ET");
             DataSet ds_infer = new DataSet(poolFolder + File.separator + poolSamp[i] + "_hapl.fas","ET");

              for (int j = 0; j < ds_infer.reads.size(); j++)
              {
                  Read r = ds_infer.reads.get(j);
                  System.out.println(j + "//" + ds_infer.reads.size());
                  double inferFreq = (double) r.getFreq();
//                  double origFreq = (double) ds_orig.getFrequencyAlign(r);
                  double origFreq = (double) ds_orig.getFrequencyAlign(r, 15, 6);
                  fw.write(origFreq + " " + inferFreq + "\n");
              }
              fw.write("\n");
              fw.write(ds_orig.getTotalNReads() + " " + ds_infer.getTotalNReads() + "\n");
              fw.close();
         }*/
    }
    
}
