#!/bin/sh

gpg --quiet --batch --yes --decrypt --passphrase="$SECRET_PASSPHRASE" \
--output ./src/main/resources/application-aws.yml ./src/main/resources/application-aws.yml.gpg

gpg --quiet --batch --yes --decrypt --passphrase="$SECRET_PASSPHRASE" \
--output ./src/main/resources/application.yml ./src/main/resources/application.yml.gpg
