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
public class CreateAudioDescricaoRequest {

    @NotNull(message = "O ID do lead é obrigatório")
    private UUID leadId;

    @NotNull(message = "A URL do áudio é obrigatória")
    @NotBlank(message = "A URL do áudio não pode estar em branco")
    @Size(max = 512, message = "A URL do áudio deve ter no máximo 512 caracteres")
    private String urlS3;

    @Size(max = 5000, message = "A transcrição deve ter no máximo 5000 caracteres")
    private String transcricao;
}