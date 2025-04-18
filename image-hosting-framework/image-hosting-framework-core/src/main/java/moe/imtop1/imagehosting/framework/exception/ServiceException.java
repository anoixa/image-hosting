package moe.imtop1.imagehosting.framework.exception;

import lombok.Getter;
import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;

/**
 * @author shuomc
 * @Date 2025/4/15
 * @Description
 */
@Getter
public class ServiceException extends RuntimeException {

    private final int code;

    private final String message;

    /**
     * 使用 ResultCodeEnum 构造异常
     *
     * @param resultCodeEnum 错误码枚举常量
     */
    public ServiceException(ResultCodeEnum resultCodeEnum) {
        // 调用父类 RuntimeException 的构造函数，设置异常信息
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

    /**
     * 使用 ResultCodeEnum 和自定义消息构造异常
     * 允许覆盖枚举中定义的默认消息
     *
     * @param resultCodeEnum 错误码枚举常量
     * @param customMessage 自定义的错误消息
     */
    public ServiceException(ResultCodeEnum resultCodeEnum, String customMessage) {
        super(customMessage); // 使用自定义消息初始化父类
        this.code = resultCodeEnum.getCode();
        this.message = customMessage;
    }

    /**
     * 使用自定义错误码和自定义消息构造异常
     * 适用于不方便或不需要在 ResultCodeEnum 中定义的临时错误
     *
     * @param code          自定义错误码
     * @param customMessage 自定义的错误消息
     */
    public ServiceException(int code, String customMessage) {
        super(customMessage);
        this.code = code;
        this.message = customMessage;
    }

    /**
     * 使用自定义消息构造异常，使用默认错误码 (例如：ResultCodeEnum.ERROR.getCode())
     *
     * @param customMessage 自定义的错误消息
     */
    public ServiceException(String customMessage) {
        super(customMessage);
        // 可以指定一个默认的错误码
        this.code = ResultCodeEnum.ERROR.getCode(); // 或者其他合适的默认值
        this.message = customMessage;
    }

    /**
     * 使用 ResultCodeEnum 和底层的 Throwable 构造异常
     * 用于包装底层异常，同时提供业务错误码和信息
     *
     * @param resultCodeEnum 错误码枚举常量
     * @param cause         底层异常
     */
    public ServiceException(ResultCodeEnum resultCodeEnum, Throwable cause) {
        super(resultCodeEnum.getMessage(), cause); // 将底层异常传递给父类
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

    /**
     * 使用自定义消息和底层的 Throwable 构造异常
     *
     * @param customMessage 自定义错误消息
     * @param cause         底层异常
     */
    public ServiceException(String customMessage, Throwable cause) {
        super(customMessage, cause);
        // 可以指定一个默认的错误码
        this.code = ResultCodeEnum.ERROR.getCode();
        this.message = customMessage;
    }
}

