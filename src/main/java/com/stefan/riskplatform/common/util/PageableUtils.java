package com.stefan.riskplatform.common.util;

public class PageableUtils {

    public static final int MAX_PAGE_SIZE = 100;

    private PageableUtils() {
    }

    public static void validatePageable(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be >= 0");
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "size must be between 1 and " + MAX_PAGE_SIZE
            );
        }
    }
}