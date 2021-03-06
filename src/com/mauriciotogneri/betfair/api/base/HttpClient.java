package com.mauriciotogneri.betfair.api.base;

import com.mauriciotogneri.betfair.Constants.Debug;
import com.mauriciotogneri.betfair.Constants.Debug.Http;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import okio.Buffer;

public class HttpClient
{
    public final OkHttpClient client;

    public HttpClient(OkHttpClient client)
    {
        this.client = client;
    }

    public static HttpClient getDefault()
    {
        OkHttpClient client = new OkHttpClient();

        if (Debug.ENABLE_LOGS)
        {
            client.interceptors().add(new Interceptor()
            {
                @Override
                public Response intercept(Chain chain) throws IOException
                {
                    Request request = chain.request();

                    long startTime = System.nanoTime();

                    if (Http.PRINT_URL)
                    {
                        System.out.println(String.format(">>> %s\n", request.url()));
                    }

                    if (Http.PRINT_HEADERS)
                    {
                        System.out.println(String.format("%s", request.headers()));
                    }

                    if (Http.PRINT_PARAMETERS)
                    {
                        Buffer buffer = new Buffer();
                        RequestBody requestBody = request.body();

                        if (requestBody != null)
                        {
                            requestBody.writeTo(buffer);

                            System.out.println(String.format("%s\n", buffer.readUtf8()));
                        }
                    }

                    Response response = chain.proceed(request);

                    long endTime = System.nanoTime();

                    if (Http.PRINT_URL)
                    {
                        System.out.println(String.format("<<< %s (%.1f ms)\n", response.request().url(), (endTime - startTime) / 1e6d));
                    }

                    if (Http.PRINT_HEADERS)
                    {
                        System.out.println(String.format("%s", response.headers()));
                    }

                    return response;
                }
            });
        }

        return new HttpClient(client);
    }
}