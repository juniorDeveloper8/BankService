package com.rober.bank.utils;

import java.time.Year;


public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account Created!";

    public static final String ACCOUNT_CREATION_SUCCESS ="002";
    public static final String ACCOUNT_CREATION_MESSAGE ="Account has been successfully created!";


    public static String generatedAccountNumber() {

        // Obtenemos el año actual
        Year currentYear = Year.now();

        // Establecemos el rango para los números aleatorios de 6 dígitos
        int min = 100000;
        int max = 999999;

        // Generamos un número aleatorio entre min y max

        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

        // Convertimos el año y el número aleatorio en cadenas de texto (Strings)

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accounNumber = new StringBuilder();

        // Devolvemos el número de cuenta generado
        return accounNumber.append(year).append(randomNumber).toString();

    }

}
