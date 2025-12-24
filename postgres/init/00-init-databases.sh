#!/bin/bash
set -e

# Read passwords from Docker secrets
KEYCLOAK_DB_PASSWORD=$(cat /run/secrets/keycloak-db-password)
API_DB_PASSWORD=$(cat /run/secrets/api-db-password)

# Execute SQL with environment variables
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- ============================================================================
    -- PostgreSQL Initialization Script
    -- Creates separate databases and users for each application
    -- ============================================================================

    -- Create database and user for Keycloak
    CREATE DATABASE keycloak;
    CREATE USER keycloak WITH PASSWORD '$KEYCLOAK_DB_PASSWORD';
    ALTER DATABASE keycloak OWNER TO keycloak;

    -- Connect to keycloak database
    \c keycloak

    -- Grant usage and create privileges on public schema
    GRANT ALL PRIVILEGES ON SCHEMA public TO keycloak;
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO keycloak;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO keycloak;
    
    -- Grant default privileges for future objects
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO keycloak;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO keycloak;

    ----------------------------------------------------------------------------

    -- Create database and user for People API
    \c postgres
    CREATE DATABASE people;
    CREATE USER people WITH PASSWORD '$API_DB_PASSWORD';
    ALTER DATABASE people OWNER TO people;

    -- Connect to keycloak database
    \c people
    
    -- Grant usage and create privileges on public schema
    GRANT ALL PRIVILEGES ON SCHEMA public TO people;
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO people;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO people;
    
    -- Grant default privileges for future objects
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO people;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO people;

    -- Return to postgres database
    \c postgres
EOSQL

echo "Database initialization completed successfully!"