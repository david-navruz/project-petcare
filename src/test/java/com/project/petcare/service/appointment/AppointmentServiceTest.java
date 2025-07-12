package com.project.petcare.service.appointment;

import com.project.petcare.dto.AppointmentDTO;
import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.PetDTO;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.*;
import com.project.petcare.repository.AppointmentRepository;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.request.AppointmentUpdateRequest;
import com.project.petcare.request.BookAppointmentRequest;
import com.project.petcare.service.pet.IPetService;
import com.project.petcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IPetService petService;
    @Mock
    private EntityConverter<Appointment, AppointmentDTO> entityConverter;
    @Mock
    private EntityConverter<Pet, PetDTO> petEntityConverter;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    // -------- createAppointment ----------

    @Test
    void createAppointment_success() {
        BookAppointmentRequest req = mock(BookAppointmentRequest.class);
        Long senderId = 1L, recipientId = 2L;

        User sender = new User();
        User recipient = new User();
        Appointment appointment = new Appointment();
        appointment.setId(100L);

        Pet pet1 = new Pet();
        pet1.setId(11L);
        pet1.setName("Tom");
        pet1.setAge(3);
        pet1.setType("Cat");
        pet1.setBreed("Siamese");

        Pet pet2 = new Pet();
        pet2.setId(12L);
        pet2.setName("Jerry");
        pet2.setAge(3);
        pet2.setType("Mouse");
        pet2.setBreed("Mini");
        List<Pet> pets = Arrays.asList(pet1, pet2);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(req.getAppointment()).thenReturn(appointment);
        when(req.getPets()).thenReturn(pets);
        when(petService.savePets(pets)).thenReturn(pets);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment saved = appointmentService.createAppointment(req, senderId, recipientId);

        assertNotNull(saved);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createAppointment_senderOrRecipientNotFound_throwsException() {
        BookAppointmentRequest req = mock(BookAppointmentRequest.class);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(req, 1L, 2L));
        assertEquals(FeedBackMessage.SENDER_RECIPIENT_NOT_FOUND, ex.getMessage());
    }


    // -------- updateAppointment ----------

    @Test
    void updateAppointment_success() {
        Long id = 5L;
        AppointmentUpdateRequest req = new AppointmentUpdateRequest();
        req.setAppointmentDate("2024-07-01");
        req.setAppointmentTime("14:00");
        req.setReason("Vaccination");

        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.updateAppointment(id, req);
        assertEquals("Vaccination", result.getReason());
        assertEquals(LocalDate.parse("2024-07-01"), result.getAppointmentDate());
        assertEquals(LocalTime.parse("14:00"), result.getAppointmentTime());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void updateAppointment_notWaitingForApproval_throwsException() {
        Long id = 5L;
        AppointmentUpdateRequest req = new AppointmentUpdateRequest();
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.APPROVED);
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));

        assertThrows(IllegalStateException.class, () -> appointmentService.updateAppointment(id, req));
    }


    // -------- getAllAppointments ----------

    @Test
    void getAllAppointments_success() {
        List<Appointment> appointments = Arrays.asList(new Appointment(), new Appointment());
        when(appointmentRepository.findAll()).thenReturn(appointments);
        List<Appointment> result = appointmentService.getAllAppointments();
        assertEquals(2, result.size());
    }


    // -------- deleteAppointment ----------

    @Test
    void deleteAppointment_success() {
        Long id = 10L;
        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(id);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void deleteAppointment_notFound_throwsException() {
        when(appointmentRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.deleteAppointment(123L));
    }


    // -------- getAppointmentById ----------

    @Test
    void getAppointmentById_success() {
        Long id = 9L;
        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        assertEquals(appointment, appointmentService.getAppointmentById(id));
    }

    @Test
    void getAppointmentById_notFound_throwsException() {
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.getAppointmentById(999L));
    }


    // -------- getAppointmentByNo ----------

    @Test
    void getAppointmentByNo_success() {
        String appointmentNo = "A123";
        Appointment appointment = new Appointment();
        when(appointmentRepository.findByAppointmentNo(appointmentNo)).thenReturn(appointment);
        assertEquals(appointment, appointmentService.getAppointmentByNo(appointmentNo));
    }


    // -------- getUserAppointments ----------

    @Test
    void getUserAppointments_success() {
        Long userId = 7L;
        Appointment appointment = new Appointment();
        appointment.setPets(Arrays.asList(new Pet(), new Pet()));
        List<Appointment> list = List.of(appointment);
        when(appointmentRepository.findAllByUserId(userId)).thenReturn(list);
        when(entityConverter.mapEntityToDTO(any(Appointment.class), eq(AppointmentDTO.class))).thenReturn(new AppointmentDTO());
        when(petEntityConverter.mapEntityToDTO(any(Pet.class), eq(PetDTO.class))).thenReturn(new PetDTO());

        List<AppointmentDTO> dtos = appointmentService.getUserAppointments(userId);
        assertEquals(1, dtos.size());
    }


    // -------- cancelAppointment ----------

    @Test
    void cancelAppointment_success() {
        Long appointmentId = 22L;
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.saveAndFlush(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.cancelAppointment(appointmentId);
        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancelAppointment_wrongStatus_throwsException() {
        Long id = 22L;
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.APPROVED);
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        assertThrows(IllegalStateException.class, () -> appointmentService.cancelAppointment(id));
    }


    // -------- approveAppointment ----------

    @Test
    void approveAppointment_success() {
        Long appointmentId = 24L;
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.saveAndFlush(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.approveAppointment(appointmentId);
        assertEquals(AppointmentStatus.APPROVED, result.getStatus());
    }

    @Test
    void approveAppointment_wrongStatus_throwsException() {
        Long id = 25L;
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        assertThrows(IllegalStateException.class, () -> appointmentService.approveAppointment(id));
    }


    // -------- declineAppointment ----------

    @Test
    void declineAppointment_success() {
        Long appointmentId = 26L;
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.saveAndFlush(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.declineAppointment(appointmentId);
        assertEquals(AppointmentStatus.NOT_APPROVED, result.getStatus());
    }

    @Test
    void declineAppointment_wrongStatus_throwsException() {
        Long id = 27L;
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.APPROVED);
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        assertThrows(IllegalStateException.class, () -> appointmentService.declineAppointment(id));
    }


    // -------- countAppointment ----------

    @Test
    void countAppointment_success() {
        when(appointmentRepository.count()).thenReturn(15L);
        assertEquals(15L, appointmentService.countAppointment());
    }


    // -------- getAppointmentSummary ----------

    @Test
    void getAppointmentSummary_success() {
        Appointment a1 = new Appointment();
        a1.setStatus(AppointmentStatus.APPROVED);
        Appointment a2 = new Appointment();
        a2.setStatus(AppointmentStatus.APPROVED);
        Appointment a3 = new Appointment();
        a3.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findAll()).thenReturn(List.of(a1, a2, a3));

        List<Map<String, Object>> summary = appointmentService.getAppointmentSummary();
        assertEquals(2, summary.size());
        Set<String> names = new HashSet<>();
        summary.forEach(m -> names.add((String) m.get("name")));
        assertTrue(names.contains("approved") && names.contains("cancelled"));
    }


    // -------- getAppointmentIds ----------

    @Test
    void getAppointmentIds_success() {
        Appointment a1 = new Appointment();
        a1.setId(101L);
        Appointment a2 = new Appointment();
        a2.setId(102L);

        when(appointmentRepository.findAll()).thenReturn(List.of(a1, a2));

        List<Long> ids = appointmentService.getAppointmentIds();
        assertEquals(List.of(101L, 102L), ids);
    }


    // -------- setAppointmentStatus ----------

    @Test
    void setAppointmentStatus_approvedToUpcoming() {
        Appointment appointment = new Appointment();
        appointment.setId(30L);
        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setAppointmentTime(LocalTime.now());

        when(appointmentRepository.findById(30L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        appointmentService.setAppointmentStatus(30L);
        assertEquals(AppointmentStatus.UP_COMING, appointment.getStatus());
    }

    @Test
    void setAppointmentStatus_waitingToNotApproved() {
        Appointment appointment = new Appointment();
        appointment.setId(40L);
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
        appointment.setAppointmentDate(LocalDate.now().minusDays(1));
        appointment.setAppointmentTime(LocalTime.now());

        when(appointmentRepository.findById(40L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        appointmentService.setAppointmentStatus(40L);
        assertEquals(AppointmentStatus.NOT_APPROVED, appointment.getStatus());
    }

}