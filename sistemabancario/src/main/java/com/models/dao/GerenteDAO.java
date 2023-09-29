package com.models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GerenteDAO {

    private static Connection conexao;

    public GerenteDAO(Connection conexao) {
        GerenteDAO.conexao = conexao;
    }

    public static boolean validarGerente(String cpfGerente, String senhaGerente) throws SQLException {
        String sql = "SELECT cpf FROM Gerente WHERE cpf = ? AND senha = ?";
    
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpfGerente);
            stmt.setString(2, senhaGerente);
    
            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next(); // Se houver resultados na consulta, as credenciais são válidas
            }
        }
    }

}
