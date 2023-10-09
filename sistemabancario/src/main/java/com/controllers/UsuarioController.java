package com.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import com.App;
import com.models.dao.UsuarioDAO;
import com.models.entity.Conexao;
import com.models.entity.Usuario;

public abstract class UsuarioController {

    private static Connection conexao = Conexao.getInstancia();
    private static Usuario usuario;
    private static UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);
    private static Scanner scanner = new Scanner(System.in);

    // Método para cadastrar/inserir um usuário ao banco de dados.
    public static Usuario cadastrarUsuario() {

        try {
            // Solicita ao usuário que insira os dados
            String cpf;
            do {
                System.out.println("+------------------------+");
                System.out.println("| C  A  D  A  S  T  R  O |");
                System.out.println("+------------------------+");

                System.out.print("-> CPF: ");
                cpf = scanner.nextLine();

                // Verifica se o CPF contém apenas dígitos
                if (!cpf.matches("\\d+")) {
                    App.limparTela();
                    System.out.println("\n > CPF deve conter apenas dígitos. <\n");
                    continue;
                }

                // Verifica se o CPF tem 11 dígitos
                if (cpf.length() != 11) {
                    App.limparTela();
                    System.out.println("\n > CPF deve conter 11 dígitos. <\n");
                    continue;
                }

                // Verifica se um CPF já existe no banco de dados
                if (ContaController.buscarPorCPF(cpf)) {
                    App.limparTela();
                    System.out.println("\n > Já existe um usuário com esse CPF, tente novamente. <\n");
                }

            } while (cpf.length() != 11 || ContaController.buscarPorCPF(cpf) || cpf.startsWith("-"));

            String nome;
            do {
                System.out.print("-> Nome completo: ");
                nome = scanner.nextLine();

                // Verifica se o nome não contém números
                if (nome.matches(".*\\d+.*")) {
                    App.limparTela();
                    System.out.println("\n > O nome não deve conter números. Tente novamente. <\n");
                    continue; // Volta ao início do loop para uma nova entrada
                }

                // Se o nome não contiver números, é válido
                break;
            } while (true);

            String rg;
            do {
                System.out.print("-> RG: ");
                rg = scanner.nextLine();

                // Verifica se o RG tem 8 dígitos
                if (rg.length() != 9 || rg.startsWith("-")) {
                    App.limparTela();
                    System.out.println(
                            "\n > RG inválido. O RG não deve ser negativo e deve conter exatamente 9 dígitos. <\n");
                    continue; // Volta ao início do loop para uma nova entrada
                }

                // Verifica se o RG contém apenas dígitos
                if (!rg.matches("\\d+")) {
                    App.limparTela();
                    System.out.println("\n > RG não deve conter letras. <\n");
                    continue; // Volta ao início do loop para uma nova entrada
                }

                // Se passar por ambas as verificações, o RG é válido
                break;
            } while (true);

            double renda = 0;
            boolean rendaValida = false;

            do {
                System.out.print("-> Renda: R$");

                if (scanner.hasNextDouble()) {
                    renda = scanner.nextDouble();
                    scanner.nextLine(); // Limpar a linha (avançar para a próxima linha)

                    // Verifica se a renda é menor que zero
                    if (renda < 0) {
                        App.limparTela();
                        System.out.println(
                                "\n > Valor de renda inválido. A renda mensal não pode ser negativa. Tente novamente. <\n");
                    } else {
                        rendaValida = true;
                    }
                } else {
                    App.limparTela();
                    System.out.println("\n > Valor de renda inválido. Digite um valor numérico. Tente novamente. <\n");
                    scanner.nextLine(); // Limpar entrada inválida e avançar para a próxima linha
                }
            } while (!rendaValida);

            String telefone;
            do {
                System.out.print("-> Telefone: ");
                telefone = scanner.nextLine();

                // Verifica se o telefone contém apenas dígitos
                if (!telefone.matches("[\\d,]+")) {
                    App.limparTela();
                    System.out.println("\n > Telefone não deve conter letras. <\n");
                    continue;
                }

                // Verifica se o telefone tem 11 dígitos
                if (telefone.length() != 11) {
                    App.limparTela();
                    System.out.println("\n > Telefone deve conter exatamente 11 dígitos. <\n");
                }
            } while (!telefone.matches("\\d{11}"));

            String email;
            do {
                System.out.print("-> E-mail: ");
                email = scanner.nextLine();

                if (email.contains("@") && email.indexOf("@") > 0 && email.indexOf("@") < email.length() - 1
                        && email.contains(".com")) {
                    // Verifica se o email é válido.
                    break;
                } else {
                    // Caso contrário, informa que o email é inválido e solicita outra tentativa.
                    App.limparTela();
                    System.out.println(
                            "\n > Endereço de email inválido. O email deve conter '@' e '.com'. Tente novamente. <\n");
                }
            } while (true);

            Date dataNascimento = null;
            while (dataNascimento == null) {
                System.out.print("-> Data de Nascimento (AAAA-MM-DD): ");
                String dataNascimentoStr = scanner.nextLine();
                dataNascimento = stringParaData(dataNascimentoStr);
            }

            String cepString;
            while (true) {
                System.out.print("-> CEP: ");
                cepString = scanner.nextLine();

                // Verifica se o CEP contém apenas dígitos
                if (!cepString.matches("\\d+")) {
                    App.limparTela();
                    System.out.println("\n > CEP deve conter apenas dígitos. <\n");
                    continue; // Volta ao início do loop para uma nova entrada
                }

                // Verifica se o CEP tem 9 dígitos
                if (cepString.length() != 8) {
                    App.limparTela();
                    System.out.println("\n > CEP deve conter exatamente 8 dígitos. <\n");
                    continue; // Volta ao início do loop para uma nova entrada
                }
                // Se passar por ambas as verificações, o CEP é válido
                break;
            }

            String cep = (cepString.substring(0, 5) + "-" + cepString.substring(5));

            System.out.print("-> Rua: ");
            String rua = scanner.nextLine();

            int numeroDaCasa = 0;
            boolean numeroValido = false;

            do {
                System.out.print("-> Número da Casa: ");
                String input = scanner.nextLine();

                if (input.matches("\\d+")) {
                    numeroDaCasa = Integer.parseInt(input);

                    if (numeroDaCasa < 0) {
                        App.limparTela();
                        System.out.println(
                                "\n > Número da casa não pode ser negativo. Digite um valor numérico válido. <\n");
                    } else {
                        numeroValido = true;
                    }
                } else {
                    App.limparTela();
                    System.out.println("\n > Número da casa inválido. Digite um valor numérico. <\n");
                }
            } while (!numeroValido);

            System.out.print("-> Bairro: ");
            String bairro = scanner.nextLine();

            System.out.print("-> Cidade: ");
            String cidade = scanner.nextLine();

            String uf;
            do {
                System.out.print("-> UF: ");
                uf = scanner.nextLine();

                // Verifica se a UF tem exatamente 2 caracteres
                if (uf.length() != 2) {
                    App.limparTela();
                    System.out.println("\n > A UF deve conter exatamente 2 caracteres. <\n");
                    continue; // Volta ao início do loop para uma nova entrada
                }

                // Verifica se a UF não contém números
                if (uf.matches(".*\\d+.*")) {
                    App.limparTela();
                    System.out.println("\n > A UF não deve conter números. <\n");
                    continue; // Volta ao início do loop para uma nova entrada
                }

                // Se passar por ambas as verificações, a UF é válida
                break;
            } while (true);

            // Cria um novo usuário com os dados inseridos pelo usuário
            usuario = new Usuario(cpf, nome, rg, renda, telefone, email, dataNascimento, cep, rua, numeroDaCasa, bairro,
                    cidade, uf);

            // Insere o novo usuário no banco de dados
            usuarioDAO.inserirUsuario(usuario);

            return usuario;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    // Visualiza os dados do usuário pelo CPF passado
    public static void visualizarInformacoes(String cpf) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorCPF(cpf);

        if (usuario != null) {
            App.limparTela();
            System.out.println("\n+-----------------------------------------+");
            System.out.println("|  I N F O R M A Ç Õ E S  D A  C O N T A  |");
            System.out.println("+-----------------------------------------+");
            System.out.println("| Nome: " + usuario.getNome());
            System.out.printf("| CPF: " + usuario.getCpf().substring(0, 3) + "." + usuario.getCpf().substring(3, 6)
                    + "." + usuario.getCpf().substring(6, 9) + "-" + usuario.getCpf().substring(9, 11));
            System.out.printf("  | RG: " + usuario.getRg().substring(0, 3) + "." + usuario.getRg().substring(3, 6)
                    + "." + usuario.getRg().substring(6, 9) + "\n");

            System.out.println("| Data de Nascimento: " + usuario.getDataNascimento());
            System.out.printf("| Renda: R$%.2f\n", usuario.getRenda());
            System.out.println("+-----------------------------------------+");
            System.out.println("|              C O N T A T O              |");
            System.out.println("+-----------------------------------------+");
            System.out.printf("| Telefone: (" + usuario.getTelefone().substring(0, 2) + ")"
                    + usuario.getTelefone().substring(2, 7) + "-" + usuario.getTelefone().substring(7)
                    + "\n");
            System.out.println("| Email: " + usuario.getEmail());
            System.out.println("+-----------------------------------------+");
            System.out.println("|              E N D E R E Ç O            |");
            System.out.println("+-----------------------------------------+");
            System.out.println("| CEP: " + usuario.getCep());
            System.out.println("| Rua: " + usuario.getRua());
            System.out.println("| Número da Casa: " + usuario.getNumeroDaCasa());
            System.out.println("| Bairro: " + usuario.getBairro());
            System.out.println("| Cidade: " + usuario.getCidade());
            System.out.println("| UF: " + usuario.getUf());
        } else {
            System.out.println("Usuário com CPF " + cpf + " não encontrado.");
        }
    }

    // Método que atualiza algum dado escolhido pelo usuário
    public static void atualizarDados(String cpf) throws SQLException {

        // Verificar se o usuário com o CPF fornecido existe
        int opcao;

        do {
            do {
                // Exibir opções para atualização
                App.limparTela();
                System.out.println("+----------------------------------------------+");
                System.out.println("|   A T U A L I Z A R  I N F O R M A Ç Õ E S   |");
                System.out.println("+----------------------------------------------+");
                System.out.println("| 1 - Nome                                     |");
                System.out.println("| 2 - Renda                                    |");
                System.out.println("| 3 - Telefone                                 |");
                System.out.println("| 4 - Email                                    |");
                System.out.println("| 5 - CEP                                      |");
                System.out.println("| 6 - Rua                                      |");
                System.out.println("| 7 - Número da Casa                           |");
                System.out.println("| 8 - Bairro                                   |");
                System.out.println("| 9 - Cidade                                   |");
                System.out.println("| 10 - UF                                      |");
                System.out.println("| 0 - Sair                                     |");
                System.out.println("+----------------------------------------------+");

                System.out.printf("| > Escolha o dado: ");
                opcao = scanner.nextInt();
                scanner.nextLine();
                System.out.println("+----------------------------------------------+");

                if ((opcao < 0 || opcao > 10)) {
                    System.out.println("\n > Opção inválida, tente novamente! <\n");
                }

            } while (opcao < 0 || opcao > 10);

            switch (opcao) {
                case 1: // ATUALIZAR NOME
                    String nome;
                    do {
                        System.out.print("| Novo nome: ");
                        nome = scanner.nextLine();

                        // Verifica se o nome não contém números
                        if (nome.matches(".*\\d+.*")) {
                            App.limparTela();
                            System.out.println("\n > O nome não deve conter números. Tente novamente. <\n");
                            continue; // Volta ao início do loop para uma nova entrada
                        }

                        // Se o nome não contiver números, é válido
                        if (usuarioDAO.atualizarAtributoUsuario(cpf, 1, nome)) {
                            App.limparTela();
                            System.out.println("\n > Nome atualizado com sucesso! < \n");
                            opcao = 0;
                        }
                        break;
                    } while (true);

                    break;

                case 2: // ATUALIZAR RENDA
                    double renda = 0;
                    String rendaStr = "";
                    boolean rendaValida = false;

                    do {
                        System.out.print("| Nova renda: R$");
                        if (scanner.hasNextDouble()) {
                            renda = scanner.nextDouble();

                            // Verifica se a renda é menor que zero
                            if (renda < 0) {
                                App.limparTela();
                                System.out.println(
                                        "\n > Valor de renda inválido. A renda mensal não pode ser negativa. Tente novamente. <\n");
                            } else {
                                rendaValida = true;
                            }
                        } else {
                            App.limparTela();
                            System.out.println(
                                    "\n > Valor de renda inválido. Digite um valor numérico. Tente novamente. <\n");
                            scanner.next(); // Limpar entrada inválida
                        }
                    } while (!rendaValida);

                    rendaStr += renda;

                    // Após sair do loop, a renda é válida, então você pode tentar atualizá-la
                    if (usuarioDAO.atualizarAtributoUsuario(cpf, 2, String.valueOf(rendaStr))) {
                        App.limparTela();
                        System.out.println("\n > Renda atualizada com sucesso! <\n");
                        opcao = 0;
                    } else {
                        App.limparTela();
                        System.out.println("\n > Falha ao atualizar a renda. <\n");
                    }
                    break;

                case 3: // ATUALIZAR TELEFONE
                    String telefone;
                    do {
                        System.out.print("| Novo telefone: ");
                        telefone = scanner.nextLine();

                        // Verifica se o telefone contém apenas dígitos
                        if (!telefone.matches("\\d+")) {
                            App.limparTela();
                            System.out.println("\n > Telefone não deve conter letras. <\n");
                            continue;
                        }

                        // Verifica se o telefone tem 11 dígitos
                        if (telefone.length() != 11) {
                            App.limparTela();
                            System.out.println("\n > Telefone deve conter exatamente 11 dígitos. <\n");
                        }
                    } while (!telefone.matches("\\d{11}"));

                    if (usuarioDAO.atualizarAtributoUsuario(cpf, 3, telefone)) {
                        App.limparTela();

                        System.out.println("\n > Telefone atualizado com sucesso! <\n");
                        opcao = 0;
                    }
                    break;
                case 4: // ATUALIZAR E-MAIL
                    String email;

                    do {
                        System.out.print("| Novo email: ");
                        email = scanner.nextLine();

                        if (email.contains("@") && email.indexOf("@") > 0 && email.indexOf("@") < email.length() - 1
                                && email.contains(".com")) {
                            // Verifica se o email é válido.
                            break;
                        } else {
                            // Caso contrário, informa que o email é inválido e solicita outra tentativa.
                            App.limparTela();
                            System.out.println(
                                    "\n > Endereço de email inválido. O email deve conter '@' e '.com'. Tente novamente. <\n");
                        }
                    } while (true);

                    if (usuarioDAO.atualizarAtributoUsuario(cpf, 4, email)) {
                        App.limparTela();
                        System.out.println("\n > Email atualizado com sucesso! <\n");
                        opcao = 0;
                    }
                    break;

                case 5: // ATUALIZAR CEP
                    String cepString = "";

                    while (true) {
                        System.out.print("| Novo CEP: ");
                        cepString = scanner.nextLine();

                        // Verifica se o CEP contém apenas dígitos
                        if (!cepString.matches("\\d+")) {
                            App.limparTela();
                            System.out.println("\n > CEP deve conter apenas dígitos. <\n");
                            continue; // Volta ao início do loop para uma nova entrada
                        }

                        // Verifica se o CEP tem 9 dígitos
                        if (cepString.length() != 8) {
                            App.limparTela();
                            System.out.println("\n > CEP deve conter exatamente 8 dígitos. <\n");
                            continue; // Volta ao início do loop para uma nova entrada
                        }
                        // Se passar por ambas as verificações, o CEP é válido
                        break;
                    }

                    String cep = (cepString.substring(0, 5) + "-" + cepString.substring(5));

                    if (usuarioDAO.atualizarAtributoUsuario(cpf, 5, cep)) {
                        App.limparTela();
                        System.out.println("\n > CEP atualizado com sucesso! <\n");
                        opcao = 0;
                    }
                    break;
                case 6: // ATUALIZAR RUA
                    System.out.print("| Nova rua: ");
                    String novaRua = scanner.nextLine();
                    usuarioDAO.atualizarAtributoUsuario(cpf, 6, novaRua);
                    App.limparTela();
                    System.out.println("\n > Rua atualizada com sucesso! <\n");
                    opcao = 0;
                    break;
                case 7: // ATUALIZAR NÚMERO DA CASA
                    int numeroDaCasa = 0;
                    boolean numeroValido = false;

                    do {
                        System.out.print("| Novo número da casa: ");
                        if (scanner.hasNextInt()) {
                            numeroDaCasa = scanner.nextInt();
                            if (numeroDaCasa < 0) {
                                App.limparTela();
                                System.out.println(
                                        "\n > Número da casa não pode ser negativo. Digite um valor numérico válido. <\n");
                            } else {
                                numeroValido = true;
                            }
                        } else {
                            App.limparTela();
                            System.out.println("\n > Número da casa inválido. Digite um valor numérico. <\n");
                            scanner.next(); // Limpar entrada inválida
                        }
                    } while (!numeroValido);

                    if (usuarioDAO.atualizarAtributoUsuario(cpf, 7, String.valueOf(numeroDaCasa))) {
                        App.limparTela();
                        System.out.println("\n > Número da casa atualizado com sucesso! <\n");
                        opcao = 0;
                    }
                    break;
                case 8: // ATUALIZAR BAIRRO
                    System.out.print("| Novo bairro: ");
                    String novoBairro = scanner.nextLine();
                    usuarioDAO.atualizarAtributoUsuario(cpf, 8, novoBairro);
                    App.limparTela();
                    System.out.println("\n > Bairro atualizado com sucesso! <\n");
                    opcao = 0;
                    break;
                case 9: // ATUALIZAR CIDADE
                    System.out.print("Nova cidade: ");
                    String novaCidade = scanner.nextLine();
                    usuarioDAO.atualizarAtributoUsuario(cpf, 9, novaCidade);
                    App.limparTela();
                    System.out.println("\n > Cidade atualizada com sucesso! <\n");
                    opcao = 0;
                    break;
                case 10: // ATUALIZAR UF
                    String uf;
                    do {
                        System.out.print("| Nova UF: ");
                        uf = scanner.nextLine();
                        // Verifica se a UF tem exatamente 2 caracteres
                        if (uf.length() != 2) {
                            App.limparTela();
                            System.out.println("\n > A UF deve conter exatamente 2 caracteres. <\n");
                            continue; // Volta ao início do loop para uma nova entrada
                        }

                        // Verifica se a UF não contém números
                        if (uf.matches(".*\\d+.*")) {
                            App.limparTela();
                            System.out.println("\n > A UF não deve conter números. <\n");
                            continue; // Volta ao início do loop para uma nova entrada
                        }

                        // Se passar por ambas as verificações, a UF é válida
                        break;
                    } while (true);

                    if (usuarioDAO.atualizarAtributoUsuario(cpf, 10, uf)) {
                        App.limparTela();
                        System.out.println("\n > UF atualizada com sucesso! <\n");
                        opcao = 0;
                    }
                    break;
                case 0:
                    // Sair do menu de atualização
                    App.limparTela();
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }

        } while (opcao != 0);
    }

    // Método que verifica o formato da data de nascimento e se o usuário é maior de
    // idade
    static Date stringParaData(String dataString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dataNascimento = sdf.parse(dataString);

            // Verificar se a data de nascimento é maior de 18 anos
            Calendar calNascimento = Calendar.getInstance();
            calNascimento.setTime(dataNascimento);
            Calendar calAtual = Calendar.getInstance();
            calAtual.add(Calendar.YEAR, -18);

            if (calNascimento.before(calAtual)) {
                return dataNascimento; // Data válida
            } else {
                App.limparTela();
                System.out.println("\n > Você deve ser maior de 18 anos para se cadastrar. Tente novamente. <\n");
                return null; // Data inválida
            }
        } catch (ParseException e) {
            App.limparTela();
            System.out.println(
                    "\n > Erro na conversão de data. Certifique-se de usar o formato AAAA-MM-DD. Tente novamente. <\n");
            return null; // Data inválida
        }
    }

}