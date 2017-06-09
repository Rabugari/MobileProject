package br.com.caelum.ichat.event;

import br.com.caelum.ichat.modelo.Mensagem;

/**
 * Created by massao on 18/03/17.
 */

public class MensagemEvent {

    private Mensagem mensagem;

    public MensagemEvent() {
    }

    public MensagemEvent(Mensagem mensagem) {

        this.mensagem = mensagem;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }
}
