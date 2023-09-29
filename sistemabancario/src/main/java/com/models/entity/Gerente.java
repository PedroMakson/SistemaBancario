package com.models.entity;

import java.util.Date;

public class Gerente extends Usuario {

    private String cargo;
    private double salario;

    public Gerente(String cpf, String nome, String rg, String telefone, String email, Date dataNascimento, String cep,
            String rua, int numeroDaCasa, String bairro, String cidade, String uf, String cargo, double salario) {
        super(cpf, nome, rg, salario, email, cargo, dataNascimento, cep, rua, numeroDaCasa, bairro, cidade, uf);
        this.cargo = cargo;
        this.salario = salario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }
    
}
