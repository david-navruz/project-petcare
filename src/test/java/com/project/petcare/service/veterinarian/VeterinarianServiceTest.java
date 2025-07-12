package com.project.petcare.service.veterinarian;

import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.UserDTO;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Appointment;
import com.project.petcare.model.Veterinarian;
import com.project.petcare.repository.AppointmentRepository;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.repository.VeterinarianRepository;
import com.project.petcare.service.photo.IPhotoService;
import com.project.petcare.service.review.IReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VeterinarianServiceTest {

    @Mock
    private VeterinarianRepository veterinarianRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IReviewService reviewService;
    @Mock
    private IPhotoService photoService;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private EntityConverter<Veterinarian, UserDTO> entityConverter;

    @InjectMocks
    private VeterinarianService veterinarianService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getAllVeterinariansWithDetails_returnsDTOList() {
        Veterinarian vet = new Veterinarian();
        vet.setId(1L);
        vet.setUserType("VET");
        vet.setFirstName("Mike");
        vet.setLastName("Tyson");
        List<Veterinarian> vets = List.of(vet);

        UserDTO userDTO = new UserDTO();
        when(userRepository.findAllByUserType("VET")).thenReturn(vets);
        when(entityConverter.mapEntityToDTO(any(Veterinarian.class), eq(UserDTO.class))).thenReturn(userDTO);
        when(reviewService.getAverageRatingForVet(anyLong())).thenReturn(4.5);
        when(reviewService.countByVeterinarianId(anyLong())).thenReturn(10L);

        List<UserDTO> result = veterinarianService.getAllVeterinariansWithDetails();
        assertEquals(1, result.size());
        assertEquals(userDTO, result.get(0));
    }

    @Test
    void getSpecializations_shouldReturnList() {
        List<String> specializations = Arrays.asList("Dog", "Cat");
        when(veterinarianRepository.getSpecializations()).thenReturn(specializations);
        List<String> result = veterinarianService.getSpecializations();
        assertEquals(2, result.size());
    }

    @Test
    void findAvailableVetsForAppointment_shouldReturnFilteredDTOs() {
        Veterinarian vet = new Veterinarian();
        vet.setId(2L);
        vet.setUserType("VET");
        vet.setSpecialization("Dog");
        List<Veterinarian> vets = List.of(vet);

        UserDTO dto = new UserDTO();
        when(veterinarianRepository.existsBySpecialization("Dog")).thenReturn(true);
        when(veterinarianRepository.findBySpecialization("Dog")).thenReturn(vets);
        when(appointmentRepository.findByVeterinarianAndAppointmentDate(any(), any())).thenReturn(Collections.emptyList());
        when(entityConverter.mapEntityToDTO(any(Veterinarian.class), eq(UserDTO.class))).thenReturn(dto);
        when(reviewService.getAverageRatingForVet(anyLong())).thenReturn(4.0);
        when(reviewService.countByVeterinarianId(anyLong())).thenReturn(2L);

        List<UserDTO> result = veterinarianService.findAvailableVetsForAppointment(
                "Dog", LocalDate.now(), LocalTime.now());
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getVeterinariansBySpecialization_exists_shouldReturnList() {
        List<Veterinarian> vets = List.of(new Veterinarian());
        when(veterinarianRepository.existsBySpecialization("Cat")).thenReturn(true);
        when(veterinarianRepository.findBySpecialization("Cat")).thenReturn(vets);
        List<Veterinarian> result = veterinarianService.getVeterinariansBySpecialization("Cat");
        assertEquals(1, result.size());
    }

    @Test
    void getVeterinariansBySpecialization_notExists_shouldThrow() {
        when(veterinarianRepository.existsBySpecialization("Fish")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> veterinarianService.getVeterinariansBySpecialization("Fish"));
    }

    @Test
    void aggregateVetsBySpecialization_returnsMapList() {
        Object[] row1 = new Object[]{"Cat", 2L};
        Object[] row2 = new Object[]{"Dog", 1L};
        when(veterinarianRepository.countVetsBySpecialization())
                .thenReturn(List.of(row1, row2));
        List<Map<String, Object>> result = veterinarianService.aggregateVetsBySpecialization();
        assertEquals(2, result.size());
        assertEquals("Cat", result.get(0).get("specialization"));
        assertEquals(2L, result.get(0).get("count"));
    }

    @Test
    void mapVeterinarianToUserDto_withPhoto_shouldSetPhoto() throws SQLException {
        Veterinarian vet = new Veterinarian();
        vet.setId(99L);
        vet.setUserType("VET");
        UserDTO dto = new UserDTO();
        vet.setPhoto(new com.project.petcare.model.Photo());
        vet.getPhoto().setId(5L);

        when(entityConverter.mapEntityToDTO(any(Veterinarian.class), eq(UserDTO.class))).thenReturn(dto);
        when(reviewService.getAverageRatingForVet(anyLong())).thenReturn(4.5);
        when(reviewService.countByVeterinarianId(anyLong())).thenReturn(10L);
        when(photoService.getPhotoData(5L)).thenReturn(new byte[]{1,2,3});

        UserDTO returnedDto = org.springframework.test.util.ReflectionTestUtils
                .invokeMethod(veterinarianService, "mapVeterinarianToUserDto", vet);

        assertNotNull(returnedDto.getPhoto());
        assertEquals(dto, returnedDto);
        assertEquals(4.5, returnedDto.getAverageRating());
        assertEquals(10L, returnedDto.getTotalReviewers());
    }

    @Test
    void mapVeterinarianToUserDto_withPhoto_sqlException_shouldThrowRuntime() throws SQLException {
        Veterinarian vet = new Veterinarian();
        vet.setId(100L);
        vet.setUserType("VET");
        UserDTO dto = new UserDTO();
        vet.setPhoto(new com.project.petcare.model.Photo());
        vet.getPhoto().setId(5L);

        when(entityConverter.mapEntityToDTO(any(Veterinarian.class), eq(UserDTO.class))).thenReturn(dto);
        when(reviewService.getAverageRatingForVet(anyLong())).thenReturn(5.0);
        when(reviewService.countByVeterinarianId(anyLong())).thenReturn(3L);
        when(photoService.getPhotoData(5L)).thenThrow(new SQLException("fail"));

        assertThrows(RuntimeException.class, () ->
                org.springframework.test.util.ReflectionTestUtils
                        .invokeMethod(veterinarianService, "mapVeterinarianToUserDto", vet));
    }

    @Test
    void doesAppointmentOverLap_overlap_shouldReturnTrue() {
        Veterinarian vet = new Veterinarian();
        Appointment existing = new Appointment();
        LocalTime start = LocalTime.of(10, 0);
        existing.setAppointmentTime(start);

        // requested slot: within buffer zone
        LocalTime requestedStart = LocalTime.of(9, 30);
        LocalTime requestedEnd = LocalTime.of(12, 0);

        boolean result = Boolean.TRUE.equals(org.springframework.test.util.ReflectionTestUtils
                .invokeMethod(veterinarianService, "doesAppointmentOverLap", existing, requestedStart, requestedEnd));

        assertTrue(result);
    }

    @Test
    void doesAppointmentOverLap_noOverlap_shouldReturnFalse() {
        Veterinarian vet = new Veterinarian();
        Appointment existing = new Appointment();
        LocalTime start = LocalTime.of(10, 0);
        existing.setAppointmentTime(start);

        LocalTime requestedStart = LocalTime.of(7, 0);
        LocalTime requestedEnd = LocalTime.of(8, 30);

        boolean result = Boolean.TRUE.equals(org.springframework.test.util.ReflectionTestUtils
                .invokeMethod(veterinarianService, "doesAppointmentOverLap", existing, requestedStart, requestedEnd));

        assertFalse(result);
    }
}
