package com;

public class Test {

    public static void main(String[] args) {

        // saldo >= 1000 && saldo < 3000
        // return saldo = (saldo * 2) %

        double valor = 13152.63;
        double number2 = Math.round(((valor * 2) / 100)) * 100;

        System.out.println("\n" + (int) number2);
    }

}