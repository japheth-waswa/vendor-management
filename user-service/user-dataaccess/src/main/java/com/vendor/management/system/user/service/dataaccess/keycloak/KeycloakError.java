package com.vendor.management.system.user.service.dataaccess.keycloak;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeycloakError {
    private String error;
    private String errorMessage;
    private String errorDescription;

    public String error(){
        if(this.error != null && !this.error.isBlank()){
            return this.error;
        }else if(this.errorMessage != null && !this.errorMessage.isBlank()){
            return this.errorMessage;
        }
        return "An error occurred while processing the request. Please try again later.";
    }
}
