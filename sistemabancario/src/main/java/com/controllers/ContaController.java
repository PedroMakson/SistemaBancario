package com.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.models.dao.ContaDAO;
import com.models.entity.Conexao;
import com.models.entity.Conta;
import com.models.entity.Usuario;

public class ContaController {

    private static Connection conexao = Conexao.getInstancia();
    private static Usuario usuario;
    private static Conta conta;
    private static ContaDAO contaDAO = new ContaDAO(conexao);
    private static Scanner scanner = new Scanner(System.in);

    public static Conta cadastrarConta() {
        try {

            usuario = UsuarioController.cadastrarUsuario();

            if (usuario != null) {
                // Solicita ao usuário que insira os dados
                System.out.print("-> Senha: ");
                String senha = scanner.nextLine();

                conta = new Conta(usuario, senha);

                contaDAO.inserirConta(usuario, conta);
            }

            return conta;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean logar(String cpf, String senha) throws SQLException {

        if (contaDAO.validarUsuario(cpf, senha)) {
            return true;
        }
        return false;
    }
}