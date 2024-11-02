package com.example.expensetracker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum MetodoPagamento {
    DINHEIRO("Dinheiro"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
    TRANSFERENCIA_BANCARIA("Transferência Bancária"),
    PIX("Pix"),
    BOLETO("Boleto"),
    APLICATIVOS_PAGAMENTO("Aplicativos de Pagamento"),
    CHEQUE("Cheque"),
    CARTAO_BENEFICIOS("Cartão de Benefícios");


    private final String valor;

    MetodoPagamento(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static MetodoPagamento fromValue(String value) {
        for (MetodoPagamento metodo : MetodoPagamento.values()) {
            if (metodo.valor.equalsIgnoreCase(value) || metodo.name().equalsIgnoreCase(value)) {
                return metodo;
            }
        }
        throw new IllegalArgumentException("Método de pagamento inválido: " + value);
    }

}
