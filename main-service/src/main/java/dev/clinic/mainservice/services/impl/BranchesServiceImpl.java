package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.BranchMapper;
import dev.clinic.mainservice.models.entities.Branches;
import dev.clinic.mainservice.models.enums.AppointmentType;
import dev.clinic.mainservice.repositories.BranchesRepository;
import dev.clinic.mainservice.services.BranchesService;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchesServiceImpl implements BranchesService {

    private final BranchesRepository branchesRepository;

    private static final Logger log = LoggerFactory.getLogger(BranchesServiceImpl.class);

    public BranchesServiceImpl(BranchesRepository branchesRepository) {
        this.branchesRepository = branchesRepository;
    }

    /**
     * Получения списка всех филиалов (подразделений) ветеринатрной клиники
     * @return возвращает список найденных филиалов.
     */
    @Override
    @Cacheable(value = "branches", key = "'all'")
    public List<BranchResponse> getAllBranches() {
        log.info("Get all branches was called");
        try {
            List<BranchResponse> result = branchesRepository.findAll().stream()
                    .map(BranchMapper::toResponse)
                    .collect(Collectors.toList());
            log.debug("Fetched {} branches", result.size());

            return result;

        } catch (DataAccessException ex) {
            log.error("Database error in getAllBranches", ex);
            throw new ServiceException("Database error while fetching all branches", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in getAllBranches", ex);
            throw new ServiceException("Unexpected error while fetching all branches", ex);
        }
    }

    /**
     * Получения списка всех филиалов (подразделений) ветеринатрной клиники в зависимости от выбранной услуги
     * @param service тип услуги
     * @return возвращает список найденных филиалов по установленному параметру
     */
    @Override
    @Cacheable(value = "branches", key = "#service.name()")
    public List<BranchResponse> getAllBranchesByServiceName(AppointmentType service) {
        if (service == null) {
            throw new IllegalArgumentException("Service must not be null");
        }
        try {
            List<BranchResponse> result = branchesRepository.getAllBranchesByServicesContains(service)
                    .stream()
                    .map(BranchMapper::toResponse)
                    .collect(Collectors.toList());
            log.debug("Fetched {} branches by services", result.size());

            return result;

        } catch (DataAccessException ex) {
            log.error("Database error in getAllBranchesByServiceName", ex);
            throw new ServiceException("Database error while fetching all branches", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in getAllBranchesByServiceName", ex);
            throw new ServiceException("Unexpected error while fetching all branches", ex);
        }
    }

    /**
     * Создание филиала (подразделеня) ветеринатрной клиники
     * @param branchRequest объект для создания филиала
     * @return возвращает созданный филиал
     */
    @Override
    @CacheEvict(value = "branches", allEntries = true)
    public BranchResponse createBranch(BranchRequest branchRequest) {
        log.info("Create branch was called");
        try {
            Branches branch = BranchMapper.toRequest(branchRequest);

            Branches savedBranch = branchesRepository.save(branch);
            log.info("Branch was created with id: {}", savedBranch.getId());

            return BranchMapper.toResponse(savedBranch);

        } catch (ResourceNotFoundException | IllegalArgumentException ex) {
            log.error("Validation error in createBranch: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in createBranch", ex);
            throw new ServiceException("Database error while creating branch", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in createBranch", ex);
            throw new ServiceException("Unexpected error while creating branch", ex);
        }
    }

    /**
     * Получение конктретного филиала (подразделеня) ветеринатрной клиники
     * @param id уникальный идентификатор филиала
     * @return возвращает найденный филиал
     */
    @Override
    public BranchResponse getBranchById(Long id) {
        log.info("Get branch was called");
        try {
            Branches branch = branchesRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Branch not found with id = {}", id);
                        return new ResourceNotFoundException("Branch not found with id: " + id);
                    });

            log.info("Branch found with id {}", id);

            return BranchMapper.toResponse(branch);

        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            log.error("Error in getBranchById: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in getBranchById for id = {}", id, ex);
            throw new ServiceException("Unexpected error while retrieving branch", ex);
        }
    }

    /**
     * Редактирование филиала (подразделеня) ветеринатрной клиники
     * @param branchRequest объект для создания филиала
     * @param id уникальный идентификатор филиала
     * @return возвращает отредактированный филиал
     */
    @Override
    @CacheEvict(value = "branches", allEntries = true)
    public BranchResponse editBranch(Long id, BranchRequest branchRequest) {
        log.info("Edit branch was called");
        try {
            Branches branch = branchesRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Branch not found with id = {}", id);
                        return new ResourceNotFoundException("Branch not found with id: " + id);
                    });

            BranchMapper.updateFromRequest(branch, branchRequest);
            Branches savedBranch = branchesRepository.save(branch);
            log.info("Branch edited with id {}", id);

            return BranchMapper.toResponse(branchesRepository.save(savedBranch));

        } catch (ResourceNotFoundException | AccessDeniedException | IllegalArgumentException ex) {
            log.error("Error in editPet: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in editPet for id = {}", id, ex);
            throw new ServiceException("Database error while editing pet", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in editPet for id = {}", id, ex);
            throw new ServiceException("Unexpected error while editing pet", ex);
        }
    }

    /**
     * Удаление филиала (подразделеня) ветеринатрной клиники.
     * @param id уникальный идентификатор филиала
     */
    @Override
    @CacheEvict(value = "branches", allEntries = true)
    public void deleteBranch(Long id) {
        log.info("Delete branch was called");
        try {
            Branches branch = branchesRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Branch not found with id = {}", id);
                        return new ResourceNotFoundException("Branch not found with id: " + id);
                    });
            branchesRepository.delete(branch);

        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            log.error("Error in deleteBranch: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in deleteBranch for id = {}", id, ex);
            throw new ServiceException("Database error while deleting pet", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in deleteBranch for id = {}", id, ex);
            throw new ServiceException("Unexpected error while deleting pet", ex);
        }
    }
}
