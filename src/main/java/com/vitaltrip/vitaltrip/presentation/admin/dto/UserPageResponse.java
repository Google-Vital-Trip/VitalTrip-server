package com.vitaltrip.vitaltrip.presentation.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserPageResponse(
    @Schema(description = "사용자 목록")
    List<AdminUserResponse> content,

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    int page,

    @Schema(description = "페이지 크기", example = "20")
    int size,

    @Schema(description = "총 요소 수", example = "100")
    long totalElements,

    @Schema(description = "총 페이지 수", example = "5")
    int totalPages,

    @Schema(description = "첫 번째 페이지 여부", example = "true")
    boolean first,

    @Schema(description = "마지막 페이지 여부", example = "false")
    boolean last,

    @Schema(description = "요소가 있는지 여부", example = "true")
    boolean hasContent,

    @Schema(description = "다음 페이지가 있는지 여부", example = "true")
    boolean hasNext,

    @Schema(description = "이전 페이지가 있는지 여부", example = "false")
    boolean hasPrevious
) {
    public static UserPageResponse from(Page<AdminUserResponse> page) {
        return new UserPageResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast(),
            page.hasContent(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
}
