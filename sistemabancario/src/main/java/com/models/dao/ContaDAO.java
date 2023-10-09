package com.models.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import com.App;
import com.models.entity.Conta;
import com.models.entity.Usuario;

public class ContaDAO {

    private static Connection conexao;

    public ContaDAO(Connection conexao) {
        ContaDAO.conexao = conexao;
    }

    // Método para inserir um novo usuário no banco de dados
    public void inserirConta(Usuario usuario, Conta conta) throws SQLException {
        String sql = "INSERT INTO Conta (cpf, tipoconta, agencia, conta, saldo, senha, statusConta) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Retorna a maior agencia e a maior conta incrementadas
        String agenciaString = "" + obterMaiorAgenciaIncrementada();
        String contaString = "" + obterMaiorContaIncrementada();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getCpf());
            stmt.setString(2, conta.getTipoDaConta());
            stmt.setString(3, agenciaString);
            stmt.setString(4, contaString);
            stmt.setDouble(5, conta.getSaldo());
            stmt.setString(6, conta.getSenha());
            stmt.setBoolean(7, false);

            stmt.executeUpdate();
        }
    }

    // Método para retornar o maior valor da coluna 'agencia' da tabela 'Conta'
    public int obterMaiorAgenciaIncrementada() throws SQLException {
        String sql = "SELECT MAX(agencia) FROM Conta";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                int maiorAgencia = resultSet.getInt(1); // O índice 1 representa a primeira coluna da consulta
                return maiorAgencia == 0 ? 1000 : maiorAgencia + 1;
            }
        }

        return 1000; // Retorna 1000 se não houver registros na tabela 'Conta'
    }

    // Método para retornar o maior valor da coluna 'conta' da tabela 'Conta' e
    // incrementá-lo
    public int obterMaiorContaIncrementada() throws SQLException {
        String sql = "SELECT MAX(conta) FROM Conta";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                int maiorConta = resultSet.getInt(1); // O índice 1 representa a primeira coluna da consulta
                return maiorConta == 0 ? 111110 : maiorConta + 1;
            }
        }

        return 111110; // Retorna 111110 se não houver registros na tabela 'Conta'
    }

    // Método que verifica o CPF e senha do cliente no banco de dados e ver também
    // se o mesmo está ativo
    public boolean validarUsuario(String cpfUsuario, String senhaUsuario) throws SQLException {
        String sql = "SELECT cpf, senha, statusconta FROM Conta WHERE cpf = ? AND senha = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpfUsuario);
            stmt.setString(2, senhaUsuario);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String cpf = resultSet.getString("cpf");
                    String senha = resultSet.getString("senha");
                    Boolean statusConta = resultSet.getBoolean("statusconta");

                    if (cpf.equals(cpfUsuario) && senha.equals(senhaUsuario) && statusConta.equals(false)) {
                        return false;
                    } else if (cpf.equals(cpfUsuario) && senha.equals(senhaUsuario) && statusConta.equals(true)) {
                        return true;
                    }
                }
            }
        }

        return false; // Retorna false se não encontrar uma correspondência de CPF e senha
    }

    // Método que ALTERA o STATUS do usuário (true ou false) no banco de dados
    public void ativarConta(String cpf) throws SQLException {
        String sql = "UPDATE conta SET statusconta = true WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            stmt.executeUpdate();
        }
    }

    public void desbloquearConta(String cpf) throws SQLException {
        String sql = "UPDATE conta SET statusconta = true WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            stmt.executeUpdate();
        }
    }

    // Método que SUBTRAI um valor do banco de dados
    public boolean removerValor(String cpf, double valor) throws SQLException {
        // Verificar o saldo da conta de origem
        String sqlSaldoConta = "SELECT saldo FROM conta WHERE cpf = ?";
        double saldoConta = 0;

        try (PreparedStatement stmtSaldoOrigem = conexao.prepareStatement(sqlSaldoConta)) {
            stmtSaldoOrigem.setString(1, cpf);

            try (ResultSet resultSet = stmtSaldoOrigem.executeQuery()) {
                if (resultSet.next()) {
                    saldoConta = resultSet.getDouble("saldo");
                }
            }
        }

        if (saldoConta >= valor) {
            // Atualizar o saldo da conta subtraindo o valor do saque
            String sqlUpdate = "UPDATE conta SET saldo = saldo - ? WHERE cpf = ?";

            try (PreparedStatement updateStmt = conexao.prepareStatement(sqlUpdate)) {
                updateStmt.setDouble(1, valor); // Define o valor a ser subtraído do saldo
                updateStmt.setString(2, cpf); // Define o CPF da conta a ser atualizada

                int linhasAfetadas = updateStmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    // Se a atualização afetou uma ou mais linhas, o saque foi bem-sucedido
                    System.out.printf("\n> Saque de R$%.2f realizado com sucesso. <\n\n", valor);
                    registrarTransacao(cpf, "Saque", -valor);
                    return true;
                }
            }
        } else {
            // Se nenhuma linha foi afetada, o saldo era insuficiente
            System.out.println("\n> Saldo insuficiente para a operação. <\n");
        }
        return false;
    }

    // Método que ADICIONA um valor do banco de dados
    public void adicionarValor(String cpf, double valor) throws SQLException {
        // Atualizar o saldo da conta
        String sql = "UPDATE conta SET saldo = saldo + ? WHERE cpf = ?";

        try (PreparedStatement updateStmt = conexao.prepareStatement(sql)) {
            updateStmt.setDouble(1, valor);
            updateStmt.setString(2, cpf);

            int linhasAfetadas = updateStmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.printf("\n> Depósito de R$%.2f realizado com sucesso. <\n\n", valor);
                registrarTransacao(cpf, "Deposito", valor);
            }
        }
    }

    // Método que TRANSFERE um valor de uma conta para outra no banco de dados
    public boolean transferirValor(String contaOrigem, String contaDestino, double valor) throws SQLException {
        // Verificar se a conta de destino está ativa
        String sqlContaAtiva = "SELECT statusconta FROM conta WHERE cpf = ?";
        boolean contaDestinoAtiva = false;

        try (PreparedStatement stmtContaAtiva = conexao.prepareStatement(sqlContaAtiva)) {
            stmtContaAtiva.setString(1, contaDestino);

            try (ResultSet resultSet = stmtContaAtiva.executeQuery()) {
                if (resultSet.next()) {
                    contaDestinoAtiva = resultSet.getBoolean("statusconta");
                }
            }
        }

        if (!contaDestinoAtiva) {
            App.limparTela();
            return false;
        }

        // Verificar o saldo da conta de origem
        String sqlSaldoConta = "SELECT saldo FROM conta WHERE cpf = ?";
        double saldoContaOrigem = 0;

        try (PreparedStatement stmtSaldoOrigem = conexao.prepareStatement(sqlSaldoConta)) {
            stmtSaldoOrigem.setString(1, contaOrigem);

            try (ResultSet resultSet = stmtSaldoOrigem.executeQuery()) {
                if (resultSet.next()) {
                    saldoContaOrigem = resultSet.getDouble("saldo");
                }
            }
        }

        // Verificar se a conta de origem possui saldo suficiente
        if (saldoContaOrigem >= valor) {
            // Atualizar o saldo da conta de origem (subtrair o valor transferido)
            String sqlAtualizarContaOrigem = "UPDATE conta SET saldo = saldo - ? WHERE cpf = ?";

            try (PreparedStatement stmtAtualizarOrigem = conexao.prepareStatement(sqlAtualizarContaOrigem)) {
                stmtAtualizarOrigem.setDouble(1, valor);
                stmtAtualizarOrigem.setString(2, contaOrigem);

                stmtAtualizarOrigem.executeUpdate();
            }

            // Atualizar o saldo da conta de destino (adicionar o valor transferido)
            String sqlAtualizarContaDestino = "UPDATE conta SET saldo = saldo + ? WHERE cpf = ?";

            try (PreparedStatement stmtAtualizarDestino = conexao.prepareStatement(sqlAtualizarContaDestino)) {
                stmtAtualizarDestino.setDouble(1, valor);
                stmtAtualizarDestino.setString(2, contaDestino);

                stmtAtualizarDestino.executeUpdate();
            }

            // Registrar a transação de transferência para ambas as contas
            registrarTransacao(contaOrigem, "Envio de Transferência", -valor);
            registrarTransacao(contaDestino, "Recebimento de Transferência", valor);
            return true;
        } else {
            App.limparTela();
            System.out.println("\n > Saldo insuficiente para realizar a transferência! <\n");
            return false;
        }
    }

    // Método que RETORNA as informações da conta destino (no processo de
    // transferência)
    public void obterDadosUsuarioContaPorCPF(String cpf) throws SQLException {
        String sql = "SELECT u.nome, u.cpf, c.agencia, SUBSTRING(c.conta, 1, 5) AS numero_conta, SUBSTRING(c.conta, 6, 1) AS digito FROM Usuarios u INNER JOIN Conta c ON u.cpf = c.cpf WHERE u.cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String nome = resultSet.getString("nome");
                    String cpfUsuario = resultSet.getString("cpf");
                    String agencia = resultSet.getString("agencia");
                    String numeroConta = resultSet.getString("numero_conta");
                    String digito = resultSet.getString("digito");

                    System.out.println("+----------------------------------+");
                    System.out.println("|     C O N T A  D E S T I N O     |");
                    System.out.println("+----------------------------------+");
                    System.out.println("| Nome: " + nome);
                    System.out.println("| CPF: " + cpfUsuario);
                    System.out.println("| Agencia: " + agencia);
                    System.out.println("| Conta: " + numeroConta + "-" + digito);
                    System.out.println("+----------------------------------+");

                }
            }
        }
    }

    // Método que REGISTRA as TRANSAÇÕES no banco de dados
    public void registrarTransacao(String cpf, String descricao, double valor) throws SQLException {
        // Obtém a data atual
        LocalDate data = LocalDate.now();

        // Consulta SQL para inserir a transação na tabela 'transacoes' com data e hora
        String sql = "INSERT INTO transacoes (cpf, descricao, valor, data) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cpf);
            pstmt.setString(2, descricao);
            pstmt.setDouble(3, valor);
            pstmt.setDate(4, java.sql.Date.valueOf(data));

            pstmt.executeUpdate();
        }
    }

    // Método que retorna o EXTRATO (transações) realizadas em um período X
    public void exibirTransacoes(String cpf, Date dataInicio, Date dataFim) throws SQLException {
        String sql = "SELECT * FROM transacoes WHERE cpf = ? AND data BETWEEN ? AND ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setDate(2, new java.sql.Date(dataInicio.getTime())); // Converte a data de início para java.sql.Date
            stmt.setDate(3, new java.sql.Date(dataFim.getTime())); // Converte a data de fim para java.sql.Date

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    String descricao = resultSet.getString("descricao");
                    double valor = resultSet.getDouble("valor");
                    Date data = resultSet.getDate("data"); // Use getDate para recuperar a data corretamente

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String dataFormatada = dateFormat.format(data); // Formate a data para exibição

                    System.out.println("+-------------------------------------------+");
                    System.out.println("| Descrição: " + descricao);
                    System.out.println("| Valor: " + valor);
                    System.out.println("| Data: " + dataFormatada);
                }
            }
        }
    }

    // Método que exclui todas as informações relacionadas a um CPF no banco de dados
    // (Transacoes, Emprestimo, Conta e Usuarios)
    public boolean excluirDados(String cpf) throws SQLException {
        String sqlTransacoes = "DELETE FROM Transacoes WHERE cpf = ?";
        String sqlEmprestimo = "DELETE FROM Emprestimo WHERE cpf = ?";
        String sqlConta = "DELETE FROM Conta WHERE cpf = ?";
        String sqlUsuarios = "DELETE FROM Usuarios WHERE cpf = ?";

        try (PreparedStatement stmtTransacoes = conexao.prepareStatement(sqlTransacoes);
                PreparedStatement stmtEmprestimo = conexao.prepareStatement(sqlEmprestimo);
                PreparedStatement stmtConta = conexao.prepareStatement(sqlConta);
                PreparedStatement stmtUsuarios = conexao.prepareStatement(sqlUsuarios)) {

            stmtTransacoes.setString(1, cpf);
            stmtEmprestimo.setString(1, cpf);
            stmtConta.setString(1, cpf);
            stmtUsuarios.setString(1, cpf);

            int linhasAfetadasTransacoes = stmtTransacoes.executeUpdate();
            int linhasAfetadasEmprestimo = stmtEmprestimo.executeUpdate();
            int linhasAfetadasConta = stmtConta.executeUpdate();
            int linhasAfetadasUsuarios = stmtUsuarios.executeUpdate();

            if (linhasAfetadasTransacoes > 0 && linhasAfetadasEmprestimo > 0 && linhasAfetadasConta > 0
                    && linhasAfetadasUsuarios > 0) {
                App.limparTela();
                System.out.println(
                        "\n > Todas as informações relacionadas ao CPF " + cpf + " foram excluídas com sucesso. <\n");
                return true;
            }
        }

        App.limparTela();
        System.out
                .println("\n > Todas as informações relacionadas ao CPF " + cpf + " foram excluídas com sucesso. <\n");
        return false;
    }

    // Método que ALTERA a SENHA de uma cota no banco de dados
    public void mudarSenha(String cpf, String novaSenha) throws SQLException {
        String sql = "UPDATE Conta SET senha = ? WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, novaSenha);
            stmt.setString(2, cpf);

            stmt.executeUpdate();
        }
    }

    // Método que verifica a existência de uma conta no banco de dados pelo CPF
    public Conta buscarContaPorCPF(String cpf) throws SQLException {
        String sql = "SELECT * FROM Conta WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String senha = resultSet.getString("senha");

                    UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);
                    Usuario usuario = usuarioDAO.buscarPorCPF(cpf);
                    // Agora cria o objeto Conta com os valores obtidos
                    Conta conta = new Conta(usuario, senha);
                    conta.setUsuario(usuario);

                    return conta;
                }
            }
        }

        return null;
    }

    // Método para obter a agência por CPF
    public String obterAgenciaPorCPF(String cpf) throws SQLException {
        String agencia = null;
        String sql = "SELECT agencia FROM Conta WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    agencia = resultSet.getString("agencia");
                }
            }
        }

        return agencia;
    }

    // Método para obter o número da conta por CPF
    public String obterNumeroDaContaPorCPF(String cpf) throws SQLException {
        String numeroConta = null;
        String sql = "SELECT conta FROM Conta WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    numeroConta = resultSet.getString("conta");
                }
            }
        }

        return numeroConta;
    }

    // Método para obter o saldo por CPF
    public double obterSaldoPorCPF(String cpf) throws SQLException {
        double saldo = 0.0;
        String sql = "SELECT saldo FROM Conta WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    saldo = resultSet.getDouble("saldo");
                }
            }
        }

        return saldo;
    }

    // Método para obter o tipo da conta por CPF
    public String obterTipoDaContaPorCPF(String cpf) throws SQLException {
        String tipoDaConta = null;
        String sql = "SELECT tipoconta FROM Conta WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    tipoDaConta = resultSet.getString("tipoconta");
                }
            }
        }

        return tipoDaConta;
    }

    // Método para obter a data de cadastro por CPF
    public LocalDate obterDataCadastroPorCPF(String cpf) throws SQLException {
        LocalDate dataCadastro = null;
        String sql = "SELECT dataCadastro FROM Conta WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    dataCadastro = resultSet.getDate("dataCadastro").toLocalDate();
                }
            }
        }

        return dataCadastro;
    }
}