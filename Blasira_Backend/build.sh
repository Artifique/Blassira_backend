#!/bin/bash
# Script de build pour Render
# Render exécutera ce script si vous utilisez "buildCommand" dans render.yaml

./mvnw clean package -DskipTests
