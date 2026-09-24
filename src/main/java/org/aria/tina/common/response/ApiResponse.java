package org.aria.tina.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private String rsCode;
    private String message;
    private T resultData;

    public ApiResponse(Boolean success, ApiCode apiCode, String message, T resultData) {
        this.success = success;
        this.rsCode = apiCode.getCode();
        this.message = message != null ? message : apiCode.getDefaultMessage();
        this.resultData = resultData;
    }

    public static <T> ApiResponse<T> success(T resultData) {
        return new ApiResponse<>(true, ApiCode.OK.getCode(), null, resultData);
    }

    public static <T> ApiResponse<T> fail(ApiCode apiCode, String message, T resultData) {
        return new ApiResponse<>(false, apiCode, message, resultData);
    }

}
