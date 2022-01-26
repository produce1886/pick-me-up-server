#!/bin/sh

# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$SECRET_PASSPHRASE" \
--output ./src/main/resources/application-aws.yml ./src/main/resources/application-aws.yml.gpg
