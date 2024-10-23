package br.com.created.connectedbackend.domain.exception;

public class DuplicatedLeadException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Já existe um lead ativo para este participante";

    public DuplicatedLeadException() {
        super(DEFAULT_MESSAGE);
    }

    public DuplicatedLeadException(String message) {
        super(message);
    }
}