package agenda.app.com.br.service;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by massao on 26/02/17.
 */

public class WebClient {

    public String post(String json){
        String endereco = "https://www.caelum.com.br/mobile";
        return realizaConexao(json, endereco);
    }

    public void insere(String json) {
        String endereco = "http://192.168.0.14:8080/api/aluno";
        realizaConexao(json, endereco);
    }

    @Nullable
    private String realizaConexao(String json, String endereco) {
        try {
            //se fosse get, colocaria os valores/parametro na url
            URL url = new URL(endereco);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //estou avisando que estou enviando um json
            connection.setRequestProperty("Content-type", "application/json");
            //espero de volta um json tb
            connection.setRequestProperty("Accept", "application/json");

            //executa o post, true > sim estou fazendp uma saida, um post
            connection.setDoOutput(true);

            PrintStream output = new PrintStream(connection.getOutputStream());
            output.println(json);

            //tentando estabelecer a conexão
            connection.connect();

            //para ler a resposta
            Scanner scanner = new Scanner(connection.getInputStream());
            String resposta = scanner.next();

            return resposta;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
