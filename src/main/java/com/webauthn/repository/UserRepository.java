package com.webauthn.repository;

import com.webauthn.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>  {

    UserEntity findUserEntityByName(String name);

    List<UserEntity> getAllByName(String name);
}
