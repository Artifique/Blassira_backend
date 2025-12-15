CREATE TABLE app_config (
    id BIGINT NOT NULL AUTO_INCREMENT,
    base_fare DECIMAL(38,2) NOT NULL,
    price_per_km DECIMAL(38,2) NOT NULL,
    default_currency VARCHAR(255) NOT NULL,
    driver_validation_required BOOLEAN NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Insérer une configuration par défaut lors de la création de la table
INSERT INTO app_config (base_fare, price_per_km, default_currency, driver_validation_required)
VALUES (2.50, 1.75, 'EUR', TRUE);
