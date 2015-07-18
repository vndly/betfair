package com.mauriciotogneri.kernel;

import com.google.gson.Gson;
import com.mauriciotogneri.kernel.api.Login;
import com.mauriciotogneri.kernel.api.Login.LoginResponse;
import com.squareup.okhttp.OkHttpClient;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Login login = new Login(client, gson);
        LoginResponse loginResponse = login.execute(args[0], args[1], args[2]);
    }
}