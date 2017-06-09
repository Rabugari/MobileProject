package br.com.caelum.ichat.module;

import android.app.Application;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import br.com.caelum.ichat.service.ChatService;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by massao on 13/03/17.
 */

//Utilizando o framework Dagger (para injeção de depedência), estou dizendo que essa classe é um
    //modulo
@Module
public class ChatModule {

    private ChatService chatService;
    private Application app;

    public ChatModule(Application app) {
        this.app = app;
    }

    /**
     * Declaro Provides para o método entrar na injeção de depedencias
     * @return
     */
    @Provides
    public ChatService getChatService(){
        Retrofit retrofit = new Retrofit.Builder()
                // Altere para o seu IP
                .baseUrl("http://192.168.0.17:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatService = retrofit.create(ChatService.class);
        return chatService;
    }

    @Provides
    public Picasso getPicasso(){
        Picasso picasso = new Picasso.Builder(app).build();
        return  picasso;
    }

    @Provides
    public EventBus getEventBus(){
        return EventBus.builder().build();
    }

    @Provides
    public InputMethodManager getInputMethodManager(){
        InputMethodManager manager = (InputMethodManager) app.getSystemService(Context.INPUT_METHOD_SERVICE);
        return manager;
    }
}
