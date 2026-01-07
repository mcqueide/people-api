#!/bin/sh
set -e

# Initialize truststore options
TRUSTSTORE_OPTS=""

# Import custom certificates if they exist
if [ -d "/certs" ]; then
    echo "Importing custom certificates..."
    
    # Get the Java home directory
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    CACERTS="$JAVA_HOME/lib/security/cacerts"
    
    # Create a writable copy of cacerts if running as non-root
    if [ ! -w "$CACERTS" ]; then
        echo "Creating writable cacerts copy..."
        cp "$CACERTS" /tmp/cacerts
        CACERTS="/tmp/cacerts"
        export JAVAX_NET_SSL_TRUSTSTORE="$CACERTS"
    fi
    
    # Import all .crt and .pem files from /certs directory
    for cert in /certs/*.crt /certs/*.pem; do
        if [ -f "$cert" ]; then
            ALIAS=$(basename "$cert" | sed 's/\.[^.]*$//')
            echo "Importing certificate: $cert as alias: $ALIAS"
            
            # Remove old certificate if it exists
            keytool -delete -alias "$ALIAS" -keystore "$CACERTS" -storepass changeit -noprompt 2>/dev/null || true
            
            # Import the certificate
            keytool -import -trustcacerts -alias "$ALIAS" -file "$cert" \
                -keystore "$CACERTS" -storepass changeit -noprompt
            
            echo "Certificate $ALIAS imported successfully"
        fi
    done
    
    echo "Certificate import complete"
fi

# Start the application with truststore options
exec java $JAVA_OPTS -jar app.jar