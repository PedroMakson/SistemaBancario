package com.models.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.App;
import com.models.entity.Emprestimo;

public class EmprestimoDAO {

    private Connection conexao;

    public EmprestimoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserirEmprestimo(Emprestimo emprestimo) throws SQLException {
        String sql = "INSERT INTO emprestimo (cpf, documento, dataemissao, valortotal, quantidadeparcelas, valorjuros, datavencimento, parcelaatual, valorparcela, valorrecebido, statusparcela) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int maiorDocumento = obterMaiorDocumento();
        if (maiorDocumento == 0) {
            maiorDocumento = 999;
        }
        maiorDocumento++;
        String documentoString = "DOC" + maiorDocumento;

        int parcelaAtual = emprestimo.getParcelaAtual();
        LocalDate dataParcela = emprestimo.getDataEmissao();

        for (int i = 1; i <= emprestimo.getQuantidadeParcelas(); i++) {

            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                dataParcela = dataParcela.plusDays(30);
                parcelaAtual++;

                stmt.setString(1, emprestimo.getConta().getUsuario().getCpf());
                stmt.setString(2, documentoString);
                stmt.setDate(3, Date.valueOf(emprestimo.getDataEmissao()));
                stmt.setDouble(4, emprestimo.getValorTotal());
                stmt.setInt(5, emprestimo.getQuantidadeParcelas());
                stmt.setDouble(6, emprestimo.getValorJuros());
                stmt.setDate(7, java.sql.Date.valueOf(dataParcela));
                stmt.setInt(8, parcelaAtual);
                stmt.setDouble(9, emprestimo.getValorParcela());
                stmt.setDouble(10, emprestimo.getValorRecebido());
                stmt.setBoolean(11, emprestimo.isStatusParcela());

                stmt.executeUpdate();
            }

        }
    }

    // Método para retornar o maior documento da coluna 'documento' da tabela 'Emprestimo'
    public int obterMaiorDocumento() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(documento, 4) AS INTEGER)) FROM emprestimo";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    // Método que exibe todos os empréstimos para determinado CPF
    public void exibirEmprestimosPorCPF(String cpf) throws SQLException {
        String sql = "SELECT documento, datavencimento, parcelaatual, valorparcela, statusparcela FROM emprestimo WHERE cpf = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet resultSet = stmt.executeQuery()) {
                App.limparTela();
                System.out.println("\n+-------------------------------------------------------------------+");
                System.out.println("| Documento | Data vencimento | Parcela |   Valor(R$)   |   Status  |");
                System.out.println("+-------------------------------------------------------------------+");

                while (resultSet.next()) {
                    String documento = resultSet.getString("documento");
                    String datavencimento = resultSet.getString("datavencimento");
                    int parcelaatual = resultSet.getInt("parcelaatual");
                    double valorparcela = resultSet.getDouble("valorparcela");
                    boolean statusparcela = resultSet.getBoolean("statusparcela");

                    System.out.printf("|  " + documento);
                    System.out.printf("  |    " + datavencimento);
                    System.out.printf("   |    " + parcelaatual);
                    System.out.printf("    |   R$%.2f", valorparcela);
                    System.out.printf("   |  " + (statusparcela ? "  Pago   |" : "Não Pago |"));
                    System.out.println();
                }
                System.out.println("+-------------------------------------------------------------------+\n");

            }
        }
    }

    // Método para pagar uma parcela se o status for falso
    public void pagarParcela(String cpf, String documento, int numeroParcela) throws SQLException {
        // Verificar o status da parcela
        boolean statusParcela = verificarStatusParcela(cpf, documento, numeroParcela);

        if (!statusParcela) {
            // Se o status for falso, marcar como paga
            String sql = "UPDATE emprestimo SET statusparcela = true WHERE cpf = ? AND documento = ? AND parcelaatual = ?";
            String sql2 = "UPDATE emprestimo SET valorrecebido = valorparcela WHERE cpf = ? AND documento = ? AND parcelaatual = ?";

            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setString(1, cpf);
                stmt.setString(2, documento);
                stmt.setInt(3, numeroParcela);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conexao.prepareStatement(sql2)) {
                stmt.setString(1, cpf);
                stmt.setString(2, documento);
                stmt.setInt(3, numeroParcela);
                stmt.executeUpdate();
            }
            App.limparTela();
            System.out.println("\n > Parcela paga com sucesso. <\n");
        } else {
            App.limparTela();
            System.out.println("\n > A parcela já está paga. <\n");
        }
    }

    // Método para verificar o status da parcela
    private boolean verificarStatusParcela(String cpf, String documento, int numeroParcela) throws SQLException {
        String sql = "SELECT statusparcela FROM emprestimo WHERE cpf = ? AND documento = ? AND parcelaatual = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, documento);
            stmt.setInt(3, numeroParcela);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("statusparcela");
                }
            }
        }

        return false; // Se não encontrou a parcela, considera como não paga
    }

    // Método para verificar se um documento existe no banco
    public boolean documentoExiste(String documento) throws SQLException {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE documento = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, documento);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Retorna true se o documento existe
                }
            }
        }

        return false; // Retorna false se não encontrou o documento
    }

    // Método para verificar se uma parcela existe para um documento específico
    public boolean parcelaExisteParaDocumento(String documento, int numeroParcela) throws SQLException {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE documento = ? AND parcelaatual = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, documento);
            stmt.setInt(2, numeroParcela);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Retorna true se o count for maior que 0 (parcela existe)
                }
            }
        }
        return false; // A parcela não existe
    }

    // Método que verifica se todas as parcelas de empréstimo estão pagas (status
    // TRUE) para um CPF específico
    public boolean existeEmprestimoParaCPF(String cpf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE cpf = ? AND statusparcela = FALSE";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int totalParcelas = resultSet.getInt(1);
                    return totalParcelas > 0; // Retorna true se todas as parcelas estão pagas (status TRUE)
                }
            }
        }

        return false; // Retorna false se não houver parcelas para o CPF especificado
    }
}