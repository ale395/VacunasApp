package com.vacunasdomaincompany.vacunasapp;

import android.util.JsonReader;
import android.util.Log;

import com.vacunasdomaincompany.vacunasapp.objetos.web.service.Usuario;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Admin on 19/10/2017.
 */

public class HttpManager {

    public static String getData(String uri){
        BufferedReader reader = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lina;

            while((lina = reader.readLine()) != null){
                stringBuilder.append(lina + "\n");
            }
            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public static Usuario getUsuario(String uri){
        BufferedReader reader = null;
        Usuario usuario;

        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lina = reader.readLine();

            if (lina != null){
                JSONObject jsonUsuario = XML.toJSONObject(lina);
                JSONObject jsonDetaUsu = (JSONObject) jsonUsuario.get("usuario");
                usuario = new Usuario(jsonDetaUsu.getInt("id"), jsonDetaUsu.getString("nombre"), jsonDetaUsu.getString("correo"));
                return usuario;
            }else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}