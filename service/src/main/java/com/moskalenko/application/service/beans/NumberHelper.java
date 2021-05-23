package com.moskalenko.application.service.beans;

public class NumberHelper {
    public static String createAccountNumber(){
        final Long firstHalf = 9999999 + (long) (Math.random()*(99999999-9999999));
        final Long secondHalf = 9999999 + (long) (Math.random()*(99999999-9999999));
        final Long thirdHalf = 999+ (long) (Math.random()*(9999-999));
        final StringBuilder accountNumber = new StringBuilder().append(firstHalf).append(secondHalf).append(thirdHalf);

        return accountNumber.toString();
    }

    public static String createCardNumber(){
        final Long firstHalf = 9999999 + (long) (Math.random() * (99999999-9999999));
        final Long secondHalf = 9999999 + (long) (Math.random() * (99999999-9999999));
        final StringBuilder cardNumber = new StringBuilder().append(firstHalf).append(secondHalf);
        return cardNumber.toString();
    }

}
