package catalogo.Representations;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Periodo {
    private LocalDateTime dataInicial;
    private LocalDateTime dataFinal;

    public Periodo(){}

    public Periodo(LocalDateTime dataInicial, LocalDateTime dataFinal){
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
    }

    @JsonProperty
    public LocalDateTime getDataInicial(){
        return dataInicial;
    }

    @JsonProperty
    public LocalDateTime getDataFinal(){
        return dataFinal;
    }
}
