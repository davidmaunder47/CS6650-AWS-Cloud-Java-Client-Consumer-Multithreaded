import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;

 class Something {
     public synchronized void  run () throws ApiException {

         long sTime = System.currentTimeMillis();
         String basePath = "http://localhost:8081/part2warexploded";
         String basePath2 = "http://204.236.221.0:8080/Part2_war";

         Integer resortID = 56; // Integer | ID of the resort
         String seasonID = "56"; // String | ID of the season
         String dayID = "56"; // String | ID of the day
         Integer skierID = 56; // Integer | ID of the skier;

         SkiersApi apiInstance = new SkiersApi();
         ApiClient client = apiInstance.getApiClient();
         client.setBasePath(basePath);

         try {
             ApiResponse res = apiInstance.getSkierDayVerticalWithHttpInfo(resortID, seasonID, dayID, skierID);
             //System.out.println(res.getStatusCode());
             Integer verticalResult = apiInstance.getSkierDayVertical(resortID, seasonID, dayID, skierID);
             //System.out.println(verticalResult);
         } catch (ApiException e){
             System.err.println("Exception when calling SkiersApi#getSkierDayVertical");
             e.printStackTrace();
         }

         long endtime = System.currentTimeMillis();
         long totaltime = endtime - sTime;

        //System.out.printf("%d ", totaltime);



     }
}

public class JavaRunnable {

    public static void main(String[] args) {

        // Example for the GET


        long starttime2 = System.currentTimeMillis();

        final Something threadObject = new Something();
        for ( int i = 0; i < 2000; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1; i++) {
                        try {
                            threadObject.run();
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
            thread.start();
        }

        long endtime2 = System.currentTimeMillis();

        long walltime = endtime2 - starttime2;

        System.out.printf("total time in MS is: %d\n", walltime);

    }
}
