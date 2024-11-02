package com.example.expensetracker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Categoria {
    ALIMENTACAO("Alimentação"),
    TRANSPORTE("Transporte"),
    LAZER("Lazer"),
    SAUDE("Saúde"),
    MORADIA("Moradia"),
    EDUCACAO("Educação"),
    OUTROS("Outros");

    private final String valor;

    Categoria(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static Categoria fromValue(String value) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.valor.equalsIgnoreCase(value) || categoria.name().equalsIgnoreCase(value)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria inválida: " + value);
    }

}
