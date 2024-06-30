package com.vendor.management.system.user.service.domain.ports.output.repository;

import com.vendor.management.system.domain.valueobject.UserField;
import com.vendor.management.system.domain.valueobject.UserId;
import com.vendor.management.system.user.service.domain.entity.User;
import com.vendor.management.system.user.service.domain.valueobject.UserAttribute;
import com.vendor.management.system.user.service.domain.valueobject.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(User user);

    void delete(User user);

    Optional<User> findById(UserId userId);

    Optional<List<UserRole>> findAllRoles();

    Optional<List<User>> findAllByAttributes(UserAttribute attributes, int pageNumber, int pageSize);

    Optional<List<User>> findAllByUserField(UserField userField, String value, int pageNumber, int pageSize);
}
