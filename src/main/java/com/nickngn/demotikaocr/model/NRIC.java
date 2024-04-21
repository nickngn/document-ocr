package com.nickngn.demotikaocr.model;

import lombok.Builder;

@Builder
public record NRIC(
        String id,
        String image,
        String name,
        String race,
        String dateOfBirth,
        String sex,
        String countryOfBirth
) { }
