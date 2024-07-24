package com.sunyoungeom.booktalk.exception;

import com.sunyoungeom.booktalk.exception.common.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class ExceptionHandlers {

    // 404 에러 페이지
    @ExceptionHandler({NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoResourceFoundException(Exception ex, Model model, HttpServletRequest request) throws NoResourceFoundException {
        // css 예외 제외
        if (request.getRequestURI().startsWith("/css/")) {
            throw new NoResourceFoundException(HttpMethod.GET, request.getRequestURI());
        }

        int errorCode = CommonErrorCode.NOT_FOUND_ERROR.getCode();
        String errorMessage = CommonErrorCode.NOT_FOUND_ERROR.getMessage();

        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorMessage", errorMessage);

        return new ModelAndView("error", model.asMap());
    }

    // 403 에러 페이지
    @ExceptionHandler({AccessDeniedException.class})
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex, Model model) {
        int errorCode = CommonErrorCode.ACCESS_DENIED_ERROR.getCode();
        String errorMessage = CommonErrorCode.ACCESS_DENIED_ERROR.getMessage();

        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorMessage", errorMessage);

        return new ModelAndView("error", model.asMap());
    }

    // 500 에러 페이지
    @ExceptionHandler({Exception.class})
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllException(Exception ex, Model model) {
        int errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR.getCode();
        String errorMessage = CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage();

        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorMessage", errorMessage);

        return new ModelAndView("error", model.asMap());
    }
}
