package com.signlanguage.rest;

/**
 * Created by marcos on 19/03/18.
 */
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
public class RestJobsApi {

    private static final String ENDPOINT = "http://10.0.2.2:9090/api/signlanguage/jobs/";
    private static RestJobsApi.ClientService service;

    public interface ClientService {

        @GET("getCandidateJobs")
        Call <ServerJsonResponse> getCandidateJobs(@Query("id") String id);
        @GET("applyJob")
        Call <ServerJsonResponse> applyJob(@Query("idUser") String idUser, @Query("idJob") String idJob);

    }

    public RestJobsApi() {

        if(service==null)
        {
            OkHttpClient httpClient = new OkHttpClient();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            //httpClient.addInterceptor(logging);


            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create()).client(httpClient);
            Retrofit retrofit = builder.build();
            service = retrofit.create(RestJobsApi.ClientService.class);
        }
    }

    public RestJobsApi.ClientService getService() {
        return service;
    }
}
