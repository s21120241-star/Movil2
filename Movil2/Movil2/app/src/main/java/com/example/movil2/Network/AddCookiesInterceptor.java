package com.example.movil2.Network;

import android.content.Context;
import android.preference.PreferenceManager;
import java.io.IOException;
import java.util.HashSet;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    public static final String PREF_COOKIES = "PREF_COOKIES";
    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(PREF_COOKIES, new HashSet<String>());

        // Unimos todas las cookies en una sola cadena separada por "; "
        StringBuilder sb = new StringBuilder();
        for (String cookie : preferences) {
            if (sb.length() > 0) sb.append("; ");
            // Extraer solo la parte "nombre=valor" antes del primer ";"
            sb.append(cookie.split(";")[0]);
        }

        if (sb.length() > 0) {
            builder.header("Cookie", sb.toString());
        }

        return chain.proceed(builder.build());
    }
}