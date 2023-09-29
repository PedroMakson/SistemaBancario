package com.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

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
            System.out.println("+----------------------+");
            System.out.println("| Cadastro de usuário. |");
            System.out.println("+----------------------+");

            // Solicita ao usuário que insira os dados
            System.out.print("-> CPF: ");
            String cpf = scanner.nextLine();

            System.out.print("-> Nome completo: ");
            String nome = scanner.nextLine();

            System.out.print("-> RG: ");
            String rg = scanner.nextLine();

            System.out.print("-> Renda mensal (R$): ");
            double renda = scanner.nextDouble();
            scanner.nextLine(); // Consumir a quebra de linha

            System.out.print("-> Telefone: ");
            String telefone = scanner.nextLine();

            System.out.print("-> Email: ");
            String email = scanner.nextLine();

            Date dataNascimento = null;
            while (dataNascimento == null) {
                System.out.print("-> Data de Nascimento (AAAA-MM-DD): ");
                String dataNascimentoStr = scanner.nextLine();
                dataNascimento = stringParaData(dataNascimentoStr);
            }

            System.out.print("-> CEP: ");
            String cep = scanner.nextLine();

            System.out.print("-> Rua: ");
            String rua = scanner.nextLine();

            System.out.print("-> Número da Casa: ");
            int numeroDaCasa = Integer.parseInt(scanner.nextLine());

            System.out.print("-> Bairro: ");
            String bairro = scanner.nextLine();

            System.out.print("-> Cidade: ");
            String cidade = scanner.nextLine();

            System.out.print("-> UF: ");
            String uf = scanner.nextLine();

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
            System.out.println("+------------------------+");
            System.out.println("| Informações do Usuário |");
            System.out.println("+------------------------+");

            System.out.println("CPF: " + usuario.getCpf());
            System.out.println("Nome: " + usuario.getNome());
            System.out.println("RG: " + usuario.getRg());
            System.out.println("Telefone: " + usuario.getTelefone());
            System.out.println("Email: " + usuario.getEmail());
            System.out.println("Data de Nascimento: " + usuario.getDataNascimento());
            System.out.println("CEP: " + usuario.getCep());
            System.out.println("Rua: " + usuario.getRua());
            System.out.println("Número da Casa: " + usuario.getNumeroDaCasa());
            System.out.println("Bairro: " + usuario.getBairro());
            System.out.println("Cidade: " + usuario.getCidade());
            System.out.println("UF: " + usuario.getUf());

            System.out.println("+----------------------+\n");
        } else {
            System.out.println("Usuário com CPF " + cpf + " não encontrado.");
        }
    }
   
    // Método que verifica o formato da data
    private static Date stringParaData(String dataString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dataString);
        } catch (ParseException e) {
            System.out.println(
                    "\n*** Erro na conversão de data. Certifique-se de usar o formato AAAA-MM-DD. Tente novamente. ***\n");
            return null;
        }
    }
}