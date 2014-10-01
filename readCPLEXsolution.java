package Pooling;


import Pooling.TestSet;
import Pooling.DoubleGraph;
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
public class readCPLEXsolution {
     public static void main(String[] args) throws IOException, Exception
     {
         String addr = "sol0.txt";
         TestSet ts = new TestSet();
         DoubleGraph g = new DoubleGraph("Pajek_Nonrel.net");
//         g.printToConsole();
         ts.readFromCplexFile(addr);
         ts.printToConsoleCheckFeasibility1(g);
//         ts.printToConsole();
         System.out.println("NTests2: " + ts.getNTests());
     }
}
