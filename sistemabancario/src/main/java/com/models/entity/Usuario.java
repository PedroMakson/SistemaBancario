package com.models.entity;

import java.util.Date;

public class Usuario {
    private String cpf;
    private String nome;
    private String rg;
    private double renda;
    private String telefone;
    private String email;
    private Date dataNascimento;
    private String cep;
    private String rua;
    private int numeroDaCasa;
    private String bairro;
    private String cidade;
    private String uf;

    // Construtor com todos os atributos
    public Usuario(String cpf, String nome, String rg, double renda, String telefone, String email, Date dataNascimento,
            String cep, String rua, int numeroDaCasa, String bairro, String cidade, String uf) {
        this.cpf = cpf;
        this.nome = nome;
        this.rg = rg;
        this.renda = renda;
        this.telefone = telefone;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.cep = cep;
        this.rua = rua;
        this.numeroDaCasa = numeroDaCasa;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
    }

    // Métodos getters e setters para acessar e modificar os atributos
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public int getNumeroDaCasa() {
        return numeroDaCasa;
    }

    public void setNumeroDaCasa(int numeroDaCasa) {
        this.numeroDaCasa = numeroDaCasa;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public double getRenda() {
        return renda;
    }

    public void setRenda(double renda) {
        this.renda = renda;
    }

}