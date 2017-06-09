package br.com.caelum.ichat.component;

import br.com.caelum.ichat.activity.MainActivity;
import br.com.caelum.ichat.adapter.MensagemAdapter;
import br.com.caelum.ichat.module.ChatModule;
import dagger.Component;

/**
 * Created by massao on 13/03/17.
 *
 * Estou dizendo que o todas as classes dentro do arquivo (mainactivity), pode usar o module
 */
@Component(modules = ChatModule.class)
public interface ChatComponent {

    void inject(MainActivity activity);
    void inject(MensagemAdapter adapter);

}
