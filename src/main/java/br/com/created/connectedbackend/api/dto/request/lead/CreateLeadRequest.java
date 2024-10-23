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
public class CreateLeadRequest {

    @NotNull(message = "O ID do ingresso do participante é obrigatório")
    private UUID ingressoParticipanteId;

    @NotNull(message = "O status do lead é obrigatório")
    private Short statusId;

    @Size(max = 255, message = "A localização deve ter no máximo 255 caracteres")
    private String localizacao;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    @NotNull(message = "Tipo de interação inicial é obrigatório")
    @NotBlank(message = "Tipo de interação inicial não pode estar em branco")
    @Size(max = 50, message = "Tipo de interação deve ter no máximo 50 caracteres")
    private String tipoInteracaoInicial;

    @Size(max = 1000, message = "A descrição da interação deve ter no máximo 1000 caracteres")
    private String descricaoInteracao;
}