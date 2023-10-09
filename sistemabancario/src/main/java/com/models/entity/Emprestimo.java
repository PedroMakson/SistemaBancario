package com.models.entity;

import java.sql.Date;
import java.time.LocalDate;

public class Emprestimo {

    private Conta conta;
    private String documento;
    private LocalDate dataEmissao;
    private double valorTotal;
    private int quantidadeParcelas;
    private double valorJuros;
    private Date dataVencimento;
    private int parcelaAtual;
    private double valorParcela;
    private double valorRecebido;
    private boolean statusParcela;

    public Emprestimo(Conta conta, double valorTotal, int quantidadeParcelas, double valorJuros) {
        this.conta = conta;
        this.documento = "";
        this.dataEmissao = LocalDate.now();
        this.valorTotal = valorTotal;
        this.quantidadeParcelas = quantidadeParcelas;
        this.valorJuros = valorJuros;
        this.dataVencimento = java.sql.Date.valueOf(dataEmissao);
        this.parcelaAtual = 0;
        this.valorParcela = valorTotal / quantidadeParcelas;
        this.valorRecebido = 0;
        this.statusParcela = false;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public int getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(int quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public double getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(double valorJuros) {
        this.valorJuros = valorJuros;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public int getParcelaAtual() {
        return parcelaAtual;
    }

    public void setParcelaAtual(int parcelaAtual) {
        this.parcelaAtual = parcelaAtual;
    }

    public double getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(double valorParcela) {
        this.valorParcela = valorParcela;
    }

    public double getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(double valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    public boolean isStatusParcela() {
        return statusParcela;
    }

    public void setStatusParcela(boolean statusParcela) {
        this.statusParcela = statusParcela;
    }

}