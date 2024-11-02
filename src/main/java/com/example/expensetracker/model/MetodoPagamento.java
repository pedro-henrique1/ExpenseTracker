package com.example.expensetracker.model;

import lombok.Getter;

@Getter
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

    private final String descricao;

    MetodoPagamento(String descricao) {
        this.descricao = descricao;
    }

}
