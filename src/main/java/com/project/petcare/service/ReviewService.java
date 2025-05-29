package com.project.petcare.service;

import com.project.petcare.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private ReviewRepository reviewRepository;


    public void deleteReview(Long reviewId){
        reviewRepository.deleteById(reviewId);
    }


}
