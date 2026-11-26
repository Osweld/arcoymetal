@org.springframework.modulith.ApplicationModule(
    allowedDependencies = { "employee::api", "shared::persistence", "shared::web", "shared::security" }
)
package com.arcoymetal.arcobackend.identityaccess;
