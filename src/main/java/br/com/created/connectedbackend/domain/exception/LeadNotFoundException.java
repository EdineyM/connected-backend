package br.com.created.connectedbackend.domain.exception;

import java.util.UUID;

public class LeadNotFoundException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Lead não encontrado";

    public LeadNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public LeadNotFoundException(UUID id) {
        super(String.format("Lead não encontrado com o ID: %s", id));
    }

    public LeadNotFoundException(String message) {
        super(message);
    }
}