package ru.stivosha.currencyconverter;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyConverter {

    private String apiKey;
    private OkHttpClient client;

    public CurrencyConverter(String apiKey) {
        client = new OkHttpClient();
        this.apiKey = apiKey;
    }

    public String convert(Currency from, Currency to){
        Request request = new Request.Builder()
                .url(getUrlForConvert(from, to))
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            return getNumberFromResponse(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
    }

    private String getNumberFromResponse(String response){
        return response.substring(0, response.length()-1).split(":")[1];
    }

    private String getUrlForConvert(Currency from, Currency to){
        return "https://free.currconv.com/api/v7/convert?q=" + from + "_" + to + "&compact=ultra&apiKey=" + apiKey;
    }

}
