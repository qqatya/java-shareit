package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.exception.dto.ErrorInfo;

@ControllerAdvice
@Slf4j
public class ErrorController extends ResponseEntityExceptionHandler {

    /**
     * Обработчик IllegalArgumentException
     *
     * @param e Эксепшн
     * @return Объект, содержащий сообщение об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorInfo processIllegalArgumentException(IllegalArgumentException e) {
        log.debug(e.getMessage());
        return new ErrorInfo(e.getMessage());
    }

    /**
     * Обработчик непредвиденных ошибок
     *
     * @param e Эксепшн
     * @return Объект, содержащий сообщение об ошибке
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorInfo processException(Exception e) {
        log.error("Unexpected error: ", e);
        return new ErrorInfo(e.getMessage());
    }

}
