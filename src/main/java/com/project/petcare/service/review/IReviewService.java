package com.project.petcare.service.review;

import com.project.petcare.model.Review;
import com.project.petcare.request.ReviewUpdateRequest;
import org.springframework.data.domain.Page;

public interface IReviewService {

    Review saveReview(Review review, Long reviewerId, Long veterinarianId);

    double getAverageRatingForVet(Long veterinarianId);

    Review updateReview(Long reviewId, ReviewUpdateRequest review);

    Page<Review> findAllReviewsByUserId(Long userId, int page, int size);

    void deleteReview(Long reviewId);

}
