package com.example.expensetracker.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.text.Normalizer;
import java.util.Arrays;

public enum PaymentMethod {
    DINHEIRO("Dinheiro"),
    CARTAOCREDITO("Cartão de Crédito"),
    CARTAODEBITO("Cartão de Débito"),
    TRANSFERENCIA_BANCARIA("Transferência Bancária"),
    PIX("Pix"),
    BOLETO("Boleto"),
    APLICATIVOSPAGAMENTO("Aplicativos de Pagamento"),
    CHEQUE("Cheque"),
    CARTAOBENEFICIOS("Cartão de Benefícios");

    private final String valor;

    PaymentMethod(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return this.valor;
    }

    @JsonCreator
    public static PaymentMethod fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Método de pagamento não pode ser nulo");
        }

        String normalizedValue = normalize(value);

        return Arrays.stream(PaymentMethod.values())
                .filter(m -> normalize(m.valor).equalsIgnoreCase(normalizedValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Método de pagamento inválido: " + value));
    }

    private static String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase();
    }
}
