package com;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.models.entity.Conexao;

public class App {

    public static void main(String[] args) throws SQLException {

        Connection conexao = Conexao.getInstancia();

    }
}