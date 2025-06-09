package com.project.petcare.service.user;

import com.project.petcare.dto.AppointmentDTO;
import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.ReviewDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserFactory userFactory;
    private final UserRepository userRepository;
    private final EntityConverter<User, UserDTO> entityConverter;
    private final VeterinarianRepository veterinarianRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReviewRepository reviewRepository;
    private final IAppointmentService appointmentService;
    private final IPetService petService;
    private final IPhotoService photoService;
    private final IReviewService reviewService;


    @Override
    public User createNewUser(RegistrationRequest request) {
        return userFactory.createNewUser(request);
    }


    @Override
    public User updateUser(Long userId, UserUpdateRequest request) {
        User userToUpdate = findUserById(userId);
        userToUpdate.setFirstName(request.getFirstName());
        userToUpdate.setLastName(request.getLastName());
        userToUpdate.setGender(request.getGender());
        userToUpdate.setPhoneNumber(request.getPhoneNumber());
        userToUpdate.setSpecialization(request.getSpecialization());
        return userRepository.save(userToUpdate);
    }


    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.NO_USER_FOUND));
    }


    // Before deleting a User, we need to find and delete his reviews and appointments
    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(userToDelete -> {
                    List<Review> reviews = new ArrayList<>(reviewRepository.findAllByUserId(userId));
                    reviewRepository.deleteAll(reviews);
                    List<Appointment> appointments = new ArrayList<>(appointmentRepository.findAllByUserId(userId));
                    appointmentRepository.deleteAll(appointments);
                    userRepository.deleteById(userId);
                }, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND);
                });
    }


    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> entityConverter.mapEntityToDTO(user, UserDTO.class))
                .collect(Collectors.toList());
    }


    public UserDTO getUserWithDetails(Long userId) throws SQLException {
        Optional<User> user = userRepository.findById(userId);
        UserDTO userDto = entityConverter.mapEntityToDTO(user.get(), UserDTO.class);
        userDto.setTotalReviewers(reviewRepository.countByVeterinarianId(userId));

        setUserAppointment(userDto);
        setUserPhoto(userDto, user.get());
        setUserReviews(userDto, userId);
        return userDto;
    }


    @Override
    public long countVeterinarians() {
        return userRepository.countByUserType("VET");
    }


    @Override
    public long countPetOwners() {
        return userRepository.countByUserType("PETOWNER");
    }


    @Override
    public long countAllUsers() {
        return userRepository.count();
    }


    /**
     * Return the list of Users based on their creationDate and Type
     */
    @Override
    public Map<String, Map<String, Long>> aggregateUsersByMonthAndType() {
        List<User> users = userRepository.findAll();
        return users.stream().collect(Collectors.groupingBy(user -> Month.of(user.getCreatedAt().getMonthValue())
                        .getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                Collectors.groupingBy(User::getUserType, Collectors.counting())));
    }


    /**
     * Return the list of Users based on their enabledStatus and Type
     */
    @Override
    public Map<String, Map<String, Long>> aggregateUsersByEnabledStatusAndType() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .collect(Collectors.groupingBy(user -> user.isEnabled() ? "Enabled" : "Non-Enabled",
                        Collectors.groupingBy(User::getUserType, Collectors.counting())));
    }


    // Helper method: setting user appointments into a UserDTO
    private void setUserAppointment(UserDTO userDto) {
        List<AppointmentDTO> appointments = appointmentService.getUserAppointments(userDto.getId());
        userDto.setAppointments(appointments);
    }


    // Helper method: setting user photo from Entity to UserDTO
    private void setUserPhoto(UserDTO userDto, User user) throws SQLException {
        if (user.getPhoto() != null){
            userDto.setPhotoId(user.getPhoto().getId());
            userDto.setPhoto(photoService.getPhotoData(user.getPhoto().getId()));
        }
    }


    // Helper method
    private void setUserReviews(UserDTO userDto, Long userId) {
        Page<Review> reviewPage = reviewService.findAllReviewsByUserId(userId, 0, Integer.MAX_VALUE);
        List<ReviewDTO> reviewDto = reviewPage.getContent()
                .stream()
                .map(this::mapReviewToDto).toList();
        if (!reviewDto.isEmpty()) {
            double averageRating = reviewService.getAverageRatingForVet(userId);
            userDto.setAverageRating(averageRating);
        }
        userDto.setReviews(reviewDto);
    }


    // Custom ReviewDTO converter
    private ReviewDTO mapReviewToDto(Review review) {
        ReviewDTO reviewDto = new ReviewDTO();
        reviewDto.setId(review.getId());
        reviewDto.setStars(review.getStars());
        reviewDto.setFeedback(review.getFeedback());
        mapVeterinarianInfo(reviewDto, review);
        mapPetOwnerInfo(reviewDto, review);
        return reviewDto;
    }

    private void mapVeterinarianInfo(ReviewDTO reviewDto, Review review) {
        if (review.getVeterinarian() != null) {
            reviewDto.setVeterinarianId(review.getVeterinarian().getId());
            reviewDto.setVeterinarianName(review.getVeterinarian().getFirstName() + " " + review.getVeterinarian().getLastName());
            // set the photo
            setVeterinarianPhoto(reviewDto, review);
        }
    }


    private void mapPetOwnerInfo(ReviewDTO reviewDto, Review review) {
        if (review.getPetOwner() != null){
            reviewDto.setPetOwnerId(review.getPetOwner().getId());
            reviewDto.setPetOwnerName(review.getPetOwner().getFirstName() + " " + review.getPetOwner().getLastName());
            // set the photo
            setReviewerPhoto(reviewDto, review);
        }
    }


    private void setReviewerPhoto(ReviewDTO reviewDto, Review review) {
        if (review.getPetOwner().getPhoto() != null) {
            try {
                reviewDto.setPetOwnerImage(photoService.getPhotoData(review.getPetOwner().getPhoto().getId()));
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        else {
            reviewDto.setPetOwnerImage(null);
        }
    }


    private void setVeterinarianPhoto(ReviewDTO reviewDto, Review review) {
        if (review.getVeterinarian().getPhoto() != null) {
            try {
                reviewDto.setVeterinarianImage(photoService.getPhotoData(review.getVeterinarian().getPhoto().getId()));
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        else {
            reviewDto.setVeterinarianImage(null);
        }
    }

    /**
     *  Parameter isEnabled is used to activate a user.
     *  isEnabled = true, it means user is active
     * @param userId
     */
    public void lockUserAccount(Long userId){
        userRepository.updateUserEnabledStatus(userId, false);
    }

    public void unLockUserAccount(Long userId){
        userRepository.updateUserEnabledStatus(userId, true);
    }

}
