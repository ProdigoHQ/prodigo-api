package com.licode.prodigoerp.auth.adapter.output.persistence.user;

import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.SaveUserPort;
import com.licode.prodigoerp.auth.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements SaveUserPort, LoadUserPort {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<User> findUserByEmail(String email) {

        return jpaUserRepository.findByEmail(email).map(UserJpaMapper::toDomainModel);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {

        return jpaUserRepository.findByUsername(username).map(UserJpaMapper::toDomainModel);
    }

    @Override
    public Optional<User> findUserById(UUID id) {
        return jpaUserRepository.findById(id).map(UserJpaMapper::toDomainModel);
    }

    @Override
    public Optional<User> findUserByIdAndTenantId(UUID id, UUID tenantId) {
        return jpaUserRepository.findByIdAndTenant_Id(id, tenantId).map(UserJpaMapper::toDomainModel);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity userJpaEntity = jpaUserRepository.save(UserJpaMapper.toJpaEntity(user));

        return UserJpaMapper.toDomainModel(userJpaEntity);
    }
}
