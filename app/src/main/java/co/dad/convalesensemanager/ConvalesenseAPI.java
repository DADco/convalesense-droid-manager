package co.dad.convalesensemanager;

import java.util.List;

import co.dad.convalesensemanager.model.Plan;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by adrienviolet on 26/01/2017.
 */

public class ConvalesenseAPI {

    private static ConvalesenseAPI instance;
    private ConvalesenseServices services;

    public static ConvalesenseAPI getInstance() {
        if (instance == null) {
            instance = new ConvalesenseAPI();
        }

        return instance;
    }

    private ConvalesenseAPI() {

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClientBuilder.addInterceptor(loggingInterceptor);

        OkHttpClient okClient = okHttpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient)
                .baseUrl("http://172.16.8.24:8000/")
                .build();

        services = retrofit.create(ConvalesenseServices.class);
    }

    public ConvalesenseServices getServices() {
        return services;
    }


    public interface ConvalesenseServices {

        @GET("api/plans?format=json")
        Call<List<Plan>> getPlans();

        @GET("api/plans/{planId}?format=json")
        Call<Plan> getPlan(@Path("planId") int planId);
    }
}
