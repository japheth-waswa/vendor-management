package com.vendor.management.system.domain.valueobject;

import com.cyberark.conjur.api.Conjur;

public record ConjurDatabaseConfig(Conjur conjurHost) {
}
