package br.com.created.connectedbackend.domain.service.core;

import br.com.created.connectedbackend.domain.exception.BusinessException;
import br.com.created.connectedbackend.domain.exception.NotFoundException;
import br.com.created.connectedbackend.domain.model.core.Empresa;
import br.com.created.connectedbackend.domain.repository.core.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional
    public Empresa createEmpresa(Empresa empresa) {
        if (empresaRepository.existsByCnpjAndDeletedAtIsNull(empresa.getCnpj())) {
            throw new BusinessException("Já existe uma empresa ativa com este CNPJ");
        }

        empresa.setAtivo(true);
        empresa.setCreatedAt(LocalDateTime.now());
        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa updateEmpresa(Empresa empresa) {
        var existingEmpresa = findById(empresa.getId());

        // Verifica se o CNPJ está sendo alterado e se já existe
        if (!existingEmpresa.getCnpj().equals(empresa.getCnpj()) &&
                empresaRepository.existsByCnpjAndDeletedAtIsNull(empresa.getCnpj())) {
            throw new BusinessException("Já existe uma empresa ativa com este CNPJ");
        }

        empresa.setUpdatedAt(LocalDateTime.now());
        return empresaRepository.save(empresa);
    }

    @Transactional(readOnly = true)
    public Empresa findById(UUID id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Empresa> findAllAtivas() {
        return empresaRepository.findByAtivoTrueAndDeletedAtIsNull();
    }

    @Transactional
    public void deleteEmpresa(UUID id) {
        var empresa = findById(id);
        empresa.setAtivo(false);
        empresa.setDeletedAt(LocalDateTime.now());
        empresaRepository.save(empresa);
    }

    @Transactional(readOnly = true)
    public boolean existsByCnpj(String cnpj) {
        return empresaRepository.existsByCnpjAndDeletedAtIsNull(cnpj);
    }
}