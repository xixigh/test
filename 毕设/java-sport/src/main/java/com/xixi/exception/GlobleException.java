package com.xixi.exception;

import com.xixi.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobleException {

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(value = RuntimeException.class)
  public Result exception(RuntimeException e){
    e.printStackTrace();
    log.error("运行时异常---->{}",e.getMessage());
    return Result.fail(e.getMessage());
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(value = AccessDeniedException.class)
  public Result exception(AccessDeniedException e){
    e.printStackTrace();
    log.error("权限不足---->{}",e.getMessage());
    return Result.fail("权限不足,请联系管理员 ");
  }
}
