package com;

import java.sql.SQLException;
import java.util.Scanner;

import com.controllers.ContaController;
import com.controllers.EmprestimoController;
import com.controllers.GerenteController;
import com.controllers.UsuarioController;
import com.models.entity.Conexao;
import com.models.entity.Conta;

public class App {

    public static void main(String[] args) throws SQLException {

        limparTela();
        Conexao.getInstancia();
        Scanner scanner = new Scanner(System.in);
        int opcao;
        String login, senha;

        do {
            System.out.println("+-------------------------------+");
            System.out.println("|  *  *  I M P E R I A L  *  *  |");
            System.out.println("| *  *  *    B A N K    *  *  * |");
            do {
                System.out.println("+-------------------------------+");
                System.out.println("| 1 -> Realizar login           |");
                System.out.println("| 2 -> Cadastrar conta          |");
                System.out.println("| 3 -> Pedir ajuda              |");
                System.out.println("| 4 -> Sair do app              |");
                System.out.println("+-------------------------------+");
                System.out.printf("| Digite: ");
                opcao = scanner.nextInt();

                limparTela();
                if (opcao < 1 || opcao > 4) {
                    System.out.println("\nOpção inválida, tente novamente!\n");
                }

            } while (opcao < 1 || opcao > 4);

            switch (opcao) {
                case 1:
                    limparTela();
                    System.out.println("+-------------------------------+");
                    System.out.println("|          L O G I N            |");
                    System.out.println("+-------------------------------+");
                    System.out.printf("| -> Login (CPF): ");
                    login = scanner.next();
                    System.out.printf("| -> Senha: ");
                    senha = scanner.next();
                    System.out.println("+-------------------------------+");

                    limparTela();
                    if (ContaController.logar(login, senha)) {
                        System.out.println("\n > Usuário logado com sucesso. <\n");
                        int opcaoMenu;

                        do {
                            do {
                                System.out.println("+-------------------------------+");
                                System.out.println("|   I M P E R I A L   B A N K   |");
                                System.out.println("+-------------------------------+");
                                System.out.println("| 1 -> Depositar                |");
                                System.out.println("| 2 -> Sacar                    |");
                                System.out.println("| 3 -> Transferir               |");
                                System.out.println("| 4 -> Ver extrato              |");
                                System.out.println("| 5 -> Ver saldo                |");
                                System.out.println("| 6 -> Ver informações da conta |");
                                System.out.println("| 7 -> Atualizar dados          |");
                                System.out.println("| 8 -> Alterar senha            |");
                                System.out.println("| 9 -> Emprestimo               |");
                                System.out.println("| 10 -> Excluir conta           |");
                                System.out.println("| 11 -> Deslogar                |");
                                System.out.println("+-------------------------------+");
                                System.out.printf("| Digite: ");
                                opcaoMenu = scanner.nextInt();

                                limparTela();
                                if (opcaoMenu < 1 || opcaoMenu > 11) {
                                    System.out.println("\n > Opção inválida, tente novamente! <\n");
                                }
                            } while (opcaoMenu < 1 || opcaoMenu > 11);

                            switch (opcaoMenu) {
                                case 1: // DEPOSITAR
                                    double valorDeposito;
                                    do {
                                        System.out.println("+-------------------------------+");
                                        System.out.println("|     D  E  P  Ó  S  I  T  O    |");
                                        System.out.println("+-------------------------------+");
                                        System.out.printf("| Digite o valor: R$");

                                        valorDeposito = scanner.nextDouble();

                                        if (valorDeposito > 0) {
                                            limparTela();
                                            ContaController.depositar(login, valorDeposito);
                                        } else {
                                            limparTela();
                                            System.out.printf("\n > Depósito de R$%.2f inválido. <\n\n", valorDeposito);
                                        }
                                    } while (valorDeposito <= 0);
                                    break;
                                case 2: // SACAR
                                    double valorSaque;
                                    do {
                                        System.out.println("+-------------------------------+");
                                        System.out.println("|         S  A  Q  U  E         |");
                                        System.out.println("+-------------------------------+");
                                        System.out.printf("| Digite o valor: R$");
                                        valorSaque = scanner.nextInt();

                                        if (valorSaque > 0) {
                                            limparTela();
                                            ContaController.sacar(login, valorSaque);
                                        } else if (valorSaque <= 0) {
                                            limparTela();
                                            System.out.printf("\n  > Saque de R$%.2f inválido. <\n\n", valorSaque);
                                        }
                                    } while (valorSaque < 0);
                                    break;
                                case 3: // TRANSFERIR
                                    double valorTransferir;
                                    int contadorErro = 0;

                                    do {
                                        System.out.println("+----------------------------------+");
                                        System.out.println("|     T R A N S F E R Ê N C I A    |");
                                        System.out.println("+----------------------------------+");
                                        System.out.printf("| Digite o valor: R$");
                                        valorTransferir = scanner.nextInt();
                                        limparTela();

                                        if (valorTransferir > 0
                                                && valorTransferir <= ContaController.imprimirSaldo(login)) {
                                            String contaDestino = "";
                                            boolean contaValida = false;

                                            for (int tentativas = 0; tentativas < 3; tentativas++) {
                                                System.out.println("+----------------------------------+");
                                                System.out.println("|     T R A N S F E R Ê N C I A    |");
                                                System.out.println("+----------------------------------+");
                                                System.out.printf("| Conta destino (CPF): ");
                                                contaDestino = scanner.next();

                                                if (ContaController.buscarPorCPF(contaDestino) == false) {
                                                    limparTela();
                                                    System.out.println("\n> Conta inválida, tente novamente! <\n");
                                                } else if (contaDestino.equals(login)) {
                                                    limparTela();
                                                    System.out.println(
                                                            "\n> Não é possível transferir para si mesmo, tente novamente! <\n");
                                                } else {
                                                    contaValida = true;
                                                    break; // Sai do loop se a conta for válida
                                                }
                                            }

                                            if (contaValida) {
                                                if (ContaController.transferir(login, contaDestino, valorTransferir,
                                                        senha)) {
                                                    limparTela();
                                                    System.out.printf(
                                                            "\n> Transferência de R$%.2f realizada com sucesso!. <\n\n",
                                                            valorTransferir);
                                                    break;
                                                } else {
                                                    System.out.println(
                                                            "\n > A conta destino está inativa OU você excedeu o número de tentativas. <\n\t\t> A transferência não pode ser realizada! <\n");
                                                }
                                            } else {
                                                contadorErro++;
                                            }
                                        } else if (valorTransferir > ContaController.imprimirSaldo(login)) {
                                            System.out.println(
                                                    "\n > Saldo insuficiente para realizar a transferência! <\n");
                                            break;
                                        } else {
                                            System.out.printf(
                                                    "\n  > Valor para transferência de R$%.2f inválido. <\n\n",
                                                    valorTransferir);
                                        }

                                    } while ((valorTransferir <= 0
                                            || valorTransferir > ContaController.imprimirSaldo(login))
                                            && contadorErro < 3);

                                    break;

                                case 4: // VER EXTRATO
                                    limparTela();
                                    ContaController.imprimirExtrato(login);
                                    break;
                                case 5: // VER SALDO
                                    limparTela();
                                    System.out.println("\n+-----------------------------------------+");
                                    System.out.println("|        S A L D O  D A  C O N T A        |");
                                    System.out.println("+-----------------------------------------+");
                                    System.out.printf("| > Saldo atual: R$%.2f\n",
                                            ContaController.imprimirSaldo(login));
                                    System.out.println("+-----------------------------------------+\n");
                                    break;
                                case 6: // VER INFORMAÇÕES DA CONTA
                                    limparTela();
                                    ContaController.visualizarInformacoes(login);
                                    break;
                                case 7: // ATUALIZAR DADOS
                                    UsuarioController.atualizarDados(login);
                                    break;
                                case 8: // ALTERAR SENHA
                                    System.out.println("+-------------------------------------------+");
                                    System.out.println("|    A  L  T  E  R  A  R   S  E  N  H  A    |");
                                    System.out.println("+-------------------------------------------+");
                                    System.out.println("| Conta: " + login);
                                    System.out.println("+-------------------------------------------+");
                                    limparTela();
                                    senha = ContaController.alterarSenhaLogado(login, senha);
                                    break;
                                case 9: // EMPRESTIMO
                                    int opcaoEmprestimo;
                                    do {
                                        System.out.println("+-------------------------------+");
                                        System.out.println("|      E M P R E S T I M O      |");
                                        System.out.println("+-------------------------------+");
                                        System.out.println("| 1 -> Fazer emprestimo         |");
                                        System.out.println("| 2 -> Visualizar emprestimo    |");
                                        System.out.println("| 3 -> Pagar parcela            |");
                                        System.out.println("| 4 -> Voltar menu              |");
                                        System.out.println("+-------------------------------+");
                                        System.out.printf("| Digite: ");
                                        opcaoEmprestimo = scanner.nextInt();

                                        if (opcaoEmprestimo < 1 || opcaoEmprestimo > 4) {
                                            limparTela();
                                            System.out.println("\n > Opção inválida, tente novamente! <\n");
                                        }

                                        switch (opcaoEmprestimo) {
                                            case 1: // FAZER EMPRESTIMO
                                                limparTela();
                                                EmprestimoController.solicitarEmprestimo(login);
                                                opcaoEmprestimo = 0;
                                                break;
                                            case 2: // VISUALIZAR EMPRESTIMO
                                                limparTela();
                                                EmprestimoController.visualizarEmprestimo(login);
                                                opcaoEmprestimo = 0;
                                                break;
                                            case 3: // PAGAR PARCELA
                                                EmprestimoController.pagarParcelaEmprestimo(login);
                                                opcaoEmprestimo = 0;
                                                break;
                                            case 4: // VOLTAR MENU
                                                limparTela();
                                                break;
                                        }

                                    } while (opcaoEmprestimo < 1 || opcaoEmprestimo > 4);
                                    break;
                                case 10: // EXCLUIR CONTA
                                    limparTela();
                                    ContaController.excluirConta(login);
                                    opcaoMenu = 10;
                                    break;
                                default:
                                    break;
                            }
                        } while (opcaoMenu != 11);
                    } else {
                        if (!ContaController.buscarPorCPF(login)) {
                            System.out.println("\n     > Usuário inexistente! <\n");
                        } else if (ContaController.logar(login, senha) == false) {
                            System.out.println(
                                    "\n > Usuário está sem permissão para acessar o sistema ou a senha está incorreta! <\n");
                        }

                    }
                    break;

                case 2: // CADASTRAR CONTA
                    Conta conta = ContaController.cadastrarConta();
                    limparTela();
                    if (GerenteController.aprovarCadastro(conta)) {
                        limparTela();
                        System.out.println("\n  Cadastro aprovado com sucesso.");
                        System.out.println("Seja bem-vindo(a) ao IMPERIAL BANK!\n");
                    } else {
                        limparTela();
                        System.out.println("\n    > Gerente inválido. Cadastro não foi aprovado. <");
                        System.out.println(" > Acessar o 'Pedir ajuda' para desbloquear sua conta. <\n");
                    }
                    break;
                case 3: // MENU DE AJUDA
                    int opcaoAjuda;
                    do {
                        do {

                            System.out.println("+------------------------------------+");
                            System.out.println("|   P R E C I S A  D E  A J U D A ?  |");
                            System.out.println("+------------------------------------+");
                            System.out.println("| 1 -> Alterar senha                 |");
                            System.out.println("| 2 -> Desbloquear conta             |");
                            System.out.println("| 3 -> Voltar menu                   |");
                            System.out.println("+------------------------------------+");
                            System.out.printf("| Digite: ");
                            opcaoAjuda = scanner.nextInt();

                            limparTela();
                            if (opcaoAjuda < 1 || opcaoAjuda > 3) {
                                System.out.println("\n > Opção inválida, tente novamente! < \n");
                            }

                        } while (opcaoAjuda < 1 || opcaoAjuda > 3);

                        switch (opcaoAjuda) {
                            case 1: // ALTERAR SENHA
                                limparTela();
                                ContaController.alterarSenhaDeslogado();
                                break;
                            case 2: // DESBLOQUEAR CONTA
                                limparTela();
                                ContaController.desbloquearConta();
                                break;
                            case 3: // VOLTA MENU
                                opcaoAjuda = 3;
                                break;
                            default:
                                break;
                        }
                        break;
                    } while (opcaoAjuda != 3);

                default:
                    break;
            }
        } while (opcao != 4);

        System.out.println("\n+----------------------------------+");
        System.out.println("|  Você deslogou do IMPERIAL BANK! |");
        System.out.println("+----------------------------------+\n");

        scanner.close();
    }

    public static void limparTela() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                // No Windows, use o comando "cls" para limpar a tela.
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Caso contrário (Linux, macOS), use a sequência ANSI para limpar a tela.
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}