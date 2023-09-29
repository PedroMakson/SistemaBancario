package com.models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.models.entity.Conta;
import com.models.entity.Usuario;

public class ContaDAO {

    private Connection conexao;

    public ContaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    // Método para inserir um novo usuário no banco de dados
    public void inserirConta(Usuario usuario, Conta conta) throws SQLException {
        String sql = "INSERT INTO Conta (cpf, tipoconta, agencia, conta, saldo, senha, statusConta) VALUES (?, ?, ?, ?, ?, ?, ?)";

        int maiorAgencia = obterMaiorAgencia();
        int maiorConta = obterMaiorConta();

        if (maiorAgencia == 0) {
            maiorAgencia = 999;
        }
        maiorAgencia++;

        if (maiorConta == 0) {
            maiorConta = 11110;
        }
        maiorConta++;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getCpf());
            stmt.setString(2, conta.getTipoDaConta());
            stmt.setInt(3, maiorAgencia);
            stmt.setInt(4, maiorConta);
            stmt.setDouble(5, conta.getSaldo());
            stmt.setString(6, conta.getSenha());
            stmt.setBoolean(7, false);

            stmt.executeUpdate();
        }
    }

    // Método para retornar o maior valor da coluna 'agencia' da tabela 'Conta'
    public int obterMaiorAgencia() throws SQLException {
        String sql = "SELECT MAX(agencia) FROM Conta";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1); // O índice 1 representa a primeira coluna da consulta
            }
        }

        return 0; // Retorna 0 se não houver registros na tabela 'Conta'
    }

    // Método para retornar o maior valor da coluna 'conta' da tabela 'Conta'
    public int obterMaiorConta() throws SQLException {
        String sql = "SELECT MAX(conta) FROM Conta";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1); // O índice 1 representa a primeira coluna da consulta
            }
        }

        return 0; // Retorna 0 se não houver registros na tabela 'Conta'
    }

    // Método que verifica o CPF e senha do cliente no banco e ver também se o mesmo está ativo
    public boolean validarUsuario(String cpfUsuario, String senhaUsuario) throws SQLException {
        String sql = "SELECT cpf, senha, statusconta FROM Conta WHERE cpf = ? AND senha = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpfUsuario);
            stmt.setString(2, senhaUsuario);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String cpf = resultSet.getString("cpf");
                    String senha = resultSet.getString("senha");
                    Boolean statusConta = resultSet.getBoolean("statusconta");

                    if (cpf.equals(cpfUsuario) && senha.equals(senhaUsuario) && statusConta.equals(false)) {
                        System.out.println("\n > O seu usuário está sem permissão para acessar o sistema! <\n");
                        return false;
                    } else if (cpf.equals(cpfUsuario) && senha.equals(senhaUsuario) && statusConta.equals(true)) {
                        System.out.println("\n > Usuário logado com sucesso. <\n");
                        return true;
                    }
                }
            }
        }

        return false; // Retorna false se não encontrar uma correspondência de CPF e senha
    }

    // Método que altera o status do usuário (true ou false) no banco de dados
    public void mudarStatusUsuario(String cpf) throws SQLException {
        String sql = "UPDATE conta SET statusconta = true WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            stmt.executeUpdate();
        } 
    }
}