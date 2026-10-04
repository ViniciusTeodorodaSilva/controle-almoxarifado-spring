package br.com.almoxarifado.service;

import br.com.almoxarifado.model.Almoxarifado;
import br.com.almoxarifado.repository.AlmoxarifadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlmoxarifadoService {

    private final AlmoxarifadoRepository repository;

    public AlmoxarifadoService(AlmoxarifadoRepository repository) {
        this.repository = repository;
    }

    public List<Almoxarifado> listar() {
        return repository.findAll();
    }

    public Optional<Almoxarifado> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Almoxarifado cadastrar(Almoxarifado almoxarifado) {
        if (almoxarifado.getNome() == null || almoxarifado.getNome().isBlank()) {

            throw new IllegalArgumentException("Nome do almoxarifado inválido");
        }
        return repository.save(almoxarifado);
    }

}
