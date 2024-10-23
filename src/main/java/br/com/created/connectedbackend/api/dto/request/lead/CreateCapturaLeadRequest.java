package br.com.created.connectedbackend.api.dto.request.lead;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCapturaLeadRequest {

    @NotNull(message = "O ID do lead é obrigatório")
    private UUID leadId;

    @NotNull(message = "Tipo de interação é obrigatório")
    @NotBlank(message = "Tipo de interação não pode estar em branco")
    @Size(max = 50, message = "Tipo de interação deve ter no máximo 50 caracteres")
    private String tipoInteracao;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String descricao;
}