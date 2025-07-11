package com.project.petcare.controller;

import com.project.petcare.model.User;
import com.project.petcare.model.VerificationToken;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.request.VerificationTokenRequest;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.token.IVerificationTokenService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.TOKEN_VERIFICATION)
public class VerificationTokenController {

    private final IVerificationTokenService verificationTokenService;
    private final UserRepository userRepository;


    @GetMapping(UrlMapping.VALIDATE_TOKEN)
    public ResponseEntity<APIResponse> validateToken(String token) {
        String result = verificationTokenService.validateToken(token);
        APIResponse response = switch (result) {
            case "INVALID" -> new APIResponse(FeedBackMessage.INVALID_TOKEN, null);
            case "VERIFIED" -> new APIResponse(FeedBackMessage.TOKEN_ALREADY_VERIFIED, null);
            case "EXPIRED" -> new APIResponse(FeedBackMessage.EXPIRED_TOKEN, null);
            case "VALID" -> new APIResponse(FeedBackMessage.VALID_VERIFICATION_TOKEN, null);
            default -> new APIResponse(FeedBackMessage.TOKEN_VALIDATION_ERROR, null);
        };
        return ResponseEntity.ok(response);
    }

    @GetMapping(UrlMapping.CHECK_TOKEN_EXPIRATION)
    public ResponseEntity<APIResponse> checkTokenExpiration(String token) {
        boolean isExpired = verificationTokenService.isTokenExpired(token);
        APIResponse response;
        if (isExpired) {
            response = new APIResponse(FeedBackMessage.EXPIRED_TOKEN, null);
        }
        else {
            response = new APIResponse(FeedBackMessage.VALID_VERIFICATION_TOKEN, null);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(UrlMapping.SAVE_TOKEN)
    public ResponseEntity<APIResponse> saveVerificationTokenForUser(@RequestBody VerificationTokenRequest request) {
        User user = userRepository.findById(request.getUser().getId())
                .orElseThrow(() -> new RuntimeException(FeedBackMessage.USER_FOUND));
        verificationTokenService.saveVerificationTokenForUser(request.getToken(), user);
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.TOKEN_SAVED_SUCCESS, null));
    }

    @PutMapping(UrlMapping.GENERATE_NEW_TOKEN_FOR_USER)
    public ResponseEntity<APIResponse> generateNewVerificationToken(@RequestParam String oldToken) {
        VerificationToken newToken = verificationTokenService.generateNewVerificationToken(oldToken);
        return ResponseEntity.ok(new APIResponse("", newToken));
    }

    @DeleteMapping(UrlMapping.DELETE_TOKEN)
    public ResponseEntity<APIResponse> deleteUserToken(@RequestParam Long userId) {
        verificationTokenService.deleteVerificationToken(userId);
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.TOKEN_DELETE_SUCCESS, null));
    }

}