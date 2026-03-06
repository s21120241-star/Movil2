package com.example.movil2.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;
    public ReceivedCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            // Recuperamos las cookies ya guardadas para añadir las nuevas (ID de sesión + Seguridad)
            Set<String> cookies = prefs.getStringSet("PREF_COOKIES", new HashSet<String>());
            HashSet<String> newCookies = new HashSet<>(cookies);

            for (String header : originalResponse.headers("Set-Cookie")) {
                newCookies.add(header);
            }

            prefs.edit().putStringSet("PREF_COOKIES", newCookies).apply();
        }

        return originalResponse;
    }
}