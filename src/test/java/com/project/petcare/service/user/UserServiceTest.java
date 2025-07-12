package com.project.petcare.service.user;

import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.UserDTO;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.factory.UserFactory;
import com.project.petcare.model.Appointment;
import com.project.petcare.model.Review;
import com.project.petcare.model.User;
import com.project.petcare.repository.AppointmentRepository;
import com.project.petcare.repository.ReviewRepository;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.repository.VeterinarianRepository;
import com.project.petcare.request.RegistrationRequest;
import com.project.petcare.request.UserUpdateRequest;
import com.project.petcare.service.appointment.IAppointmentService;
import com.project.petcare.service.pet.IPetService;
import com.project.petcare.service.photo.IPhotoService;
import com.project.petcare.service.review.IReviewService;
import com.project.petcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserFactory userFactory;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EntityConverter<User, UserDTO> entityConverter;
    @Mock
    private VeterinarianRepository veterinarianRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private IAppointmentService appointmentService;
    @Mock
    private IPetService petService;
    @Mock
    private IPhotoService photoService;
    @Mock
    private IReviewService reviewService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createNewUser_ShouldReturnUser() {
        RegistrationRequest request = new RegistrationRequest();
        User user = new User();
        when(userFactory.createNewUser(request)).thenReturn(user);

        User created = userService.createNewUser(request);

        assertEquals(user, created);
        verify(userFactory, times(1)).createNewUser(request);
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        Long userId = 1L;
        UserUpdateRequest req = new UserUpdateRequest();
        req.setFirstName("Alice");
        req.setLastName("Doe");
        req.setGender("F");
        req.setPhoneNumber("1234");
        req.setSpecialization("Cat");
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.updateUser(userId, req);

        assertEquals(user, updated);
        verify(userRepository).save(user);
        assertEquals("Alice", user.getFirstName());
        assertEquals("Doe", user.getLastName());
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrow() {
        Long userId = 99L;
        UserUpdateRequest req = new UserUpdateRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(userId, req));
        assertEquals(FeedBackMessage.NO_USER_FOUND, ex.getMessage());
    }

    @Test
    void findUserById_ShouldReturnUser() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findUserById(userId);

        assertEquals(user, result);
    }

    @Test
    void findUserById_NotFound_ShouldThrow() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(2L));
    }

    @Test
    void deleteUser_ShouldDeleteUserAndRelatedEntities() {
        Long userId = 1L;
        User user = new User();
        List<Review> reviews = Arrays.asList(new Review(), new Review());
        List<Appointment> appts = Arrays.asList(new Appointment(), new Appointment());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewRepository.findAllByUserId(userId)).thenReturn(reviews);
        when(appointmentRepository.findAllByUserId(userId)).thenReturn(appts);

        userService.deleteUser(userId);

        verify(reviewRepository).deleteAll(reviews);
        verify(appointmentRepository).deleteAll(appts);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_UserNotFound_ShouldThrow() {
        Long userId = 9L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void getAllUsers_ShouldReturnDTOs() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);
        when(entityConverter.mapEntityToDTO(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());

        List<UserDTO> dtos = userService.getAllUsers();

        assertEquals(2, dtos.size());
    }

    @Test
    void getUserWithDetails_UserPresent_ShouldReturnDTO() throws SQLException {
        Long userId = 11L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDTO dto = new UserDTO();
        dto.setId(userId);
        when(entityConverter.mapEntityToDTO(any(User.class), eq(UserDTO.class))).thenReturn(dto);
        when(reviewRepository.countByVeterinarianId(userId)).thenReturn(3L);
        when(appointmentService.getUserAppointments(userId)).thenReturn(new ArrayList<>());
        when(reviewService.findAllReviewsByUserId(eq(userId), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of()));
        when(photoService.getPhotoData(anyLong())).thenReturn(new byte[0]);

        UserDTO result = userService.getUserWithDetails(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void getUserWithDetails_UserNotPresent_ShouldReturnEmptyDTO() throws SQLException {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());
        UserDTO dto = userService.getUserWithDetails(5L);
        assertNotNull(dto);
    }

    @Test
    void countVeterinarians_ShouldReturnCount() {
        when(userRepository.countByUserType("VET")).thenReturn(5L);
        assertEquals(5L, userService.countVeterinarians());
    }

    @Test
    void countPetOwners_ShouldReturnCount() {
        when(userRepository.countByUserType("PETOWNER")).thenReturn(10L);
        assertEquals(10L, userService.countPetOwners());
    }

    @Test
    void countAllUsers_ShouldReturnCount() {
        when(userRepository.count()).thenReturn(12L);
        assertEquals(12L, userService.countAllUsers());
    }

    @Test
    void aggregateUsersByMonthAndType_ShouldAggregateCorrectly() {
        User user1 = new User();
        user1.setCreatedAt(LocalDate.of(2023, 3, 5));
        user1.setUserType("VET");
        User user2 = new User();
        user2.setCreatedAt(LocalDate.of(2023, 3, 8));
        user2.setUserType("PETOWNER");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        Map<String, Map<String, Long>> result = userService.aggregateUsersByMonthAndType();

        assertEquals(1, result.get("March").get("VET"));
        assertEquals(1, result.get("March").get("PETOWNER"));
    }

    @Test
    void aggregateUsersByEnabledStatusAndType_ShouldAggregateCorrectly() {
        User user1 = new User();
        user1.setUserType("VET");
        user1.setEnabled(true);
        User user2 = new User();
        user2.setUserType("PETOWNER");
        user2.setEnabled(false);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        Map<String, Map<String, Long>> result = userService.aggregateUsersByEnabledStatusAndType();

        assertEquals(1, result.get("Enabled").get("VET"));
        assertEquals(1, result.get("Non-Enabled").get("PETOWNER"));
    }

    @Test
    void lockUserAccount_ShouldCallRepositoryWithFalse() {
        userService.lockUserAccount(1L);
        verify(userRepository).updateUserEnabledStatus(1L, false);
    }

    @Test
    void unLockUserAccount_ShouldCallRepositoryWithTrue() {
        userService.unLockUserAccount(1L);
        verify(userRepository).updateUserEnabledStatus(1L, true);
    }
}
