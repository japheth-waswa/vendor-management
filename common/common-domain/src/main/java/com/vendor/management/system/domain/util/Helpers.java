package com.vendor.management.system.domain.util;

import com.vendor.management.system.domain.valueobject.Pagination;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Helpers {
    private Helpers() {
    }

    public static String removeStartingSlash(String input) {
        if (input.length() == 1 && input.startsWith("/")) {
            return "";
        } else if (input.startsWith("/")) {
            return input.substring(1);
        }
        return input;
    }

    public static String removeTrailingSlash(String input) {
        return input.replaceAll("/\\z", "");
    }

    public static URI buildUrl(Map<String, String> queryParams, String... urlParts) {
        return buildUrl("/", queryParams, false, false, urlParts);
    }

    public static URI buildUrl(Map<String, String> queryParams, boolean encodeQueryKey, boolean encodeQueryValue, String... urlParts) {
        return buildUrl("/", queryParams, encodeQueryKey, encodeQueryValue, urlParts);
    }

    public static URI buildUrl(String pathDelimiter, Map<String, String> queryParams, boolean encodeQueryKey, boolean encodeQueryValue, String... urlParts) {
        StringJoiner urlJoiner = new StringJoiner(pathDelimiter);
        for (String part : urlParts) {
            urlJoiner.add(removeTrailingSlash(removeStartingSlash(part)));
        }
        String baseUrl = urlJoiner.toString();
        if (queryParams != null && !queryParams.isEmpty()) {
            StringJoiner queryJoiner = new StringJoiner("&");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                queryJoiner.add((encodeQueryKey ? URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) : entry.getKey())
                        + "=" + (encodeQueryValue ? URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8) : entry.getValue()));
            }
            baseUrl += "?" + queryJoiner;
        }
        return URI.create(baseUrl);
    }

    public static Map<Pagination, String> parsePagination(int pageNumber, int pageSize) {
        Map<Pagination, String> pagination = new HashMap<>();
        pagination.put(Pagination.OFFSET, String.valueOf(pageNumber * pageSize));
        pagination.put(Pagination.LIMIT, String.valueOf(pageSize));
        return pagination;
    }
}
