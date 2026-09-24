    package org.aria.tina.common.exception;

    import org.aria.tina.common.response.ApiCode;
    import org.aria.tina.common.response.ApiResponse;
    import org.springframework.context.MessageSource;
    import org.springframework.context.i18n.LocaleContextHolder;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.RestControllerAdvice;

    import java.util.Locale;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        private final MessageSource messageSource;

        public GlobalExceptionHandler(MessageSource messageSource) {
            this.messageSource = messageSource;
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
            Locale locale = LocaleContextHolder.getLocale();
            String message = messageSource.getMessage(ex.getCode(), ex.getArgs(), locale);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ApiCode.BAD_REQUEST, message, null));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
            Locale locale = LocaleContextHolder.getLocale();
            String message = messageSource.getMessage(
                    "error.internal.server", null, locale);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ApiCode.INTERNAL_SERVER_ERROR, message, null));
        }
    }