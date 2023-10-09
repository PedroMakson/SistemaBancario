package com.models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.models.entity.Usuario;

public class UsuarioDAO {

    private Connection conexao;

    public UsuarioDAO(Connection conexao) {
        this.conexao = conexao;
    }

    // Método para inserir um novo usuário no banco de dados
    public void inserirUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (cpf, nome, rg, renda, telefone, email, datanascimento, cep, rua, numerodacasa, bairro, cidade, uf) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getCpf());
            stmt.setString(2, usuario.getNome());
            stmt.setString(3, usuario.getRg());
            stmt.setDouble(4, usuario.getRenda());
            stmt.setString(5, usuario.getTelefone());
            stmt.setString(6, usuario.getEmail());
            stmt.setDate(7, new java.sql.Date(usuario.getDataNascimento().getTime()));
            stmt.setString(8, usuario.getCep());
            stmt.setString(9, usuario.getRua());
            stmt.setInt(10, usuario.getNumeroDaCasa());
            stmt.setString(11, usuario.getBairro());
            stmt.setString(12, usuario.getCidade());
            stmt.setString(13, usuario.getUf());

            stmt.executeUpdate();
        }
    }

    // Método para buscar um usuário pelo CPF que retorna um RESULTSET
    public Usuario buscarPorCPF(String cpf) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return criarUsuario(resultSet);
                }
            }
        }

        return null;
    }

    // Método para criar um objeto Usuario a partir de um ResultSet
    private Usuario criarUsuario(ResultSet resultSet) throws SQLException {
        String cpf = resultSet.getString("cpf");
        String nome = resultSet.getString("nome");
        String rg = resultSet.getString("rg");
        double renda = resultSet.getDouble("renda");
        String telefone = resultSet.getString("telefone");
        String email = resultSet.getString("email");
        Date dataNascimento = resultSet.getDate("datanascimento");
        String cep = resultSet.getString("cep");
        String rua = resultSet.getString("rua");
        int numeroDaCasa = resultSet.getInt("numerodacasa");
        String bairro = resultSet.getString("bairro");
        String cidade = resultSet.getString("cidade");
        String uf = resultSet.getString("uf");

        return new Usuario(cpf, nome, rg, renda, telefone, email, dataNascimento, cep, rua, numeroDaCasa, bairro,
                cidade, uf);
    }

    // Método para retornar a renda de um usuário pelo CPF
    public double obterRendaPorCPF(String cpf) throws SQLException {
        String sql = "SELECT renda FROM usuarios WHERE cpf = ?";
        double renda = 0.0;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    renda = resultSet.getDouble("renda");
                }
            }
        }

        return renda;
    }

    // Método que verifica o dado que o usuário quer atualizar e faz a atualização dele
    public boolean atualizarAtributoUsuario(String cpf, int caso, String novoValor) throws SQLException {
        String sql = "";
        String colunaAtributo = "";
        double novaRenda = 0;
        int novoNumeroCasa = 0;

        switch (caso) {
            case 1:
                colunaAtributo = "nome";
                break;
            case 2:
                colunaAtributo = "renda";
                novaRenda = Double.parseDouble(novoValor);
                break;
            case 3:
                colunaAtributo = "telefone";
                break;
            case 4:
                colunaAtributo = "email";
                break;
            case 5:
                colunaAtributo = "cep";
                break;
            case 6:
                colunaAtributo = "rua";
                break;
            case 7:
                colunaAtributo = "numeroDaCasa";
                novoNumeroCasa = Integer.parseInt(novoValor);
                break;
            case 8:
                colunaAtributo = "bairro";
                break;
            case 9:
                colunaAtributo = "cidade";
                break;
            case 10:
                colunaAtributo = "uf";
                break;
            default:
                return false;
        }

        sql = "UPDATE Usuarios SET " + colunaAtributo + " = ? WHERE cpf = ?";

        if (caso == 1 || caso == 3 || caso == 4 || caso == 5 || caso == 6 || caso == 8 || caso == 9 || caso == 10) {
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setString(1, novoValor);
                stmt.setString(2, cpf);

                int linhasAfetadas = stmt.executeUpdate();
                return linhasAfetadas > 0;
            }
        }

        if (caso == 2) {
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setDouble(1, novaRenda);
                stmt.setString(2, cpf);

                int linhasAfetadas = stmt.executeUpdate();
                return linhasAfetadas > 0;
            }
        }

        if (caso == 7) {
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setInt(1, novoNumeroCasa);
                stmt.setString(2, cpf);

                int linhasAfetadas = stmt.executeUpdate();
                return linhasAfetadas > 0;
            }
        }

        return false;
    }

    // Método que verifica se existe uma Usuario com o CPF no banco
    public boolean buscarPorCPFBollean(String cpf) throws SQLException {
        String sql = "SELECT cpf FROM usuarios WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next(); // Retorna true se o CPF existe no banco de dados, caso contrário, retorna
                                         // false
            }
        }
    }
}