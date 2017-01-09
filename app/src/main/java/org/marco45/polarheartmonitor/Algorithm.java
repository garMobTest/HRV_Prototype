package org.marco45.polarheartmonitor;

import java.util.ArrayList;

/**
 * Created by garrypolykoff on 1/5/17.
 */

public class Algorithm {


    /* these would be taken from some database based on a person age/sex, but for now
     we just hardcode them
     **/

    private static final double LN_MEAN = 3.02;
    private static final double LN_VAR = 0.69;
    private static final double HRV_MEAN = 61.83;
    private static final double HRV_VAR = 10.59;


    public static int calculateHRV_Score(ArrayList<Integer> hrvRaw_array) {


        double score = 0.0;
        double sum = 0.0;

        System.out.println("Array size: " + hrvRaw_array.size());


        for (int i = 0; i < hrvRaw_array.size() -1; i++) {

          //  System.out.println("Member: " + hrvRaw_array.get(i));

            sum += Math.pow((hrvRaw_array.get(i + 1) - hrvRaw_array.get(i)), 2);
        }


            // divide sum by number of measurements and take square root of it.
         //   score = sqrt(sum / (hrvRaw_array.length -1));
        // Log.e("Score:", score + "");

// take natural logarithm of the number above – this is is the result.
            score = Math.log10(Math.sqrt(sum / (hrvRaw_array.size() -1)));
        System.out.println("Inside Algorithm Class LN(rmssd): " + score );

        return extrapolateScore(score, LN_MEAN,LN_VAR, HRV_MEAN, HRV_VAR);


        }


  private static int extrapolateScore (double rmssd, double ln_mean, double ln_variance, double score_mean, double score_variance) {


      int result = 0;


      double factor = (Math.abs(ln_mean - rmssd))/ln_variance;



      if (rmssd < ln_mean) {


          result = (int)(score_mean - score_variance * factor);
      } else {


          result = (int)(score_mean + score_variance * factor);
      }

      return result;

  }





}
