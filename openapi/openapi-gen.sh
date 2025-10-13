#!/bin/bash
# OpenAPI Generator Script for Paperless REST API

# Clean up any previous generation
echo "Cleaning previous generation..."
rm -rf ./out/
rm -rf ../PaperlessREST/src/main/java/*
rm -rf ../PaperlessREST/src/test/java/*

# Disable Git Bash path conversion for Docker
export MSYS_NO_PATHCONV=1

# Get the current directory
CURRENT_DIR="$(pwd)"

# Generate Spring Boot code using OpenAPI spec
docker run --rm \
  -v "${CURRENT_DIR}:/workspace" \
  -w /workspace \
  openapitools/openapi-generator-cli generate \
  -i openapi.yaml \
  -g spring \
  -o out \
  -p pocoModels=true \
  -p useSeperateModelProject=true \
  --artifact-id PaperlessREST \
  --group-id com.fhtechnikum \
  --additional-properties useTags=true \
  --package-name com.fhtechnikum.paperless \
  --api-package com.fhtechnikum.paperless.controller \
  --model-package com.fhtechnikum.paperless.services.dto \
  --additional-properties configPackage=com.fhtechnikum.paperless.config \
  --additional-properties basePackage=com.fhtechnikum.paperless.services \
  --additional-properties useSpringBoot3=true \
  --additional-properties useJakartaEe=true

# Re-enable path conversion
unset MSYS_NO_PATHCONV

# Remove generated files we don't want
rm -f ./out/src/main/java/com/fhtechnikum/paperless/services/OpenApiGeneratorApplication.java
rm -f ./out/src/main/java/com/fhtechnikum/paperless/controller/PaperlessApiController.java

# Copy all generated files to PaperlessREST module (dont copy pom.xml)
cp -rf ./out/src/* ../PaperlessREST/src/
