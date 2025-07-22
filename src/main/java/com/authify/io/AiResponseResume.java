package com.authify.io;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseResume {
    private int atsScore;

    @JsonProperty("matchPercentage")
    private int matchPercent;

    private List<String> suggestions;
    private List<Map<String, String>> jobMatches;


    public AiResponseResume(String errorMessage) {
        this.atsScore = 0;
        this.matchPercent = 0;
        this.suggestions = List.of(errorMessage);
        this.jobMatches = List.of();
    }
}
