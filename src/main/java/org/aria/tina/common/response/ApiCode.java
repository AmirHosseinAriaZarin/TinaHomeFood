package org.aria.tina.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiCode {
    OK("200", "Success", HttpStatus.OK),
    BAD_REQUEST("400", "Bad Request", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("404", "Not Found", HttpStatus.NOT_FOUND),
    QUERY_ERROR("500", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED("401", "Unauthorized", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;
}
