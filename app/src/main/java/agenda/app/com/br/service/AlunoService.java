package agenda.app.com.br.service;

import java.util.List;

import agenda.app.com.br.dto.AlunoSync;
import agenda.app.com.br.modelo.Aluno;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Massao on 18/07/2017.
 */

public interface AlunoService {

    /**
     * o @Body diz que o objeto aluno irá no corpo da requisição
     * @param aluno
     * @return
     */
    @POST("aluno")
    Call<Void> insere(@Body  Aluno aluno);

    @GET("aluno")
    Call<AlunoSync> lista();

    //Path para colocar o parametro na url
    @DELETE("aluno/{id}")
    Call<Void> deleta(@Path("id") String id);

    @GET("aluno/diff")
    Call<AlunoSync> novos(@Header("datahora") String versao);

    @PUT("aluno/lista")
    Call<AlunoSync> atualiza(@Body List<Aluno> alunos);
}
