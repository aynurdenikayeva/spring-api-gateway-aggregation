package com.aynur.commonlib.error;

import java.util.List;

public record ExceptionResponseDto(ApiError error, List<String> details) {}
