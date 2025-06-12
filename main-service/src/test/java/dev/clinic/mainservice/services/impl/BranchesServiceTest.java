package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;
import dev.clinic.mainservice.mapping.BranchMapper;
import dev.clinic.mainservice.models.entities.Branches;
import dev.clinic.mainservice.models.enums.AppointmentType;
import dev.clinic.mainservice.repositories.BranchesRepository;
import dev.clinic.mainservice.services.BranchesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BranchesServiceTest {

    @Mock
    private BranchesRepository branchesRepository;

    @InjectMocks
    private BranchesServiceImpl branchesService;

    private Branches branch;
    private BranchResponse response;
    private BranchRequest request;

    GeometryFactory geometryFactory = new GeometryFactory();
    double latitude = 55.7558;
    double longitude = 37.6176;

    Coordinate coordinate = new Coordinate(longitude, latitude);
    Point point = geometryFactory.createPoint(new CoordinateArraySequence(new Coordinate[]{coordinate}));

    Set<AppointmentType> services1 = Set.of(
            AppointmentType.CONSULTATION,
            AppointmentType.VACCINATION,
            AppointmentType.XRAY
    );

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new BranchRequest();
        request.setName("Клиника 1");
        request.setShortName("Клиника 1");
        request.setEmail("test@example.com");
        request.setAddress("Россия, г. Москва, ул. Ленина, д. 1");
        request.setServices(services1);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setPhone("89007770011");

        branch = new Branches();
        branch.setId(50L);
        branch.setName("Клиника 1");
        branch.setShortName("Клиника 1");
        branch.setEmail("test@example.com");
        branch.setAddress("Россия, г. Москва, ул. Ленина, д. 1");
        branch.setServices(services1);
        branch.setCoordinates(point);
        branch.setPhone("89007770011");

        response = new BranchResponse();
        response.setId(50L);
        response.setName("Клиника 1");
        response.setShortName("Клиника 1");
        response.setEmail("test@example.com");
        response.setAddress("Россия, г. Москва, ул. Ленина, д. 1");
        response.setServices(services1);
        response.setLatitude(latitude);
        response.setLongitude(longitude);
        response.setPhone("89007770011");
    }

    @Test
    void createBranch_savesAndReturnsResponse() {
        try (MockedStatic<BranchMapper> mapper = mockStatic(BranchMapper.class)) {
            mapper.when(() -> BranchMapper.toRequest(request)).thenReturn(branch);
            mapper.when(() -> BranchMapper.toResponse(branch)).thenReturn(response);

            when(branchesRepository.save(branch)).thenReturn(branch);

            BranchResponse result = branchesService.createBranch(request);

            assertThat(result).isSameAs(response);
            mapper.verify(() -> BranchMapper.toRequest(request), times(1));
            mapper.verify(() -> BranchMapper.toResponse(branch), times(1));
            verify(branchesRepository, times(1)).save(branch);
        }
    }

    @Test
    void getAllBranches_returnsAllBranchResponses() {
        try (MockedStatic<BranchMapper> mapper = mockStatic(BranchMapper.class)) {
            Branches anotherBranch = new Branches();
            anotherBranch.setId(51L);
            anotherBranch.setName("Клиника 2");
            anotherBranch.setShortName("Клиника 2");
            anotherBranch.setEmail("test2@example.com");
            anotherBranch.setAddress("Россия, г. Москва, ул. Тверская, д. 2");
            anotherBranch.setCoordinates(point);
            anotherBranch.setServices(services1);
            anotherBranch.setPhone("89007770022");

            BranchResponse anotherResponse = new BranchResponse();
            anotherResponse.setId(51L);
            anotherResponse.setName("Клиника 2");
            anotherResponse.setShortName("Клиника 2");
            anotherResponse.setEmail("test2@example.com");
            anotherResponse.setAddress("Россия, г. Москва, ул. Тверская, д. 2");
            anotherResponse.setLatitude(latitude);
            anotherResponse.setLongitude(longitude);
            anotherResponse.setServices(services1);
            anotherResponse.setPhone("89007770022");

            when(branchesRepository.findAll()).thenReturn(java.util.List.of(branch, anotherBranch));

            mapper.when(() -> BranchMapper.toResponse(branch)).thenReturn(response);
            mapper.when(() -> BranchMapper.toResponse(anotherBranch)).thenReturn(anotherResponse);

            var result = branchesService.getAllBranches();

            assertEquals(2, result.size());
            assertTrue(result.contains(response));
            assertTrue(result.contains(anotherResponse));
            verify(branchesRepository, times(1)).findAll();
        }
    }

    @Test
    void getBranchById_returnsBranchResponse() {
        try (MockedStatic<BranchMapper> mapper = mockStatic(BranchMapper.class)) {
            when(branchesRepository.findById(50L)).thenReturn(java.util.Optional.of(branch));
            mapper.when(() -> BranchMapper.toResponse(branch)).thenReturn(response);

            BranchResponse result = branchesService.getBranchById(50L);

            assertNotNull(result);
            assertEquals(response, result);
            verify(branchesRepository, times(1)).findById(50L);
        }
    }

    @Test
    void getBranchById_throwsExceptionIfNotFound() {
        when(branchesRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> branchesService.getBranchById(99L));

        assertTrue(exception.getMessage().contains("Branch not found"));
        verify(branchesRepository, times(1)).findById(99L);
    }

    @Test
    void deleteBranch_deletesBranchById() {
        when(branchesRepository.findById(50L)).thenReturn(Optional.of(branch));
        doNothing().when(branchesRepository).delete(branch);

        branchesService.deleteBranch(50L);

        verify(branchesRepository, times(1)).delete(branch);
    }

    @Test
    void deleteBranch_throwsExceptionIfNotFound() {
        when(branchesRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> branchesService.deleteBranch(99L));

        assertTrue(exception.getMessage().contains("Branch not found"));
        verify(branchesRepository, never()).deleteById(any());
    }
}