package Logic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Utilizador {
    private String nome;
    private String password;
    private String tipo;

    public Utilizador(){}

    public Utilizador(Utilizador utilizador){
        this.nome = utilizador.getNome();
        this.password =utilizador.getPassword();
        this.tipo = utilizador.getTipo();
    }

    public Utilizador(String nome, String password, String tipo){
        this.nome = nome;
        this.password = password;
        this.tipo = tipo;
    }

    @JsonProperty
    public String getNome(){
        return nome;
    }

    @JsonProperty
    public String getPassword(){
        return password;
    }

    @JsonProperty
    public String getTipo() {
        return tipo;
    }
}
