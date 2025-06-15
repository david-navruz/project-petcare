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
import com.project.petcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public Review saveReview(Review review, Long reviewerId, Long veterinarianId) {
        // Checking if the reviewer is the same user as the Vet being reviewed
        if (veterinarianId.equals(reviewerId)) {
            throw new IllegalArgumentException(FeedBackMessage.CANNOT_REVIEW);
        }
        // Checking if the reviewer has already submitted a review for this Vet
        Optional<Review> existingReview = reviewRepository.findByVeterinarianIdAndPetOwnerId(veterinarianId, reviewerId);
        if (existingReview.isPresent()){
            throw new ReviewAlreadyExistsException(FeedBackMessage.ALREADY_REVIEWED);
        }
        // Checking if the reviewer has got a completed appointment with this Vet
        boolean hadCompletedAppointments =
                appointmentRepository.existsByVeterinarianIdAndPetOwnerIdAndStatus(veterinarianId, reviewerId, AppointmentStatus.COMPLETED);
        if (!hadCompletedAppointments) {
            throw new IllegalStateException(FeedBackMessage.NOT_ALLOWED);
        }

        User veterinarian = userRepository.findById(veterinarianId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VET_OR_PETOWNER_NOT_FOUND));

        User petOwner = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VET_OR_PETOWNER_NOT_FOUND));
        // Adding the Vet and PetOwner into the Review and saving to the DB
        review.setVeterinarian(veterinarian);
        review.setPetOwner(petOwner);
        return reviewRepository.save(review);
    }


    @Transactional(readOnly = true)
    @Override
    public double getAverageRatingForVet(Long veterinarianId) {
        List<Review> reviews = reviewRepository.findByVeterinarianId(veterinarianId);
        return reviews.isEmpty() ? 0 : reviews.stream()
                .mapToInt(Review::getStars)
                .average()
                .orElse(0.0);
    }


    @Override
    public Review updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest) {
       Optional<Review> existingReview = reviewRepository.findById(reviewId);
       existingReview.map(review -> {
           review.setFeedback(reviewUpdateRequest.getFeedback());
           review.setStars(reviewUpdateRequest.getStars());
           return reviewRepository.save(review);
       });
        throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
    }


    @Override
    public Page<Review> findAllReviewsByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return reviewRepository.findAllByUserId(userId, pageRequest);
    }


    @Override
    public void deleteReview(Long reviewId) {
        // First we remove the relationship between the review and the Vet and PetOwner
        reviewRepository.findById(reviewId).ifPresentOrElse(Review::removeRelationShip,
                () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
                });
        // Then we delete the review
        reviewRepository.deleteById(reviewId);
    }


    @Override
    public Long countByVeterinarianId(Long id) {
        return reviewRepository.countByVeterinarianId(id);
    }

}
