package com.ukma.authentication.service.user;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import java.util.NoSuchElementException;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(NoSuchElementException.class)
    public Status handleInvalidArgument(NoSuchElementException exception) {
        return Status.INVALID_ARGUMENT.withDescription(exception.getMessage()).withCause(exception);
    }
}
