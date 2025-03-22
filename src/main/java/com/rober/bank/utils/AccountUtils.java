package com.rober.bank.utils;

import java.time.Year;


public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account Created!";

    public static final String ACCOUNT_CREATION_SUCCESS ="002";
    public static final String ACCOUNT_CREATION_MESSAGE ="Account has been successfully created!";


    public static String generatedAccountNumber() {

        /**
         * ex. 2024 + randomSixDigits
         */

        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        // generamos el los digitos random con el min & max

        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

        // convertimos el actual y numero random en string, los conetamos

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accounNumber = new StringBuilder();

        return accounNumber.append(year).append(randomNumber).toString();

    }

}
