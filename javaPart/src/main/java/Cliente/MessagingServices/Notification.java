package Cliente.MessagingServices;

import Logic.Encomenda;
import Logic.Producao;

public class Notification {

    private NotificationType type;
    private Encomenda encomenda;
    private Producao producao;

    public Notification(Producao producao) {
        type = NotificationType.OFERTA_PRODUCAO;
        this.producao = producao;
    }

    public Notification(Encomenda encomenda) {
        type = NotificationType.RESULTADO_NEGOCIACAO;
        this.encomenda = encomenda;
    }
    public enum NotificationType {
        OFERTA_PRODUCAO,
        RESULTADO_NEGOCIACAO
    }

    public NotificationType getType() {
        return type;
    }

    public Encomenda getEncomenda() {
        return encomenda;
    }

    public Producao getProducao() {
        return producao;
    }
}
