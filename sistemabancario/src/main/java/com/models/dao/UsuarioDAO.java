package com.models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    // Método para buscar um usuário pelo CPF
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

    // Método para listar todos os usuários
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                usuarios.add(criarUsuario(resultSet));
            }
        }

        System.out.println(usuarios);
        return usuarios;
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
}