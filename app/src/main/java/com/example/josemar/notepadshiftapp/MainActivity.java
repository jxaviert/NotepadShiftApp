package com.example.josemar.notepadshiftapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.josemar.notepadshiftapp.api.NotaAPI;
import com.example.josemar.notepadshiftapp.model.Nota;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etTitulo;
    private EditText etTexto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etTexto = (EditText) findViewById(R.id.etTexto);


        Stetho.initializeWithDefaults(this);
    }

    public void pesquisar(View v){
        NotaAPI api = getRetrofit().create(NotaAPI.class);
        api.buscarNota(etTitulo.getText().toString())
                .enqueue(new Callback<Nota>() {
                    @Override
                    public void onResponse(Call<Nota> call, Response<Nota> response) {
                        if(response.isSuccessful()){
                            etTexto.setText(response.body().getDescricao());
                        }
                    }

                    @Override
                    public void onFailure(Call<Nota> call, Throwable t) {
                        Toast.makeText(MainActivity.this,"Erro",Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void limpar(View v){
        etTexto.setText("");
        etTitulo.setText("");
    }

    public void salvar(View v){

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this,"","Aguarde! Estamos gravando os dados",true);
        dialog.show();

        NotaAPI api = getRetrofit().create(NotaAPI.class);

        Nota nota = new Nota();

        nota.setTitulo(etTitulo.getText().toString());
        nota.setDescricao(etTexto.getText().toString());

        api.salvar(nota).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MainActivity.this,
                        "Dado gravado com sucesso",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


    private Retrofit getRetrofit(){

        OkHttpClient client = new OkHttpClient.Builder().
                addNetworkInterceptor(new StethoInterceptor()).
                build();

        return new Retrofit.Builder()
                .baseUrl("https://notepadcloudjosemar.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
