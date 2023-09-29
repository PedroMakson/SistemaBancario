package com;

import java.sql.Connection;
import java.sql.SQLException;

import com.models.dao.UsuarioDAO;
import com.models.entity.Conexao;
import com.models.entity.Usuario;

public class Teste {

    public static void main(String[] args) throws SQLException {

        Connection conexao = Conexao.getInstancia();

        UsuarioDAO usuarioDao = new UsuarioDAO(conexao);

        usuarioDao.inserirUsuario(null);

    }
}