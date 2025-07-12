package com.project.petcare.controller;

import com.project.petcare.dto.ReviewDTO;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.exception.ReviewAlreadyExistsException;
import com.project.petcare.model.Review;
import com.project.petcare.request.ReviewUpdateRequest;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.review.IReviewService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.REVIEWS)
@RestController
public class ReviewController {

    private final IReviewService reviewService;
    private final ModelMapper modelMapper;


    @PostMapping(UrlMapping.SUBMIT_REVIEW)
    public ResponseEntity<APIResponse> saveReview(@RequestParam Long reviewerId,
                                                  @RequestParam Long vetId,
                                                  @RequestBody Review review) {
        try {
            Review savedReview = reviewService.saveReview(review, reviewerId, vetId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.CREATE_REVIEW_SUCCESS, savedReview.getId()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        } catch (ReviewAlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new APIResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }


    @PutMapping(UrlMapping.UPDATE_REVIEW)
    public ResponseEntity<APIResponse> updateReview(@RequestBody ReviewUpdateRequest updateRequest,
                                                    @PathVariable Long reviewId) {

        try {
            Review updatedReview = reviewService.updateReview(reviewId, updateRequest);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.REVIEW_UPDATE_SUCCESS, updatedReview.getId()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }


    @GetMapping(UrlMapping.GET_USER_REVIEWS)
    public ResponseEntity<APIResponse> getReviewsByUserId(@PathVariable Long userId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        try {
            Page<Review> reviewPage = reviewService.findAllReviewsByUserId(userId, page, size);
            Page<ReviewDTO> reviewDTOS = reviewPage.map((element) -> modelMapper.map(element, ReviewDTO.class));
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.REVIEW_FOUND, reviewDTOS));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }


    @DeleteMapping(UrlMapping.DELETE_REVIEW)
    public ResponseEntity<APIResponse> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.REVIEW_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }


}
