package com.ukma.authentication.service.protobuf;

import com.ukma.authentication.service.user.User;
import com.ukma.authentication.service.user.UserRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void getUserById(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        String userId = request.getUserId();
        userRepository.findById(userId).ifPresent(user -> {
            UserResponse response = UserResponse.newBuilder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .build();
            responseObserver.onNext(response);
        });
        responseObserver.onCompleted();
    }

    @Override
    public void getAllUsers(Empty request, StreamObserver<UserResponse> responseObserver) {
        Iterable<User> users = userRepository.findAll();
        users.forEach(user -> {
            UserResponse response = UserResponse.newBuilder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .build();
            responseObserver.onNext(response);
        });
        responseObserver.onCompleted();
    }
}