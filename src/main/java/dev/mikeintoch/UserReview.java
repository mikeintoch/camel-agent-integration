package dev.mikeintoch;

import com.fasterxml.jackson.annotation.JsonCreator;

public record UserReview(Evaluation evaluation, String message, String reply) {

    @JsonCreator
    public UserReview {
    }
    
}