package com.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.App;
import com.models.dao.ContaDAO;
import com.models.dao.GerenteDAO;
import com.models.entity.Conexao;
import com.models.entity.Conta;

public class GerenteController {

    private static Connection conexao = Conexao.getInstancia();
    private static ContaDAO contaDAO = new ContaDAO(conexao);
    private static GerenteDAO gerenteDAO = new GerenteDAO(conexao);
    private static Scanner scanner = new Scanner(System.in);

    public static boolean aprovarCadastro(Conta conta) throws SQLException {

        System.out.println("\n > Dados informados com sucesso, agora é necessário a aprovação do gerente. <");

        System.out.println("\n---------------------------------");
        System.out.println("    L O G I N   G E R E N T E    ");
        System.out.println("---------------------------------");
        System.out.printf("-> Login (CPF): ");
        String login = scanner.next();
        System.out.printf("-> Senha: ");
        String senha = scanner.next();

        if (gerenteDAO.validarGerente(login, senha)) {
            contaDAO.ativarConta(conta.getUsuario().getCpf());
            return true;
        }
        return false;
    }

    public static boolean aprovarEmprestimo() throws SQLException {
        App.limparTela();
        System.out.println("\n > Dados informados com sucesso, agora é necessário a aprovação do gerente. <");

        System.out.println("\n---------------------------------");
        System.out.println("    L O G I N   G E R E N T E    ");
        System.out.println("---------------------------------");
        System.out.printf("-> Login (CPF): ");
        String login = scanner.next();
        System.out.printf("-> Senha: ");
        String senha = scanner.next();

        if (gerenteDAO.validarGerente(login, senha)) {
            return true;
        }
        return false;
    }

    public static boolean aprovarExclusao() throws SQLException {
        App.limparTela();
        System.out.println("\n > Dados informados com sucesso, agora é necessário a aprovação do gerente. <");

        System.out.println("\n---------------------------------");
        System.out.println("    L O G I N   G E R E N T E    ");
        System.out.println("---------------------------------");
        System.out.printf("-> Login (CPF): ");
        String login = scanner.next();
        System.out.printf("-> Senha: ");
        String senha = scanner.next();

        if (gerenteDAO.validarGerente(login, senha)) {
            return true;
        }
        return false;
    }

    public static boolean aprovarDesbloqueio(String cpf) throws SQLException {

        App.limparTela();
        System.out.println("\n > Agora é necessário a aprovação do gerente. <");

        System.out.println("\n---------------------------------");
        System.out.println("    L O G I N   G E R E N T E    ");
        System.out.println("---------------------------------");
        System.out.printf("-> Login (CPF): ");
        String login = scanner.next();
        System.out.printf("-> Senha: ");
        String senha = scanner.next();

        if (gerenteDAO.validarGerente(login, senha)) {
            contaDAO.desbloquearConta(cpf);
            return true;
        }
        return false;
    }

}