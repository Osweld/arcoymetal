package com.arcoymetal.arcobackend.identityaccess.internal.application.port.in;

import java.util.List;
import java.util.UUID;

public interface GetLinkedEmployeeIdsUseCase {

    List<UUID> getLinkedEmployeeIds();
}
