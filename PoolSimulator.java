package Pooling;


import Pooling.Pool;
import ErrorCorrection.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pavel
 */
public class PoolSimulator {
    ArrayList<DataSet> samples;
    String addrSamples;
    public ArrayList<String> sampFileNames;
    PoolSimulator()
    {
        
    }
    PoolSimulator(ArrayList<DataSet> ar)
    {
        samples = ar;
    }
    PoolSimulator(String addr)
    {
        this.addrSamples = addr;
    }
    ArrayList<Pool> generate()
    {
        PoolsOperator po = new PoolsOperator();
        int n = samples.size();
        ArrayList<ArrayList<Integer>> partitions = po.generatePartitions(n);
        ArrayList<Pool> mixtures = new ArrayList();
        for (ArrayList<Integer> partition : partitions)
        {
//            DataSet dspool = new DataSet();
            DataSet dspool = null;
            for (Integer i : partition)
                dspool.reads.addAll(samples.get(i-1).reads);
            Pool p = new Pool(dspool,partition);
            mixtures.add(p);
        }        
        return mixtures;
    }
    ArrayList<Pool> generate(int n, int nSeqSamp) throws FileNotFoundException, IOException
    {
        File folder = new File(addrSamples);
        System.out.println(folder.exists());
        File[] list_files = folder.listFiles();
        
        samples = new ArrayList<DataSet>();
        ArrayList<Integer> ind = new ArrayList();
        for (int i = 0; i < list_files.length; i++)
            ind.add(i);
        DataSet refs = new DataSet();
        int nref = 1;
        for (int i = 0; i < n; i++)
        {
            int j = (int) (ind.size()*Math.random());
            System.out.println(list_files[ind.get(j)].getPath());
            DataSet ds = new DataSet(list_files[ind.get(j)].getPath(),"ET");
            DataSet ds_nonun = new DataSet();
            ArrayList<Read> mostFreq = ds.FindMostFreqReads(nref);
            refs.reads.addAll(mostFreq);
            for (Read r : ds.reads)
                for (int f = 0; f < r.getFreq(); f++)
                    ds_nonun.reads.add(new Read(r.getNucl(),r.name,1));
            DataSet sampDS = new DataSet();
            for (int k = 0; k < nSeqSamp; k++)
            {
                int q = (int) (ds_nonun.reads.size()*Math.random());
                sampDS.reads.add(ds_nonun.reads.get(q));
            }
            samples.add(sampDS);
            ind.remove(ind.get(j));
        }
        
        refs.PrintUniqueReads("simRef.fas");
        
        PoolsOperator po = new PoolsOperator();
        ArrayList<ArrayList<Integer>> partitions = po.generatePartitions(n);
        for (ArrayList<Integer> ar : partitions)
        {
             for (Integer i : ar)
                 System.out.print(i + " ");
             System.out.println();
        }
        ArrayList<Pool> mixtures = new ArrayList();
        for (ArrayList<Integer> partition : partitions)
        {
//            DataSet dspool = new DataSet();
            DataSet dspool = new DataSet();
            for (Integer i : partition)
                dspool.reads.addAll(samples.get(i-1).reads);
            Pool p = new Pool(dspool,partition);
            mixtures.add(p);
        }        
        return mixtures;
    }
    ArrayList<Pool> generate(int n, int nSeqSamp,  ArrayList<ArrayList<Integer>> partitions) throws FileNotFoundException, IOException
    {
        File folder = new File(addrSamples);
        System.out.println(folder.exists());
        File[] list_files = folder.listFiles();
        
        samples = new ArrayList<DataSet>();
        ArrayList<Integer> ind = new ArrayList();
        for (int i = 0; i < list_files.length; i++)
            ind.add(i);
        DataSet refs = new DataSet();
        int nref = 1;
        for (int i = 0; i < n; i++)
        {
            int j = (int) (ind.size()*Math.random());
            System.out.println(list_files[ind.get(j)].getPath());
            DataSet ds = new DataSet(list_files[ind.get(j)].getPath(),"ET");
            DataSet ds_nonun = new DataSet();
            ArrayList<Read> mostFreq = ds.FindMostFreqReads(nref);
            refs.reads.addAll(mostFreq);
            for (Read r : ds.reads)
                for (int f = 0; f < r.getFreq(); f++)
                    ds_nonun.reads.add(new Read(r.getNucl(),r.name,1));
            DataSet sampDS = new DataSet();
            for (int k = 0; k < nSeqSamp; k++)
            {
                int q = (int) (ds_nonun.reads.size()*Math.random());
                sampDS.reads.add(ds_nonun.reads.get(q));
            }
            samples.add(sampDS);
            ind.remove(ind.get(j));
        }
        
        refs.PrintUniqueReads("simRef.fas");
        
        for (ArrayList<Integer> ar : partitions)
        {
             for (Integer i : ar)
                 System.out.print(i + " ");
             System.out.println();
        }
        ArrayList<Pool> mixtures = new ArrayList();
        for (ArrayList<Integer> partition : partitions)
        {
//            DataSet dspool = new DataSet();
            DataSet dspool = new DataSet();
            for (Integer i : partition)
                dspool.reads.addAll(samples.get(i-1).reads);
            Pool p = new Pool(dspool,partition);
            mixtures.add(p);
        }        
        return mixtures;
    }
    ArrayList<Pool> generate(int nSamples, int nSeqPool,ArrayList<ArrayList<Double>> frequencies, ArrayList<ArrayList<Integer>> partitions) throws FileNotFoundException, IOException
    {
        int n = nSamples;
        int nRef = 5;
        
        File folder = new File(addrSamples);
        System.out.println(folder.exists());
        File[] list_files = folder.listFiles();
        
        samples = new ArrayList<DataSet>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> names_full = new ArrayList<String>();
        ArrayList<Integer> ind = new ArrayList();
        for (int i = 0; i < list_files.length; i++)
            ind.add(i);
        
        
        for (int i = 0; i < n; i++)
        {
            int j = (int) (ind.size()*Math.random());
            String addr = list_files[ind.get(j)].getPath();
            System.out.println(addr);
            samples.add(new DataSet(addr,"ET"));
            ind.remove(ind.get(j));
            StringTokenizer st = new StringTokenizer(addr,"" + File.separator);
            st.nextToken();
            names.add(st.nextToken());
            names_full.add(addr);
        }
        

        ArrayList<Pool> mixtures = new ArrayList();
        for (int i = 0; i < frequencies.size(); i++)
        {
            ArrayList<Double> distr = frequencies.get(i);
            DataSet dspool = new DataSet();
            for (int j = 0; j < n; j++)
            {
                int nSeqSamp = (int) (nSeqPool*distr.get(j));
                DataSet ds_nonun = new DataSet();
                for (Read r : samples.get(j).reads)
                    for (int f = 0; f < r.getFreq(); f++)
                        ds_nonun.reads.add(new Read(r.getNucl(),r.name,1));
                HashMap<String,Integer> seq = new HashMap<String, Integer>();
                if (nSeqSamp == 0)
                    continue;
                
                int maxK = Math.min(nSeqSamp, ds_nonun.reads.size());
                for (int k = 0; k < maxK; k++)
                {
                    int q = (int) (ds_nonun.reads.size()*Math.random());
                    Read r = ds_nonun.reads.get(q);
                    String nucl = r.getNucl();
                    if (seq.containsKey(nucl))
                        seq.put(nucl, seq.get(nucl)+1);
                    else
                        seq.put(nucl, 1);
                    ds_nonun.reads.remove(r);
                }
                Iterator it = seq.entrySet().iterator();
		int count = 1;
		while (it.hasNext())
		{
			Map.Entry me = (Map.Entry)it.next();
			String nucleo = (String) me.getKey(); 
			int f = (Integer) me.getValue();
			String nm = names.get(j) + "_n_" + count;
			dspool.reads.add(new Read(nucleo,nm,f));
			count++;
		}
                
            }
            Pool p = new Pool(dspool,partitions.get(i));
            mixtures.add(p);
        }        
        return mixtures;
    }
    ArrayList<Pool> generate(int nSamples, int nSeqPool,ArrayList<ArrayList<Double>> frequencies, ArrayList<ArrayList<Integer>> partitions, String logfile) throws FileNotFoundException, IOException
    {
        int n = nSamples;
        int nRef = 5;
        
        File folder = new File(addrSamples);
        System.out.println(folder.exists());
        File[] list_files = folder.listFiles();
        
        samples = new ArrayList<DataSet>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> names_full = new ArrayList<String>();
        ArrayList<Integer> ind = new ArrayList();
        for (int i = 0; i < list_files.length; i++)
            ind.add(i);
        
        ArrayList<String> sampNames = new ArrayList();
        int[] sampReads = new int[n];
        ArrayList<HashSet<String>> sampHapls = new ArrayList();
        for (int i = 0; i < n; i++)
            sampHapls.add(new HashSet());
    

        
        for (int i = 0; i < n; i++)
        {
            int j = (int) (ind.size()*Math.random());
            String addr = list_files[ind.get(j)].getPath();
            String name = list_files[ind.get(j)].getName();
            System.out.println(name);
            sampNames.add(name);
            samples.add(new DataSet(addr,"ET"));
            ind.remove(ind.get(j));
            StringTokenizer st = new StringTokenizer(addr,"" + File.separator);
            st.nextToken();
            names.add(st.nextToken());
            names_full.add(addr);
        }

        ArrayList<Pool> mixtures = new ArrayList();
        for (int i = 0; i < frequencies.size(); i++)
        {
            ArrayList<Double> distr = frequencies.get(i);
            DataSet dspool = new DataSet();
            for (int j = 0; j < n; j++)
            {
                int nSeqSamp = (int) (nSeqPool*distr.get(j));
//                System.out.println(distr.get(j));
//                System.out.println(nSeqSamp);
                DataSet ds_nonun = new DataSet();
                for (Read r : samples.get(j).reads)
                    for (int f = 0; f < r.getFreq(); f++)
                        ds_nonun.reads.add(new Read(r.getNucl(),r.name,1));
                HashMap<String,Integer> seq = new HashMap<String, Integer>();
                if (nSeqSamp == 0)
                    continue;
                
                int maxK = Math.min(nSeqSamp, ds_nonun.reads.size());
                for (int k = 0; k < maxK; k++)
                {
                    int q = (int) (ds_nonun.reads.size()*Math.random());
                    Read r = ds_nonun.reads.get(q);
                    String nucl = r.getNucl();
                    if (seq.containsKey(nucl))
                        seq.put(nucl, seq.get(nucl)+1);
                    else
                        seq.put(nucl, 1);
                    ds_nonun.reads.remove(r);
                }
                Iterator it = seq.entrySet().iterator();
		int count = 1;
		while (it.hasNext())
		{
			Map.Entry me = (Map.Entry)it.next();
			String nucleo = (String) me.getKey(); 
			int f = (Integer) me.getValue();
			String nm = names.get(j) + "_n_" + count;
			dspool.reads.add(new Read(nucleo,nm,f));
			count++;
                        sampReads[j] += f;
                        sampHapls.get(j).add(nucleo);
		}
                
            }
            Pool p = new Pool(dspool,partitions.get(i));
            mixtures.add(p);
        } 
        
        FileWriter fw = new FileWriter(logfile);
        for (int i = 0; i < n; i++)
            fw.write((i+1) + ": " + sampNames.get(i) + " " + sampReads[i] + " " + sampHapls.get(i).size() + "\n");
        fw.close();
        
        return mixtures;
    }
    ArrayList<Pool> generateWithSampErrors(int nSamples, int nSeqPool,ArrayList<ArrayList<Double>> frequencies, ArrayList<ArrayList<Integer>> partitions, String logfile,ArrayList<ArrayList<Integer>> misSamp) throws FileNotFoundException, IOException
    {
        int n = nSamples;
        int nRef = 5;
        
        File folder = new File(addrSamples);
        System.out.println(folder.exists());
        File[] list_files = folder.listFiles();
        
        samples = new ArrayList<DataSet>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> names_full = new ArrayList<String>();
        ArrayList<Integer> ind = new ArrayList();
        for (int i = 0; i < list_files.length; i++)
            ind.add(i);
        
        ArrayList<String> sampNames = new ArrayList();
        int[] sampReads = new int[n];
        ArrayList<HashSet<String>> sampHapls = new ArrayList();
        for (int i = 0; i < n; i++)
            sampHapls.add(new HashSet());
    

        
        for (int i = 0; i < n; i++)
        {
            int j = (int) (ind.size()*Math.random());
            String addr = list_files[ind.get(j)].getPath();
            String name = list_files[ind.get(j)].getName();
            System.out.println(name);
            sampNames.add(name);
            samples.add(new DataSet(addr,"ET"));
            ind.remove(ind.get(j));
            StringTokenizer st = new StringTokenizer(addr,"" + File.separator);
            st.nextToken();
            names.add(st.nextToken());
            names_full.add(addr);
        }

        ArrayList<Pool> mixtures = new ArrayList();
        for (int i = 0; i < frequencies.size(); i++)
        {
            ArrayList<Double> distr = frequencies.get(i);
            DataSet dspool = new DataSet();
            for (int j = 0; j < n; j++)
            {
                int nSeqSamp = (int) (nSeqPool*distr.get(j));
//                System.out.println(distr.get(j));
//                System.out.println(nSeqSamp);
                DataSet ds_nonun = new DataSet();
                for (Read r : samples.get(j).reads)
                    for (int f = 0; f < r.getFreq(); f++)
                        ds_nonun.reads.add(new Read(r.getNucl(),r.name,1));
                HashMap<String,Integer> seq = new HashMap<String, Integer>();
                if (nSeqSamp == 0)
                    continue;
                if (misSamp.get(i).contains(j+1))
                    continue;
                
                int maxK = Math.min(nSeqSamp, ds_nonun.reads.size());
                for (int k = 0; k < maxK; k++)
                {
                    int q = (int) (ds_nonun.reads.size()*Math.random());
                    Read r = ds_nonun.reads.get(q);
                    String nucl = r.getNucl();
                    if (seq.containsKey(nucl))
                        seq.put(nucl, seq.get(nucl)+1);
                    else
                        seq.put(nucl, 1);
                    ds_nonun.reads.remove(r);
                }
                Iterator it = seq.entrySet().iterator();
		int count = 1;
		while (it.hasNext())
		{
			Map.Entry me = (Map.Entry)it.next();
			String nucleo = (String) me.getKey(); 
			int f = (Integer) me.getValue();
			String nm = names.get(j) + "_n_" + count;
			dspool.reads.add(new Read(nucleo,nm,f));
			count++;
                        sampReads[j] += f;
                        sampHapls.get(j).add(nucleo);
		}
                
            }
            Pool p = new Pool(dspool,partitions.get(i));
            mixtures.add(p);
        } 
        
        FileWriter fw = new FileWriter(logfile);
        for (int i = 0; i < n; i++)
            fw.write((i+1) + ": " + sampNames.get(i) + " " + sampReads[i] + " " + sampHapls.get(i).size() + "\n");
        fw.close();
        this.sampFileNames = sampNames;
        
        return mixtures;
    }
    ArrayList<ArrayList<Integer>> generateMissingSamples(ArrayList<ArrayList<Integer>> partitions, int nBadPools, int nMisSamp, String logfileMis) throws IOException
    {
        ArrayList<Integer> badPools = new ArrayList();
        ArrayList<Integer> candBadPools = new ArrayList();
        for (int i = 0; i < partitions.size(); i++)
            if (partitions.get(i).size() > 1)
                candBadPools.add(i);
        
        for (int j = 0; j < Math.min(nBadPools, candBadPools.size()); j++)
        {
            int i = (int) (Math.random()*candBadPools.size());
            badPools.add(candBadPools.get(i));
            candBadPools.remove(i);
        }
        
        ArrayList<ArrayList<Integer>> misSamp = new ArrayList();
        for (int i = 0; i < partitions.size(); i++)
        {
            ArrayList<Integer> misSampCurr = new ArrayList();
            if (badPools.contains(i))
            {
                ArrayList<Integer> remainSampCurr = new ArrayList(partitions.get(i));
                for (int j = 0; j < nMisSamp; j++)
                {
                    int misInd = (int) (Math.random()*remainSampCurr.size());
                    misSampCurr.add(remainSampCurr.get(misInd));
                    remainSampCurr.remove(misSampCurr);

                }
            }
            misSamp.add(misSampCurr);
        }
        
        FileWriter fwMis = new FileWriter(logfileMis);
        for (int i = 0; i <  misSamp.size(); i++)
        {
            fwMis.write((i+1) + ": ");
            ArrayList<Integer> mis = misSamp.get(i);
            for (Integer j : mis)
                fwMis.write(j + " ");
            fwMis.write("\n");
        }
        fwMis.close();
        return misSamp;
    }
    ArrayList<ArrayList<Integer>> generateMissingSamples(ArrayList<ArrayList<Integer>> partitions, int nBadPools, double percMisSamp, String logfileMis) throws IOException
    {
        ArrayList<Integer> badPools = new ArrayList();
        ArrayList<Integer> candBadPools = new ArrayList();
        for (int i = 0; i < partitions.size(); i++)
            if (partitions.get(i).size() > 1)
                candBadPools.add(i);
        
        for (int j = 0; j < Math.min(nBadPools, candBadPools.size()); j++)
        {
            int i = (int) (Math.random()*candBadPools.size());
            badPools.add(candBadPools.get(i));
            candBadPools.remove(i);
        }
        
        ArrayList<ArrayList<Integer>> misSamp = new ArrayList();
        for (int i = 0; i < partitions.size(); i++)
        {
            ArrayList<Integer> misSampCurr = new ArrayList();
            if (badPools.contains(i))
            {
                ArrayList<Integer> remainSampCurr = new ArrayList(partitions.get(i));
                int nMisSamp = Math.max((int) (percMisSamp*partitions.get(i).size()),1);
                for (int j = 0; j < nMisSamp; j++)
                {
                    int misInd = (int) (Math.random()*remainSampCurr.size());
                    misSampCurr.add(remainSampCurr.get(misInd));
                    remainSampCurr.remove(misSampCurr);

                }
            }
            misSamp.add(misSampCurr);
        }
        
        FileWriter fwMis = new FileWriter(logfileMis);
        for (int i = 0; i <  misSamp.size(); i++)
        {
            fwMis.write((i+1) + ": ");
            ArrayList<Integer> mis = misSamp.get(i);
            for (Integer j : mis)
                fwMis.write(j + " ");
            fwMis.write("\n");
        }
        fwMis.close();
        return misSamp;
    }
    ArrayList<Pool> generateWithRefs(int nSamples, int nSeqPool,ArrayList<ArrayList<Double>> frequencies, ArrayList<ArrayList<Integer>> partitions, int nRef) throws FileNotFoundException, IOException, InterruptedException
    {
        int n = nSamples;
        
        File folder = new File(addrSamples);
        System.out.println(folder.exists());
        File[] list_files = folder.listFiles();
        
        samples = new ArrayList<DataSet>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> names_full = new ArrayList<String>();
        ArrayList<Integer> ind = new ArrayList();
        for (int i = 0; i < list_files.length; i++)
            ind.add(i);
        
        ArrayList<String> addrs = new ArrayList();
        
        for (int i = 0; i < n; i++)
        {
            int j = (int) (ind.size()*Math.random());
            String addr = list_files[ind.get(j)].getPath();
            addrs.add(addr);
            System.out.println(addr);
            samples.add(new DataSet(addr,"ET"));
            ind.remove(ind.get(j));
            StringTokenizer st = new StringTokenizer(addr,"" + File.separator);
            st.nextToken();
            names.add(st.nextToken());
            names_full.add(addr);
        }
        
        createReferences(addrs, nRef);

        ArrayList<Pool> mixtures = new ArrayList();
        for (int i = 0; i < frequencies.size(); i++)
        {
            ArrayList<Double> distr = frequencies.get(i);
            DataSet dspool = new DataSet();
            for (int j = 0; j < n; j++)
            {
                int nSeqSamp = (int) (nSeqPool*distr.get(j));
                DataSet ds_nonun = new DataSet();
                for (Read r : samples.get(j).reads)
                    for (int f = 0; f < r.getFreq(); f++)
                        ds_nonun.reads.add(new Read(r.getNucl(),r.name,1));
                HashMap<String,Integer> seq = new HashMap<String, Integer>();
                if (nSeqSamp == 0)
                    continue;
                
                int maxK = Math.min(nSeqSamp, ds_nonun.reads.size());
                for (int k = 0; k < maxK; k++)
                {
                    int q = (int) (ds_nonun.reads.size()*Math.random());
                    Read r = ds_nonun.reads.get(q);
                    String nucl = r.getNucl();
                    if (seq.containsKey(nucl))
                        seq.put(nucl, seq.get(nucl)+1);
                    else
                        seq.put(nucl, 1);
                    ds_nonun.reads.remove(r);
                }
                Iterator it = seq.entrySet().iterator();
		int count = 1;
		while (it.hasNext())
		{
			Map.Entry me = (Map.Entry)it.next();
			String nucleo = (String) me.getKey(); 
			int f = (Integer) me.getValue();
			String nm = names.get(j) + "_n_" + count;
			dspool.reads.add(new Read(nucleo,nm,f));
			count++;
		}
                
            }
            Pool p = new Pool(dspool,partitions.get(i));
            mixtures.add(p);
        }        
        return mixtures;
    }
    void createReferences(ArrayList<String> addrs, int n) throws FileNotFoundException, IOException, InterruptedException
    {
        DataSet refs = new DataSet();
        for (String s : addrs)
        {
            DataSet ds = new DataSet(s,"ET",1);
            ds.delGaps();
            String newaddr = s + "_allreads.fas";
            StringTokenizer st = new StringTokenizer(newaddr,"" + File.separator);
            st.nextToken();
            String outfile = st.nextToken();
            ds.PrintReads(outfile);
            PoolsOperator po = new PoolsOperator();
            po.clusteringButterfly(n, outfile);
            ds = new DataSet("clust_" + outfile + File.separator + "haplotypes_cleaned.fas",'c');
            for (int i = 0; i < ds.reads.size(); i++)
            {
                ds.reads.get(i).name = outfile + "_" + (i+1);
                refs.reads.add(ds.reads.get(i));
            }
        }
        refs.PrintUniqueReads("refer_gener" + n + ".fas");
    }
    ArrayList<ArrayList<Double>> generateFreqDistr(int nSamp, ArrayList<ArrayList<Integer>> partitions, String distrType)
    {
        ArrayList<ArrayList<Double>> freq = new ArrayList();
        for (ArrayList<Integer> pool : partitions)
        {
            ArrayList<Integer> ind = new ArrayList();
            for (int i = 0; i < pool.size(); i++)
                ind.add(i);
            ArrayList<Double> distrib_prelim = new ArrayList(pool.size());
            ArrayList<Double> distrib = new ArrayList(pool.size());
            if (distrType.equalsIgnoreCase("Uniform"))
                for (int i = 0; i < pool.size(); i++)
                    distrib_prelim.add(1.0/pool.size());
            if (distrType.equalsIgnoreCase("Geometric"))
            {
                double base = 1 + 0.5*Math.random();
                ArrayList<Double> prog = new ArrayList<Double>();
                prog.add(1.0);
                double sum = 1.0;
                for (int i = 1; i < pool.size(); i++)
                {
                    double x = prog.get(prog.size()-1)*base;
                    prog.add(x);
                    sum += x;
                }                
                for (int i = 0; i < pool.size(); i++)
                    distrib_prelim.add(prog.get(i)/sum);
                
/*                double y = Double.MAX_VALUE;
                for (int i = 0; i < distrib_prelim.size(); i++)
                    if (distrib_prelim.get(i) < y)
                        y = distrib_prelim.get(i);
                        
                System.out.println(100*y);*/
            }
            while (ind.size() > 0)
            {
                int q = (int) (Math.random()*ind.size());
                distrib.add(distrib_prelim.get(ind.get(q)));
                ind.remove(ind.get(q));
            }
            
            ArrayList<Double> ar = new ArrayList(nSamp);
            int j = 0;
            for (int i = 1; i <= nSamp; i++)
                if (!pool.contains(i))
                    ar.add(i-1, 0.0);
                else
                {
                    ar.add(i-1,distrib.get(j));
                    j++;
                }
            freq.add(ar);
        }        
        return freq;
    }
    boolean checkClustering(Pool p, String method) throws IOException
    {
        String addr = "";
        if (method.equalsIgnoreCase("Butterfly"))
            addr ="clust_" + p.fileName + File.separator + "reads_clustered.fas";
        if (method.equalsIgnoreCase("Matlab"))
            addr ="clust_" + p.fileName + File.separator + "reads_clustered_matlab.fas";
        DataSet clustered_ds = new DataSet(addr,'c');
        
        HashMap<Integer,ArrayList<String>> hm = new HashMap();
        
        for (int i = 0; i < clustered_ds.reads.size(); i++)
            {
                StringTokenizer st = new StringTokenizer(clustered_ds.reads.get(i).name,"_");
//                System.out.println(clustered_ds.reads.get(i).name);
                String s = st.nextToken();
                int clust = Integer.parseInt(s.substring(1));
                st.nextToken();
                s = st.nextToken();
                if (hm.containsKey(clust))
                {
                    ArrayList<String> ar = hm.get(clust);
                    boolean toAdd = true;
                    for (String samp : ar)
                        if (samp.equalsIgnoreCase(s))
                        {
                            toAdd = false;
                            break;
                        }
                    if (toAdd)
                        ar.add(s);
                    hm.put(clust, ar);
                }
                else
                {
                    ArrayList<String> ar = new ArrayList();
                    ar.add(s);
                    hm.put(clust, ar);
                }
            }
        boolean output = true;
        for (Map.Entry me : hm.entrySet())
        {
            ArrayList<String> ar = (ArrayList<String>) me.getValue();
            int clust = (Integer) me.getKey();
            if (ar.size() > 1)
                output = false;
            System.out.print("c" + clust + ": ");
            for (String s: ar)
                System.out.print(s + " ");
            System.out.println();
        }
        return output;
    }
    boolean checkClustering(String addr) throws IOException
    {
        DataSet clustered_ds = new DataSet(addr,'c');
        
        HashMap<Integer,ArrayList<String>> hm = new HashMap();
        
        for (int i = 0; i < clustered_ds.reads.size(); i++)
            {
                StringTokenizer st = new StringTokenizer(clustered_ds.reads.get(i).name,"_");
//                System.out.println(clustered_ds.reads.get(i).name);
                String s = st.nextToken();
                int clust = Integer.parseInt(s.substring(1));
                st.nextToken();
                s = st.nextToken();
                if (hm.containsKey(clust))
                {
                    ArrayList<String> ar = hm.get(clust);
                    boolean toAdd = true;
                    for (String samp : ar)
                        if (samp.equalsIgnoreCase(s))
                        {
                            toAdd = false;
                            break;
                        }
                    if (toAdd)
                        ar.add(s);
                    hm.put(clust, ar);
                }
                else
                {
                    ArrayList<String> ar = new ArrayList();
                    ar.add(s);
                    hm.put(clust, ar);
                }
            }
        boolean output = true;
        for (Map.Entry me : hm.entrySet())
        {
            ArrayList<String> ar = (ArrayList<String>) me.getValue();
            int clust = (Integer) me.getKey();
            if (ar.size() > 1)
                output = false;
            System.out.print("c" + clust + ": ");
            for (String s: ar)
                System.out.print(s + " ");
            System.out.println();
        }
        return output;
    }
    String checkClusteringGetOutString(String addr) throws IOException
    {
        DataSet clustered_ds = new DataSet(addr,'c');
        String outs = "";
        
        HashMap<Integer,ArrayList<String>> hm = new HashMap();
        
        for (int i = 0; i < clustered_ds.reads.size(); i++)
            {
                StringTokenizer st = new StringTokenizer(clustered_ds.reads.get(i).name,"_");
//                System.out.println(clustered_ds.reads.get(i).name);
                String s = st.nextToken();
                int clust = Integer.parseInt(s.substring(1));
                st.nextToken();
                s = st.nextToken();
                if (hm.containsKey(clust))
                {
                    ArrayList<String> ar = hm.get(clust);
                    boolean toAdd = true;
                    for (String samp : ar)
                        if (samp.equalsIgnoreCase(s))
                        {
                            toAdd = false;
                            break;
                        }
                    if (toAdd)
                        ar.add(s);
                    hm.put(clust, ar);
                }
                else
                {
                    ArrayList<String> ar = new ArrayList();
                    ar.add(s);
                    hm.put(clust, ar);
                }
            }
        boolean output = true;
        for (Map.Entry me : hm.entrySet())
        {
            ArrayList<String> ar = (ArrayList<String>) me.getValue();
            int clust = (Integer) me.getKey();
            if (ar.size() > 1)
                output = false;
            System.out.print("c" + clust + ": ");
            outs += "c" + clust + ": ";
            for (String s: ar)
            {
                System.out.print(s + " ");
                outs+=s + " ";
            }
            System.out.println();
            outs += "\n";
        }
        return outs;
    }
     boolean checkSolution(Pool p) throws IOException
    {
        String numsamples = "";
         for (Integer i : p.indsamples)
            numsamples += ("_" + i);
        String addr = p.fileName + numsamples + ".fas";
        DataSet clustered_ds = new DataSet(addr,'c');
        
        HashSet<String> hs = new HashSet();
        HashMap<String,Integer> countsUnique = new HashMap();
        
        for (int i = 0; i < clustered_ds.reads.size(); i++)
            {
                StringTokenizer st = new StringTokenizer(clustered_ds.reads.get(i).name,"_");
//                System.out.println(clustered_ds.reads.get(i).name);
                String clust = st.nextToken();
                hs.add(clust);
                if (countsUnique.containsKey(clust))
                {
                    int c = countsUnique.get(clust);
                    countsUnique.put(clust, c + 1);
                }
                else
                    countsUnique.put(clust, 1);
            }
        

        boolean output = true;
        for (String s : hs)
            System.out.print(s + " (" + countsUnique.get(s) + ") ");
        if (hs.size() > 1)
            return false;
        return true;
    }
      String checkSolutionGetOutString(Pool p) throws IOException
    {
        String numsamples = "";
         for (Integer i : p.indsamples)
            numsamples += ("_" + i);
        String addr = p.fileName + numsamples + ".fas";
        
        File f = new File(addr);
        if (f.length() == 0)
            return "";
        
        DataSet clustered_ds = new DataSet(addr,'c');
        
        HashSet<String> hs = new HashSet();
        HashMap<String,Integer> countsUnique = new HashMap();
        
        for (int i = 0; i < clustered_ds.reads.size(); i++)
            {
                StringTokenizer st = new StringTokenizer(clustered_ds.reads.get(i).name,"_");
//                System.out.println(clustered_ds.reads.get(i).name);
                String clust = st.nextToken();
                hs.add(clust);
                if (countsUnique.containsKey(clust))
                {
                    int c = countsUnique.get(clust);
                    countsUnique.put(clust, c + 1);
                }
                else
                    countsUnique.put(clust, 1);
            }
        

        String outString = "";
        for (String s : hs)
        {
           outString = outString + s + " (" + countsUnique.get(s) + ") ";
        }
        
        return outString;
    }
      String checkSolutionGetOutString(Pool p, String outdir) throws IOException
    {
        String numsamples = "";
         for (Integer i : p.indsamples)
            numsamples += ("_" + i);
        String addr = outdir + File.separator + p.fileName + numsamples + ".fas";
        
        File f = new File(addr);
        if (f.length() == 0)
            return "";
        
        DataSet clustered_ds = new DataSet(addr,"ET");
        Read mr = clustered_ds.findMostFreqRead();
        
        HashSet<String> hs = new HashSet();
        HashMap<String,Integer> countsUnique = new HashMap();
        HashMap<String,Integer> countsAll = new HashMap();
        
        for (int i = 0; i < clustered_ds.reads.size(); i++)
            {
                Read r = clustered_ds.reads.get(i);
                StringTokenizer st = new StringTokenizer(r.name,"_");
//                System.out.println(clustered_ds.reads.get(i).name);
                String clust = st.nextToken();
                hs.add(clust);
                if (countsUnique.containsKey(clust))
                {
                    int c = countsUnique.get(clust);
                    int d = countsAll.get(clust);
                    countsUnique.put(clust, c + 1);
                    countsAll.put(clust, d + r.getFreq());
                }
                else
                {
                    countsUnique.put(clust, 1);
                    countsAll.put(clust, r.getFreq());
                }
            }
        

        String outString = "";
        for (String s : hs)
        {
           outString = outString + s + " " + countsAll.get(s) + " " + countsUnique.get(s) + " ";
        }
        
        return outString;
    }
      void generateReport(String genFile, String resFile, int nPools, long workTime, String outdir, int nSamp) throws IOException
      {
          FileWriter fw = new FileWriter(outdir + File.separator + "report.txt");
          BufferedReader brGen = new BufferedReader(new FileReader(genFile));
          BufferedReader brRes = new BufferedReader(new FileReader(resFile));
          int goodReads = 0;
          int goodSamp = 0;
          int allReads = 0;
          int contSamp = 0;
          ArrayList<Double> percsContReads = new ArrayList();
          for (int i = 0; i < nSamp; i++)
          {
              String sgen = brGen.readLine();
              String sres = brRes.readLine();
              StringTokenizer stGen = new StringTokenizer(sgen," _");
              StringTokenizer stRes = new StringTokenizer(sres," _");
              int nTok = stRes.countTokens();
              stGen.nextToken();
              stRes.nextToken();
              String realSamp = stGen.nextToken();
              stGen.nextToken();
              int realReads = Integer.parseInt(stGen.nextToken());
              allReads+=realReads;
              boolean isFound = false;
              int allReadsSamp = 0;
              int allContReadsSamp = 0;
              while (stRes.hasMoreTokens())
              {
                  String samp = stRes.nextToken();
                  int reads = Integer.parseInt(stRes.nextToken());
                  allReadsSamp += reads;
                  stRes.nextToken();
                  if (samp.equalsIgnoreCase(realSamp))
                  {
                      goodSamp++;
                      goodReads+=reads;
                      isFound = true;
                  }
                  else
                  {
                      allContReadsSamp+=reads;
                  }
              }
              if (isFound && (nTok > 4))
              {
                  contSamp++;
                  double d = 100*((double) allContReadsSamp/allReadsSamp);
                  percsContReads.add(d);
              }
          }
          double percGoodSamp = 100*((double) goodSamp/nSamp);
          double percContSamp = 100*((double) contSamp/nSamp);
          double percGoodReads = 100*((double) goodReads/allReads);
          double avpercsContReads = 0;
          for (Double d : percsContReads)
              avpercsContReads+=d;
          avpercsContReads/=contSamp;
          fw.write("Perc of recovered samp: " + percGoodSamp + "\n");
          fw.write("Perc of cont samp: " + percContSamp + "\n");
          fw.write("Perc of correctly classified reads: " + percGoodReads + "\n");
          fw.write("Av perc of wrongly classified reads in cont samp: " + avpercsContReads + "\n");
          fw.write("Number of pools: " + nPools + "\n");
          fw.write("Working time: " + ((double) workTime)/1000 + "\n");
          fw.close();
          brGen.close();
          brRes.close();
      }
      void generateReport(String genFile, String resFile, String reportFile, int nPools, long workTime, String outdir, int nSamp) throws IOException
      {
          FileWriter fw = new FileWriter(reportFile);
          BufferedReader brGen = new BufferedReader(new FileReader(genFile));
          BufferedReader brRes = new BufferedReader(new FileReader(resFile));
          int goodReads = 0;
          int goodSamp = 0;
          int allReads = 0;
          int contSamp = 0;
          int classifiedReads = 0;
          ArrayList<Double> percsContReads = new ArrayList();
          for (int i = 0; i < nSamp; i++)
          {
              String sgen = brGen.readLine();
              String sres = brRes.readLine();
              StringTokenizer stGen = new StringTokenizer(sgen," _");
              StringTokenizer stRes = new StringTokenizer(sres," _");
              int nTok = stRes.countTokens();
              stGen.nextToken();
              stRes.nextToken();
              String realSamp = stGen.nextToken();
              stGen.nextToken();
              int realReads = Integer.parseInt(stGen.nextToken());
              allReads+=realReads;
              boolean isFound = false;
              int allReadsSamp = 0;
              int allContReadsSamp = 0;
              while (stRes.hasMoreTokens())
              {
                  String samp = stRes.nextToken();
                  int reads = Integer.parseInt(stRes.nextToken());
                  allReadsSamp += reads;
                  classifiedReads+=reads;
                  stRes.nextToken();
                  if (samp.equalsIgnoreCase(realSamp))
                  {
                      goodSamp++;
                      goodReads+=reads;
                      isFound = true;
                  }
                  else
                  {
                      allContReadsSamp+=reads;
                  }
              }
              if (isFound && (nTok > 4))
              {
                  contSamp++;
                  double d = 100*((double) allContReadsSamp/allReadsSamp);
                  percsContReads.add(d);
              }
          }
          double percGoodSamp = 100*((double) goodSamp/nSamp);
          double percContSamp = 100*((double) contSamp/nSamp);
          double percGoodReads = 100*((double) goodReads/classifiedReads);
          double percClassified = 100*((double) classifiedReads/allReads);
          double avpercsContReads = 0;
          for (Double d : percsContReads)
              avpercsContReads+=d;
          avpercsContReads/=contSamp;
          
          fw.write("Perc of recovered samp: " + percGoodSamp + "\n");
          fw.write("Perc of cont samp: " + percContSamp + "\n");
          fw.write("Perc of correctly classified reads: " + percGoodReads + "\n");
          fw.write("Av perc of wrongly classified reads in cont samp: " + avpercsContReads + "\n");
          fw.write("Number of pools: " + nPools + "\n");
          fw.write("Working time: " + ((double) workTime)/1000 + "\n");
          fw.write("Perc of classified reads: " + percClassified + "\n");
          fw.close();
          brGen.close();
          brRes.close();
      }
      void generateReport(String genFile, String resFile, String reportFile, int nPools, long workTime, String outdir, int nSamp, String genSampFolder) throws IOException
      {
          StringTokenizer st = new StringTokenizer(genFile,"_" + File.separator);
          st.nextToken();
          String testID = "_" + st.nextToken() + "_" + st.nextToken();
          st.nextToken();
          testID += "_" + st.nextToken();

          
          FileWriter fw = new FileWriter(reportFile);
          BufferedReader brGen = new BufferedReader(new FileReader(genFile));
          BufferedReader brRes = new BufferedReader(new FileReader(resFile));
          int goodReads = 0;
          int goodSamp = 0;
          int allReads = 0;
          int contSamp = 0;
          int classifiedReads = 0;
          ArrayList<Double> percsContReads = new ArrayList();
          for (int i = 0; i < nSamp; i++)
          {
              String sgen = brGen.readLine();
              String sres = brRes.readLine();
              StringTokenizer stGen = new StringTokenizer(sgen," _");
              StringTokenizer stRes = new StringTokenizer(sres," _");
              int nTok = stRes.countTokens();
              stGen.nextToken();
              stRes.nextToken();
              String realSamp = stGen.nextToken();
              stGen.nextToken();
              int realReads = Integer.parseInt(stGen.nextToken());
              allReads+=realReads;
              boolean isFound = false;
              int allReadsSamp = 0;
              int allContReadsSamp = 0;
              while (stRes.hasMoreTokens())
              {
                  String samp = stRes.nextToken();
                  int reads = Integer.parseInt(stRes.nextToken());
                  allReadsSamp += reads;
                  classifiedReads+=reads;
                  stRes.nextToken();
                  if (samp.equalsIgnoreCase(realSamp))
                  {
                      goodSamp++;
                      goodReads+=reads;
                      isFound = true;
                  }
                  else
                  {
                      allContReadsSamp+=reads;
                  }
              }
              if (isFound && (nTok > 4))
              {
                  contSamp++;
                  double d = 100*((double) allContReadsSamp/allReadsSamp);
                  percsContReads.add(d);
              }
          }
          double percGoodSamp = 100*((double) goodSamp/nSamp);
          double percContSamp = 100*((double) contSamp/nSamp);
          double percGoodReads = 100*((double) goodReads/classifiedReads);
          double percClassified = 100*((double) classifiedReads/allReads);
          double avpercsContReads = 0;
          for (Double d : percsContReads)
              avpercsContReads+=d;
          avpercsContReads/=contSamp;
          
          // Root mean square error of frequencies
          double rmse = 0;
          int nReadsPooling = 0;
          brGen = new BufferedReader(new FileReader(genFile));
          for (int i = 1; i <= nSamp; i++)
          {
              String s = brGen.readLine();
              st = new StringTokenizer(s," ");
              st.nextToken();
              String sampGenFile = st.nextToken();
              DataSet ds_orig = null;
              DataSet ds_infer = null;
              try
              {
                  ds_orig = new DataSet(genSampFolder + File.separator + sampGenFile,"ET");
                  ds_infer = new DataSet(outdir + File.separator + "Samp" + i + testID + ".fas_" + i + ".fas","ET");
              }
              catch(Exception e)
              {
                  System.err.println(genSampFolder + File.separator + sampGenFile);
                  System.err.println(outdir + File.separator + "Samp" + i + testID + ".fas_" + i + ".fas");
              }
              int nOrig = ds_orig.getTotalNReads();
              int nInfer = ds_infer.getTotalNReads();
              for (Read r : ds_infer.reads)
              {
                  nReadsPooling++;
                  double inferFreq = (double) r.getFreq();
                  double origFreq = (double) ds_orig.getFrequency(r);
                  double diff = 100*(inferFreq/nInfer - origFreq/nOrig);
                  rmse += diff*diff;
              }                         
          }
          rmse = Math.sqrt(rmse/nReadsPooling);
          
          fw.write("Perc of recovered samp: " + percGoodSamp + "\n");
          fw.write("Perc of cont samp: " + percContSamp + "\n");
          fw.write("Perc of correctly classified reads: " + percGoodReads + "\n");
          fw.write("Av perc of wrongly classified reads in cont samp: " + avpercsContReads + "\n");
          fw.write("Number of pools: " + nPools + "\n");
          fw.write("Working time: " + ((double) workTime)/1000 + "\n");
          fw.write("Perc of classified reads: " + percClassified + "\n");
          fw.write("Root mean square error: " + rmse + "\n");
          fw.close();
          brGen.close();
          brRes.close();
      }
      void generateReportReseq(String genFile, String resFile, String reportFile, int nPools, long workTime, String outdir, int nSamp, String genSampFolder) throws IOException
      {
          
          File f = new File(resFile);
          if (!f.exists())
              return;
          
          StringTokenizer st = new StringTokenizer(genFile,"_" + File.separator);
          st.nextToken();
          String testID = "_" + st.nextToken() + "_" + st.nextToken() + "_" + st.nextToken();

          
          FileWriter fw = new FileWriter(reportFile);
          BufferedReader brGen = new BufferedReader(new FileReader(genFile));
          BufferedReader brRes = new BufferedReader(new FileReader(resFile));
          int goodReads = 0;
          int goodSamp = 0;
          int allReads = 0;
          int contSamp = 0;
          int classifiedReads = 0;
          ArrayList<Double> percsContReads = new ArrayList();
          for (int i = 0; i < nSamp; i++)
          {
              String sgen = brGen.readLine();
              String sres = brRes.readLine();
              StringTokenizer stGen = new StringTokenizer(sgen," _");
              StringTokenizer stRes = new StringTokenizer(sres," _");
              int nTok = stRes.countTokens();
              stGen.nextToken();
              stRes.nextToken();
              String realSamp = stGen.nextToken();
              stGen.nextToken();
              int realReads = Integer.parseInt(stGen.nextToken());
              allReads+=realReads;
              boolean isFound = false;
              int allReadsSamp = 0;
              int allContReadsSamp = 0;
              while (stRes.hasMoreTokens())
              {
                  String samp = stRes.nextToken();
                  int reads = Integer.parseInt(stRes.nextToken());
                  allReadsSamp += reads;
                  classifiedReads+=reads;
                  stRes.nextToken();
                  if (samp.equalsIgnoreCase(realSamp))
                  {
                      goodSamp++;
                      goodReads+=reads;
                      isFound = true;
                  }
                  else
                  {
                      allContReadsSamp+=reads;
                  }
              }
              if (isFound && (nTok > 4))
              {
                  contSamp++;
                  double d = 100*((double) allContReadsSamp/allReadsSamp);
                  percsContReads.add(d);
              }
          }
          double percGoodSamp = 100*((double) goodSamp/nSamp);
          double percContSamp = 100*((double) contSamp/nSamp);
          double percGoodReads = 100*((double) goodReads/classifiedReads);
          double percClassified = 100*((double) classifiedReads/allReads);
          double avpercsContReads = 0;
          for (Double d : percsContReads)
              avpercsContReads+=d;
          avpercsContReads/=contSamp;
          
          // Root mean square error of frequencies
          double rmse = 0;
          int nReadsPooling = 0;
          brGen = new BufferedReader(new FileReader(genFile));
          for (int i = 1; i <= nSamp; i++)
          {
              String s = brGen.readLine();
              st = new StringTokenizer(s," ");
              st.nextToken();
              String sampGenFile = st.nextToken();
              DataSet ds_orig = null;
              DataSet ds_infer = null;
              try
              {
                  ds_orig = new DataSet(genSampFolder + File.separator + sampGenFile,"ET");
                  ds_infer = new DataSet(outdir + File.separator + "Samp" + i + testID + "_reseq.fas_" + i + ".fas","ET");
              }
              catch(Exception e)
              {
                  System.err.println(genSampFolder + File.separator + sampGenFile);
                  System.err.println(outdir + File.separator + "Samp" + i + testID + "_reseq.fas_" + i + ".fas");
              }
              int nOrig = ds_orig.getTotalNReads();
              int nInfer = ds_infer.getTotalNReads();
              for (Read r : ds_infer.reads)
              {
                  nReadsPooling++;
                  double inferFreq = (double) r.getFreq();
                  double origFreq = (double) ds_orig.getFrequency(r);
                  double diff = 100*(inferFreq/nInfer - origFreq/nOrig);
                  rmse += diff*diff;
              }                         
          }
          rmse = Math.sqrt(rmse/nReadsPooling);
          
          fw.write("Perc of recovered samp: " + percGoodSamp + "\n");
          fw.write("Perc of cont samp: " + percContSamp + "\n");
          fw.write("Perc of correctly classified reads: " + percGoodReads + "\n");
          fw.write("Av perc of wrongly classified reads in cont samp: " + avpercsContReads + "\n");
          fw.write("Number of pools: " + nPools + "\n");
          fw.write("Working time: " + ((double) workTime)/1000 + "\n");
          fw.write("Perc of classified reads: " + percClassified + "\n");
          fw.write("Root mean square error: " + rmse + "\n");
          fw.close();
          brGen.close();
          brRes.close();
      }
      ArrayList<ArrayList<Integer>> retrievePartitions(String addr)
      {
          ArrayList<ArrayList<Integer>> ar = new ArrayList();
          
          File folder = new File(addr);
          System.out.println(folder.exists());
          File[] list_files = folder.listFiles();
          
          int nPools = 0;
          for (int i = 0; i < list_files.length; i++)
              if (list_files[i].getName().startsWith("SimPool"))
                  nPools++;
          nPools /= 2;
          
          for (int i = 0; i < nPools; i++)
          {
              ArrayList<Integer> part = new ArrayList();
              for (int j = 0; j < list_files.length; j++)
                  if (list_files[j].getName().startsWith("SimPool" + i + ".fas_unique.fas"))
                  {
                      String name = list_files[j].getName();
                      StringTokenizer st = new StringTokenizer(name,"._");
                      for (int k = 0; k < 4; k++)
                          st.nextToken();
                      String s = st.nextToken();
                      while (!s.equalsIgnoreCase("fas"))
                      {
                          part.add(Integer.parseInt(s));
                          s = st.nextToken();
                      }
                      break;
                  }
              ar.add(part);
          }
          
          return ar;
      }
      ArrayList<Pool> reSequencing(ArrayList<Pool> recovSamp) throws IOException
      {
          ArrayList<Pool> reseqSamp = new ArrayList();
          for (int i = 0; i < recovSamp.size(); i++)
          {
              if (recovSamp.get(i).isEmpty())
              {
                  String addr = this.addrSamples + File.separator + this.sampFileNames.get(i);
                  DataSet ds = new DataSet(addr,"ET");
                  ArrayList<Integer> ind = new ArrayList();
                  ind.add(i+1);
                  Pool p = new Pool(ds,ind); 
                  p.fileName = "ra" + i + ".fas";
                  reseqSamp.add(p);
              }
          }
          return reseqSamp;    
      }
      ArrayList<Pool> reSequencing(ArrayList<Pool> recovSamp, int minReads) throws IOException
      {
          ArrayList<Pool> reseqSamp = new ArrayList();
          for (int i = 0; i < recovSamp.size(); i++)
          {
              if (recovSamp.get(i).ds.getTotalNReads() <= minReads)
              {
                  String addr = this.addrSamples + File.separator + this.sampFileNames.get(i);
                  DataSet ds = new DataSet(addr,"ET");
                  ArrayList<Integer> ind = new ArrayList();
                  ind.add(i+1);
                  Pool p = new Pool(ds,ind); 
                  p.fileName = "ra" + i + ".fas";
                  reseqSamp.add(p);
              }
          }
          return reseqSamp;    
      }
      void readSampFileNames(String genlogfile_prev) throws FileNotFoundException, IOException
      {
          BufferedReader br = new BufferedReader(new FileReader(genlogfile_prev));
          sampFileNames = new ArrayList();
          String s = br.readLine();
          while (s!=null)
          {
              StringTokenizer st = new StringTokenizer(s," ");
              st.nextToken();
              sampFileNames.add(st.nextToken());
              s = br.readLine();
          }
      }
}
