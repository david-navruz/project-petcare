package com.project.petcare.service.review;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.exception.ReviewAlreadyExistsException;
import com.project.petcare.model.AppointmentStatus;
import com.project.petcare.model.Review;
import com.project.petcare.model.User;
import com.project.petcare.repository.AppointmentRepository;
import com.project.petcare.repository.ReviewRepository;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.request.ReviewUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    // --- saveReview ---

    @Test
    void saveReview_success() {
        Long reviewerId = 1L, vetId = 2L;
        Review review = new Review();
        User vet = new User();
        User petOwner = new User();

        when(reviewRepository.findByVeterinarianIdAndPetOwnerId(vetId, reviewerId)).thenReturn(Optional.empty());
        when(appointmentRepository.existsByVeterinarianIdAndPetOwnerIdAndStatus(
                vetId, reviewerId, AppointmentStatus.COMPLETED)).thenReturn(true);
        when(userRepository.findById(vetId)).thenReturn(Optional.of(vet));
        when(userRepository.findById(reviewerId)).thenReturn(Optional.of(petOwner));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review saved = reviewService.saveReview(review, reviewerId, vetId);

        assertEquals(review, saved);
        verify(reviewRepository).save(review);
        assertEquals(vet, review.getVeterinarian());
        assertEquals(petOwner, review.getPetOwner());
    }

    @Test
    void saveReview_sameUserAsVet_throwsException() {
        Long reviewerId = 1L, vetId = 1L;
        Review review = new Review();
        assertThrows(IllegalArgumentException.class, () -> reviewService.saveReview(review, reviewerId, vetId));
    }

    @Test
    void saveReview_alreadyReviewed_throwsException() {
        Long reviewerId = 1L, vetId = 2L;
        Review review = new Review();
        when(reviewRepository.findByVeterinarianIdAndPetOwnerId(vetId, reviewerId))
                .thenReturn(Optional.of(new Review()));

        assertThrows(ReviewAlreadyExistsException.class, () -> reviewService.saveReview(review, reviewerId, vetId));
    }

    @Test
    void saveReview_noCompletedAppointments_throwsException() {
        Long reviewerId = 1L, vetId = 2L;
        Review review = new Review();
        when(reviewRepository.findByVeterinarianIdAndPetOwnerId(vetId, reviewerId))
                .thenReturn(Optional.empty());
        when(appointmentRepository.existsByVeterinarianIdAndPetOwnerIdAndStatus(
                vetId, reviewerId, AppointmentStatus.COMPLETED)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> reviewService.saveReview(review, reviewerId, vetId));
    }

    @Test
    void saveReview_vetOrOwnerNotFound_throwsException() {
        Long reviewerId = 1L, vetId = 2L;
        Review review = new Review();
        when(reviewRepository.findByVeterinarianIdAndPetOwnerId(vetId, reviewerId))
                .thenReturn(Optional.empty());
        when(appointmentRepository.existsByVeterinarianIdAndPetOwnerIdAndStatus(
                vetId, reviewerId, AppointmentStatus.COMPLETED)).thenReturn(true);
        when(userRepository.findById(vetId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.saveReview(review, reviewerId, vetId));
    }


    // --- getAverageRatingForVet ---

    @Test
    void getAverageRatingForVet_returnsCorrectAverage() {
        Long vetId = 2L;
        List<Review> reviews = Arrays.asList(
                createReviewWithStars(5),
                createReviewWithStars(3),
                createReviewWithStars(4)
        );
        when(reviewRepository.findByVeterinarianId(vetId)).thenReturn(reviews);

        double avg = reviewService.getAverageRatingForVet(vetId);
        assertEquals(4.0, avg, 0.01);
    }

    @Test
    void getAverageRatingForVet_empty_returnsZero() {
        when(reviewRepository.findByVeterinarianId(99L)).thenReturn(Collections.emptyList());
        assertEquals(0.0, reviewService.getAverageRatingForVet(99L));
    }


    // --- updateReview ---

    @Test
    void updateReview_found_shouldUpdateAndReturn() {
        Long reviewId = 1L;
        ReviewUpdateRequest req = new ReviewUpdateRequest();
        req.setFeedback("Good");
        req.setStars(5);
        Review review = new Review();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        assertThrows(ResourceNotFoundException.class, () -> reviewService.updateReview(reviewId, req));
        assertEquals("Good", review.getFeedback());
        assertEquals(5, review.getStars());
        verify(reviewRepository).save(review);
    }

    @Test
    void updateReview_notFound_throwsException() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.updateReview(1L, new ReviewUpdateRequest()));
    }


    // --- findAllReviewsByUserId ---

    @Test
    void findAllReviewsByUserId_returnsPage() {
        Long userId = 7L;
        PageRequest pr = PageRequest.of(0, 2);
        Page<Review> page = new PageImpl<>(List.of(new Review(), new Review()));
        when(reviewRepository.findAllByUserId(eq(userId), any(PageRequest.class))).thenReturn(page);

        Page<Review> result = reviewService.findAllReviewsByUserId(userId, 0, 2);
        assertEquals(2, result.getContent().size());
    }


    // --- deleteReview ---

    @Test
    void deleteReview_found_deletes() {
        Long reviewId = 5L;
        Review review = mock(Review.class);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReview(reviewId);

        verify(review).removeRelationShip();
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void deleteReview_notFound_throwsException() {
        when(reviewRepository.findById(6L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview(6L));
    }


    // --- countByVeterinarianId ---

    @Test
    void countByVeterinarianId_returnsCount() {
        when(reviewRepository.countByVeterinarianId(10L)).thenReturn(11L);
        assertEquals(11L, reviewService.countByVeterinarianId(10L));
    }

    // --- Utility for test ---
    private static Review createReviewWithStars(int stars) {
        Review review = new Review();
        review.setStars(stars);
        return review;
    }
}
