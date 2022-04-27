import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;

import java.lang.reflect.Array;

class SkiersApiExample {

    public static void main(String[] args) {


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for ( int i =0; i < 1000; i++) {
                    String basePath = "http://localhost:8081/part2warexploded/skiers/12/seasons/2019/days/1/skiers/123.";
                    String basePath2 = "http://204.236.221.0:8080/Part2_war";
                    SkiersApi apiInstance = new SkiersApi();
                    ApiClient client = apiInstance.getApiClient();
                    client.setBasePath(basePath2);
                    long diff [] =  new long[1000];
                    long total = 0;

                    // Example for the GET
                    Integer resortID = 56; // Integer | ID of the resort
                    String seasonID = "56"; // String | ID of the season
                    String dayID = "56"; // String | ID of the day
                    Integer skierID = 56; // Integer | ID of the skier
                    long start = System.currentTimeMillis();
                    try {
                        //System.out.println("test");
                        ApiResponse res = apiInstance.getSkierDayVerticalWithHttpInfo(resortID, seasonID, dayID, skierID);
                        //System.out.println(res.getStatusCode());
                        Integer verticalResult = apiInstance.getSkierDayVertical(resortID, seasonID, dayID, skierID);
                        //System.out.println(verticalResult);
                    } catch (ApiException e) {
                        System.err.println("Exception when calling SkiersApi#getSkierDayVertical");
                        e.printStackTrace();
                    }
                    long end = System.currentTimeMillis();
                    long difference = end - start;
                    diff[i] = difference;

                    //total += difference;

                }


            }
        });


    }
}