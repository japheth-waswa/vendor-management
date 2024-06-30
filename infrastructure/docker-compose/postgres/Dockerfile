FROM postgres:13

# Define arguments for environment variables
ARG CONJUR_ACCOUNT
ARG CONJUR_APPLIANCE_URL
ARG CONJUR_DATABASE_CONFIG_VERSION
ARG CONJUR_AUTHN_LOGIN_HOST_DATABASE_CONFIG
ARG CONJUR_AUTHN_API_KEY_HOST_DATABASE_CONFIG
ARG CONJUR_VARIABLE_POSTRESQL_USERNAME
ARG CONJUR_VARIABLE_POSTRESQL_PASSWORD

### Switch to root user
#USER root

## Create the missing directory
RUN mkdir -p /var/lib/apt/lists/partial

# Install curl, perl and cpanminus
RUN apt-get update && apt-get install -y curl perl cpanminus

# Install URI::Escape module
RUN cpanm URI::Escape

# Create a custom_env.sh script to store the environment variables
RUN echo "#!/bin/sh" > custom_env.sh

#Fetch variables from conjur for database configuration
RUN export CONJUR_AUTHN_LOGIN_HOST_DATABASE_CONFIG_ENCODED=$(perl -MURI::Escape -e 'print uri_escape($ARGV[0]);' "$CONJUR_AUTHN_LOGIN_HOST_DATABASE_CONFIG") &&  \
    export CONJUR_TOKEN=$(curl -k --location "$CONJUR_APPLIANCE_URL/authn/$CONJUR_ACCOUNT/$CONJUR_AUTHN_LOGIN_HOST_DATABASE_CONFIG_ENCODED/authenticate" \
                                       --header 'Accept-Encoding: base64' \
                                       --header 'Content-Type: text/plain'  \
                                       --data "$CONJUR_AUTHN_API_KEY_HOST_DATABASE_CONFIG") &&  \
    echo "export POSTGRES_USER=$(curl -k --location "$CONJUR_APPLIANCE_URL/secrets/$CONJUR_ACCOUNT/variable/$CONJUR_VARIABLE_POSTRESQL_USERNAME?version=$CONJUR_DATABASE_CONFIG_VERSION" \
                                        --header 'Content-Type: application/json' \
                                        --header "Authorization: Token token=\"$CONJUR_TOKEN\"")" >> custom_env.sh &&  \
    echo "export POSTGRES_PASSWORD=$(curl -k --location "$CONJUR_APPLIANCE_URL/secrets/$CONJUR_ACCOUNT/variable/$CONJUR_VARIABLE_POSTRESQL_PASSWORD?version=$CONJUR_DATABASE_CONFIG_VERSION" \
                                            --header 'Content-Type: application/json' \
                                            --header "Authorization: Token token=\"$CONJUR_TOKEN\"")" >> custom_env.sh

# Make the custom_env.sh script readable and executable by all users
RUN chmod 755 custom_env.sh

## Switch back to the original user
#USER 1001

# Use the custom_env.sh script as the entrypoint
ENTRYPOINT ["/bin/sh", "-c", ". /custom_env.sh && docker-entrypoint.sh postgres"]