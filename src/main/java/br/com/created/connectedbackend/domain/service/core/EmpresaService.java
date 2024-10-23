package br.com.created.connectedbackend.domain.service.core;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.core.Empresa;
import br.com.created.connectedbackend.domain.repository.core.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public Empresa createEmpresa(Empresa empresa) {
        if (empresaRepository.existsByCnpjAndDeletedAtIsNull(empresa.getCnpj())) {
            throw new BusinessException("Já existe uma empresa cadastrada com este CNPJ");
        }
        return empresaRepository.save(empresa);
    }

    public Empresa updateEmpresa(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public Empresa findEmpresaById(UUID id) {
        return empresaRepository.findByIdOrCnpjOrRazaoSocialContaining(id, null, null)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));
    }

    public Optional<Empresa> findEmpresaByIdOrCnpjOrRazaoSocial(UUID id, String cnpj, String termo) {
        return empresaRepository.findByIdOrCnpjOrRazaoSocialContaining(id, cnpj, termo);
    }

    public void deleteEmpresa(Empresa empresa) {
        empresa.setDeletedAt(LocalDateTime.now());
        empresaRepository.save(empresa);
    }
}