package Pooling;


import ErrorCorrection.Read;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kki8
 */
public class ReadsPair {
    Read r1;
    Read r2;
    double dist;
    ReadsPair(Read x, Read y) throws IOException
    {
        r1 = x;
        r2 = y;
        dist = r1.calcEditDistAbsAlign(r2, 15, 6);
    }
        
}
