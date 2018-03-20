package br.com.whatsappandroid.cursoandroid.whatsapp.model;

/**
 * Created by rafael on 01/03/18.
 */

public class Conversa {
    private String idusuario;
    private String nome;
    private String mensagem;

    public Conversa() {
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
