package com.signlanguage.rest;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by marcos on 16/03/18.
 */

public class RestApi {
    private static final String ENDPOINT = "http://10.0.2.2:9090/api/signlanguage/candidates/";
    private static ClientService service;

    public interface ClientService {
        @GET("reg")
        Call <ServerJsonResponse> registerUser(@Query("em") String email, @Query("na") String name, @Query("pa") String pass, @Query("t") String tlf, @Query("ad") String address, @Query("co") String county);
        @GET("logBackend")
        Call <ServerJsonResponse> logUser(@Query("em") String email, @Query("pa") String pass);
        @GET("restoreP")
        Call <ServerJsonResponse> restorePass(@Query("em") String email);
        @GET("setIdToken")
        Call <ServerJsonResponse> sendIdToken(@Query("token") String token);

    }

    public RestApi() {

        if(service==null)
        {
            OkHttpClient httpClient = new OkHttpClient();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            //httpClient.addInterceptor(logging);


            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create()).client(httpClient);
            Retrofit retrofit = builder.build();
            service = retrofit.create(ClientService.class);
        }
    }

    public ClientService getService() {
        return service;
    }
}
