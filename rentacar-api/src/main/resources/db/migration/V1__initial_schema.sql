-- V1: Initial schema
-- Activated in Phase 2 when Flyway migrations are enabled.
-- Written now to serve as the source of truth for the schema.

CREATE TABLE accounts (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name       VARCHAR(50)  NOT NULL,
    last_name        VARCHAR(50)  NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE,
    password         VARCHAR(72)  NOT NULL,
    phone            VARCHAR(20)  NOT NULL UNIQUE,
    age              INTEGER      NOT NULL CHECK (age BETWEEN 18 AND 99),
    active           BOOLEAN      NOT NULL DEFAULT FALSE,
    user_role        VARCHAR(20)  NOT NULL DEFAULT 'USER'
);

CREATE TABLE vehicles (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    brand                 VARCHAR(50)      NOT NULL,
    model                 VARCHAR(50),
    color                 VARCHAR(30),
    year_manufacture      INTEGER          NOT NULL,
    plate                 VARCHAR(20)      NOT NULL UNIQUE,
    daily_rate            NUMERIC(10, 2),
    status                VARCHAR(20)      NOT NULL DEFAULT 'AVAILABLE',
    current_kilometers    INTEGER          NOT NULL DEFAULT 0,
    maintenance_end_date  DATE,
    maintenance_kilometers INTEGER         NOT NULL DEFAULT 0
);

CREATE TABLE rentals (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id        BIGINT       NOT NULL REFERENCES accounts(id),
    vehicle_id        BIGINT       NOT NULL REFERENCES vehicles(id),
    date_start        DATE         NOT NULL,
    date_end          DATE         NOT NULL,
    date_return       DATE,
    start_kilometers  INTEGER      NOT NULL DEFAULT 0,
    end_kilometers    INTEGER      NOT NULL DEFAULT 0,
    total_price       NUMERIC(10, 2),
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
);

CREATE INDEX idx_rentals_account_id ON rentals(account_id);
CREATE INDEX idx_rentals_vehicle_id ON rentals(vehicle_id);
CREATE INDEX idx_rentals_status     ON rentals(status);
CREATE INDEX idx_accounts_email     ON accounts(email);
