package co.dad.convalesensemanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import co.dad.convalesensemanager.model.Plan;
import co.dad.convalesensemanager.model.Result;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okClient)
                .baseUrl("http://172.16.8.24:8000/")
                .build();

        services = retrofit.create(ConvalesenseServices.class);
    }

    public ConvalesenseServices getServices() {
        return services;
    }


    public interface ConvalesenseServices {

        @GET("api/plans?android&format=json")
        Call<List<Plan>> getPlans();

        @GET("api/plans/{planId}?android&format=json")
        Call<Plan> getPlan(@Path("planId") int planId);

        @POST("api/exercise-records/")
        Call<ResponseBody> sendScore(@Body Result result);
    }
}
