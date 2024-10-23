package br.com.created.connectedbackend.api.dto.request.lead;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLeadRequest {

    private Short statusId;

    @Size(max = 255, message = "A localização deve ter no máximo 255 caracteres")
    private String localizacao;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String descricao;
}