package catalogo.Representations;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Utilizador {
    private String nome;
    private String password;

    public Utilizador(){}

    public Utilizador(Utilizador utilizador){
        this.nome = utilizador.getNome();
        this.password =utilizador.getPassword();
    }

    @JsonProperty
    public String getNome(){
        return nome;
    }

    @JsonProperty
    public String getPassword(){
        return password;
    }
}
