package com.example.expensetracker.model;

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

    private final String descricao;

    Categoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
