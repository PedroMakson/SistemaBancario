package com.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.App;
import com.models.dao.ContaDAO;
import com.models.dao.EmprestimoDAO;
import com.models.dao.UsuarioDAO;
import com.models.entity.Conexao;
import com.models.entity.Conta;
import com.models.entity.Emprestimo;

public class EmprestimoController {

    private static Connection conexao = Conexao.getInstancia();
    private static EmprestimoDAO emprestimoDAO = new EmprestimoDAO(conexao);
    private static ContaDAO contaDAO = new ContaDAO(conexao);
    private static UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);
    private static Scanner scanner = new Scanner(System.in);

    public static void solicitarEmprestimo(String cpf) throws SQLException {
        double renda = usuarioDAO.obterRendaPorCPF(cpf);
        double valorEmprestimo = (Math.round(((renda * 2.3) / 100)) * 100);
        double valorEmprestimoComJuros = valorEmprestimo + (0.06 * valorEmprestimo);
        int parcelas = 0;
        double validarValorParcela;

        do {
            System.out.println("+-------------------------------------------+");
            System.out.println("|       E  M  P  R  E  S  T  I  M  O        |");
            System.out.println("+-------------------------------------------+");
            System.out.printf("\t > RENDA ATUAL: R$%.2f <\n", renda);
            System.out.println("     Divida em até 12x com 6% de juros.      ");
            System.out.println("+-------------------------------------------+");
            System.out.printf("| > VALOR NORMAL: R$%.2f\n", valorEmprestimo);
            System.out.println("+-------------------------------------------+");
            System.out.printf("| > VALOR COM JUROS: R$%.2f\n", valorEmprestimoComJuros);
            System.out.println("+-------------------------------------------+");
            System.out.print("-> Quantidade de parcelas: ");
            parcelas = scanner.nextInt();

            validarValorParcela = valorEmprestimoComJuros / parcelas;

            if (parcelas < 1 || parcelas > 12) {
                App.limparTela();
                System.out.println("\n > Quantidade de parcelas inválidas. Digite novamente! <\n");
            } else {
                if (validarValorParcela > renda) {
                    App.limparTela();
                    System.out.printf("\n\n > O valor total ficou de R$%.2f em %dX com 6%% de juros! <\n",
                            valorEmprestimoComJuros, parcelas);
                    System.out.printf(
                            " > As parcelas ficaram no valor de R$%.2f, o que não condiz com o valor da sua renda. <\n",
                            validarValorParcela);
                    System.out.println(" > Aumente o número de parcelas para adequar ao seu orçamento! <\n\n");
                }
            }
        } while (parcelas < 1 || parcelas > 12 || (parcelas >= 1 && parcelas <= 12 && validarValorParcela > renda));

        if (GerenteController.aprovarEmprestimo()) {
            App.limparTela();
            Conta conta = contaDAO.buscarContaPorCPF(cpf);
            Emprestimo emprestimo = new Emprestimo(conta, valorEmprestimoComJuros, parcelas, 6);
            emprestimoDAO.inserirEmprestimo(emprestimo);
            System.out.println("\n > Empréstimo aprovado com sucesso. <\n");

        } else {
            App.limparTela();
            System.out.println("\n > Gerente inválido. Empréstimo não foi aprovado. <\n");
        }

    }

    public static void visualizarEmprestimo(String cpf) throws SQLException {
        emprestimoDAO.exibirEmprestimosPorCPF(cpf);
    }

    public static void pagarParcelaEmprestimo(String cpf) throws SQLException {

        String documento;
        int numeroParcela;
        do {
            System.out.println("\n+-------------------------------------------+");
            System.out.println("|    P  A  G  A  R   P  A  R  C  E  L  A    |");
            System.out.println("+-------------------------------------------+");
            System.out.printf("| Documento : ");
            documento = scanner.next();
            App.limparTela();

            if (!emprestimoDAO.documentoExiste(documento.toUpperCase())) {
                App.limparTela();
                System.out.println("\n > Documento inválido, tente novamente. <\n");
            }
        } while (!emprestimoDAO.documentoExiste(documento.toUpperCase()));

        boolean parcelaExiste;
        do {
            System.out.println("+-------------------------------------------+");
            System.out.println("|    P  A  G  A  R   P  A  R  C  E  L  A    |");
            System.out.println("+-------------------------------------------+");
            System.out.println("| Documento: " + documento.toUpperCase());
            System.out.printf("| Parcela : ");
            numeroParcela = scanner.nextInt();

            if (numeroParcela < 1 || numeroParcela > 12) {
                App.limparTela();
                System.out.println("\n > Parcela inválida, escolha entre 1 e 12. <\n");
            }

            parcelaExiste = emprestimoDAO.parcelaExisteParaDocumento(documento.toUpperCase(), numeroParcela);
            if (!parcelaExiste) {
                App.limparTela();
                System.out.println("\n > Parcela não existe. <\n");
            }

        } while (numeroParcela < 1 || numeroParcela > 12 || parcelaExiste != true);

        emprestimoDAO.pagarParcela(cpf, documento.toUpperCase(), numeroParcela);

    }
}