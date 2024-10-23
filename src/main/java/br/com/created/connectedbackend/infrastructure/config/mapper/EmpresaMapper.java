package br.com.created.connectedbackend.infrastructure.config.mapper;

import br.com.created.connectedbackend.api.dto.request.core.CreateEmpresaRequest;
import br.com.created.connectedbackend.api.dto.request.core.UpdateEmpresaRequest;
import br.com.created.connectedbackend.api.dto.response.core.EmpresaResponse;
import br.com.created.connectedbackend.domain.model.core.Empresa;
import br.com.created.connectedbackend.domain.service.address.EnderecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmpresaMapper {

    private final EnderecoService enderecoService;
    private final EnderecoMapper enderecoMapper;

    public Empresa toEmpresa(CreateEmpresaRequest request) {
        return Empresa.builder()
                .razaoSocial(request.getRazaoSocial())
                .nomeFantasia(request.getNomeFantasia())
                .cnpj(request.getCnpj())
                .telefone(request.getTelefone())
                .endereco(enderecoService.findById(request.getEnderecoId()).orElse(null))
                .build();
    }

    public Empresa toEmpresa(UpdateEmpresaRequest request) {
        return Empresa.builder()
                .razaoSocial(request.getRazaoSocial())
                .nomeFantasia(request.getNomeFantasia())
                .cnpj(request.getCnpj())
                .telefone(request.getTelefone())
                .endereco(enderecoService.findById(request.getEnderecoId()).orElse(null))
                .build();
    }

    public EmpresaResponse toEmpresaResponse(Empresa empresa) {
        return EmpresaResponse.builder()
                .id(empresa.getId())
                .razaoSocial(empresa.getRazaoSocial())
                .nomeFantasia(empresa.getNomeFantasia())
                .cnpj(empresa.getCnpj())
                .telefone(empresa.getTelefone())
                .endereco(enderecoMapper.toEnderecoResponse(empresa.getEndereco()))
                .createdAt(empresa.getCreatedAt())
                .updatedAt(empresa.getUpdatedAt())
                .deletedAt(empresa.getDeletedAt())
                .ativo(empresa.getAtivo())
                .build();
    }
}