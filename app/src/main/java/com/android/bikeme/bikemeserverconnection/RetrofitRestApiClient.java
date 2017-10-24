package com.android.bikeme.bikemeserverconnection;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Daniel on 19/03/2017.
 */
public class RetrofitRestApiClient {

    private static RetrofitRestApiClient retrofitRestApiClient;
    private EndPointsApi endPointsApi;

    private RetrofitRestApiClient()
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Api-Key", RestApiConstants.API_KEY_PRODUCTION);

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(60,TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestApiConstants.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        endPointsApi = retrofit.create(EndPointsApi.class);
    }

    public static RetrofitRestApiClient getInstance()
    {
        if (retrofitRestApiClient == null)
        {
            retrofitRestApiClient = new RetrofitRestApiClient();
        }
        return retrofitRestApiClient;
    }

    public EndPointsApi getEndPointsApi()
    {
        return endPointsApi;
    }
}