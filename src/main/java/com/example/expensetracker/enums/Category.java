package com.example.expensetracker.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.text.Normalizer;
import java.util.Arrays;

public enum Category {
    ALIMENTACAO("Alimentação"),
    TRANSPORTE("Transporte"),
    LAZER("Lazer"),
    SAUDE("Saúde"),
    MORADIA("Moradia"),
    EDUCACAO("Educação"),
    OUTROS("Outros");

    private final String valor;

    Category(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static Category fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Category não pode ser nula");
        }
        String normalizedValue = normalize(value);
        return Arrays.stream(Category.values())
                .filter(c -> normalize(c.valor).equals(normalizedValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Category inválida: " + value));
    }

    private static String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase();
    }
}
