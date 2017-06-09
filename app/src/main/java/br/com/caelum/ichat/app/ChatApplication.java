package br.com.caelum.ichat.app;

import android.app.Application;

import br.com.caelum.ichat.component.ChatComponent;
import br.com.caelum.ichat.component.DaggerChatComponent;
import br.com.caelum.ichat.module.ChatModule;

/**
 * Created by massao on 13/03/17.
 */

public class ChatApplication extends Application {

    private ChatComponent component;

    public void onCreate(){
        super.onCreate();
        component = (ChatComponent) DaggerChatComponent.builder()
                .chatModule(new ChatModule(this))
                .build();
    }

    public ChatComponent getComponent(){
        return component;
    }
}
