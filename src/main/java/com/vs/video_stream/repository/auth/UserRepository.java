package com.vs.video_stream.repository.auth;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vs.video_stream.model.User;

public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findByEmail(String email);

}
