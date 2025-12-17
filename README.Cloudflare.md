# Cloudflare Integration & Troubleshooting Guide

This guide covers the integration of Cloudflare with Kubernetes ingress-nginx and common troubleshooting steps.

## Table of Contents

- [Overview](#overview)
- [Cloudflare SSL/TLS Modes](#cloudflare-ssltls-modes)
- [PROXY Protocol Configuration](#proxy-protocol-configuration)
- [Common Issues](#common-issues)
- [Troubleshooting Steps](#troubleshooting-steps)

## Overview

When using Cloudflare as a CDN/proxy in front of your Kubernetes cluster, you need to ensure compatibility between:
- Cloudflare's SSL/TLS encryption mode
- Ingress-nginx controller configuration
- PROXY protocol settings on your load balancer

## Cloudflare SSL/TLS Modes

Cloudflare offers several encryption modes:

### 1. **Flexible Mode** (Default)
- **Client ↔ Cloudflare:** HTTPS
- **Cloudflare ↔ Origin:** HTTP
- **Use case:** When you don't have SSL certificates on your origin
- **⚠️ Requirement:** PROXY protocol must be **disabled** on ingress-nginx

### 2. **Full Mode**
- **Client ↔ Cloudflare:** HTTPS
- **Cloudflare ↔ Origin:** HTTPS (self-signed certificate OK)
- **Use case:** When you have SSL certificates (can be self-signed)
- **Requirement:** Ingress must have TLS configuration

### 3. **Full (Strict) Mode** (Recommended)
- **Client ↔ Cloudflare:** HTTPS
- **Cloudflare ↔ Origin:** HTTPS (valid certificate required)
- **Use case:** Production environments with Let's Encrypt or valid SSL certificates
- **Requirement:** Ingress must have valid TLS certificate (e.g., via cert-manager)

### 4. **Off Mode**
- No encryption between client and Cloudflare
- Not recommended for production

## PROXY Protocol Configuration

### What is PROXY Protocol?

PROXY protocol is a network protocol that preserves client IP addresses when traffic passes through load balancers or proxies.

### When to Enable PROXY Protocol

✅ **Enable** PROXY protocol when:
- You're using a cloud load balancer that supports it (DigitalOcean, AWS NLB, etc.)
- You need to preserve real client IP addresses for logging/security
- **You're NOT using Cloudflare proxy** (orange cloud disabled in DNS)

❌ **Disable** PROXY protocol when:
- Using Cloudflare with proxying enabled (orange cloud)
- Cloudflare doesn't send PROXY protocol headers by default
- You're in "Flexible" SSL/TLS mode

### Current Configuration

This project is configured to work with Cloudflare, so PROXY protocol should be **disabled**.

## Common Issues

### Issue 1: "Empty reply from server" or "Connection reset"

**Symptoms:**
```bash
curl: (52) Empty reply from server
# or
curl: (56) Recv failure: Connection reset by peer
```

**Cause:** PROXY protocol mismatch between load balancer and ingress-nginx.

**Error in ingress-nginx logs:**
```
[error] broken header: "GET / HTTP/1.1" while reading PROXY protocol
```

**Solution:** Disable PROXY protocol (see steps below).

---

### Issue 2: Can't reach application via DNS but external IP works

**Symptoms:**
- `curl http://<external-ip>` works
- `curl http://yourdomain.com` fails

**Possible Causes:**
1. DNS not properly configured in Cloudflare
2. Cloudflare proxy enabled but ingress not configured correctly
3. SSL/TLS mode mismatch

**Solution:** Check DNS settings and SSL/TLS configuration.

---

### Issue 3: 502 Bad Gateway from Cloudflare

**Symptoms:**
- Cloudflare shows 502 error page

**Possible Causes:**
1. Origin server (ingress) is down
2. SSL/TLS mode is "Full" or "Full (strict)" but ingress has no TLS
3. Backend services are not running

**Solution:** 
- Switch to "Flexible" mode if no TLS configured
- Or configure TLS on ingress with cert-manager

---

## Troubleshooting Steps

### Step 1: Check Ingress Status

```bash
# List all ingress resources
kubectl get ingress -A

# Describe ingress for details
kubectl describe ingress people-api-ingress

# Check ingress address is assigned
kubectl get ingress people-api-ingress -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
```

**Expected:** Ingress should have an external IP address assigned.

---

### Step 2: Check Ingress Controller

```bash
# Check ingress-nginx controller service
kubectl get svc -n ingress-nginx

# Check controller pods
kubectl get pods -n ingress-nginx

# Check controller logs for errors
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller --tail=50
```

**Look for:**
- Service has `EXTERNAL-IP` assigned (not `<pending>`)
- Pods are in `Running` state
- No repeated errors in logs, especially "broken header" or "PROXY protocol" errors

---

### Step 3: Check PROXY Protocol Configuration

```bash
# Check LoadBalancer service annotation
kubectl get svc ingress-nginx-controller -n ingress-nginx -o yaml | grep proxy-protocol

# Check ConfigMap setting
kubectl get configmap ingress-nginx-controller -n ingress-nginx -o yaml | grep use-proxy-protocol
```

**For Cloudflare:** Both should be `"false"` or absent.

**If they're `"true"`, disable them:**

```bash
# Disable on LoadBalancer service
kubectl patch svc ingress-nginx-controller -n ingress-nginx -p '{"metadata":{"annotations":{"service.beta.kubernetes.io/do-loadbalancer-enable-proxy-protocol":"false"}}}'

# Disable in ConfigMap
kubectl patch configmap ingress-nginx-controller -n ingress-nginx -p '{"data":{"use-proxy-protocol":"false"}}'

# Restart ingress controller to apply changes
kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx

# Wait for rollout to complete
kubectl rollout status deployment ingress-nginx-controller -n ingress-nginx
```

---

### Step 4: Test Direct Connectivity

```bash
# Get external IP
EXTERNAL_IP=$(kubectl get svc ingress-nginx-controller -n ingress-nginx -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

# Test with Host header
curl -v -H "Host: people.mcqueide.com" http://$EXTERNAL_IP/

# Expected: HTTP 200 OK response
```

---

### Step 5: Check Backend Services

```bash
# Check if backend services exist
kubectl get svc people-api people-frontend

# Check if pods are running
kubectl get pods -l app=people-api
kubectl get pods -l app=people-frontend

# Test internal service connectivity
kubectl run test-curl --image=curlimages/curl --rm -it --restart=Never -- curl -v http://people-frontend:3000
```

**Expected:** Services and pods should be healthy and responsive.

---

### Step 6: Check DNS Configuration

```bash
# Check DNS resolution (requires bind9-dnsutils)
nslookup people.mcqueide.com
# or
dig people.mcqueide.com +short
```

**In Cloudflare Dashboard:**
1. Go to DNS settings
2. Verify A record exists: `people.mcqueide.com` → `<your-external-ip>`
3. Orange cloud (proxy) should be **enabled** if using Cloudflare features
4. If orange cloud is enabled, verify SSL/TLS mode matches your setup

---

### Step 7: Test via Domain Name

```bash
# Test HTTP
curl -I http://people.mcqueide.com

# If using TLS, test HTTPS
curl -I https://people.mcqueide.com
```

**Expected:** HTTP 200 OK response.

---

### Step 8: Check Cloudflare SSL/TLS Settings

1. Log in to Cloudflare Dashboard
2. Select your domain
3. Go to **SSL/TLS** → **Overview**
4. Verify encryption mode:
   - **No TLS on ingress?** Use "Flexible"
   - **Self-signed cert?** Use "Full"
   - **Valid cert (Let's Encrypt)?** Use "Full (strict)"

---

## Quick Fix Commands

### Disable PROXY Protocol (Most Common Fix)

```bash
# Disable PROXY protocol on service
kubectl patch svc ingress-nginx-controller -n ingress-nginx \
  -p '{"metadata":{"annotations":{"service.beta.kubernetes.io/do-loadbalancer-enable-proxy-protocol":"false"}}}'

# Disable PROXY protocol in ConfigMap
kubectl patch configmap ingress-nginx-controller -n ingress-nginx \
  -p '{"data":{"use-proxy-protocol":"false"}}'

# Restart controller
kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx

# Wait for restart
kubectl rollout status deployment ingress-nginx-controller -n ingress-nginx

# Test (replace with your domain)
curl -I http://people.mcqueide.com
```

---

### Enable TLS with cert-manager

If you want to use "Full (strict)" mode in Cloudflare:

```bash
# Apply TLS-enabled ingress
kubectl apply -f .k8s/backend/ingress.yaml.tls

# Check certificate status
kubectl get certificate

# Check cert-manager logs if issues
kubectl logs -n cert-manager deployment/cert-manager
```

---

## Useful Diagnostic Commands

```bash
# View ingress-nginx controller configuration
kubectl exec -n ingress-nginx deployment/ingress-nginx-controller -- cat /etc/nginx/nginx.conf | grep proxy_protocol

# Check what ingress rules are loaded
kubectl get ingress people-api-ingress -o yaml

# Monitor ingress-nginx logs in real-time
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller -f

# Test from within cluster
kubectl run debug --image=curlimages/curl -it --rm --restart=Never -- sh
# Then run: curl -v http://people-frontend:3000
```

---

## Architecture Diagrams

### With Cloudflare (Flexible Mode)

```
Internet → Cloudflare (HTTPS) → LoadBalancer (HTTP) → Ingress-nginx (HTTP) → Services
                                 ↓
                          PROXY Protocol: OFF
```

### With Cloudflare (Full Strict Mode)

```
Internet → Cloudflare (HTTPS) → LoadBalancer (HTTPS) → Ingress-nginx (HTTPS/TLS) → Services
                                 ↓                       ↓
                          PROXY Protocol: OFF    cert-manager (Let's Encrypt)
```

### Without Cloudflare (Direct)

```
Internet → LoadBalancer (HTTP/HTTPS) → Ingress-nginx → Services
            ↓
     PROXY Protocol: Can be ON
```

---

## Best Practices

1. **Use Full (strict) mode** in production with valid SSL certificates
2. **Disable PROXY protocol** when using Cloudflare proxy
3. **Enable HTTP/2** and **Brotli compression** in Cloudflare
4. **Monitor ingress-nginx logs** for configuration issues
5. **Use cert-manager** for automatic SSL certificate management
6. **Test both HTTP and HTTPS** endpoints after changes
7. **Document your SSL/TLS mode** in your deployment notes

---

## Additional Resources

- [Ingress-NGINX Documentation](https://kubernetes.github.io/ingress-nginx/)
- [Cloudflare SSL/TLS Modes](https://developers.cloudflare.com/ssl/origin-configuration/ssl-modes/)
- [PROXY Protocol Specification](https://www.haproxy.org/download/1.8/doc/proxy-protocol.txt)
- [cert-manager Documentation](https://cert-manager.io/docs/)
- [DigitalOcean Load Balancer Docs](https://docs.digitalocean.com/products/networking/load-balancers/)

---

## Changelog

- **2025-12-16:** Initial troubleshooting guide created
  - Documented PROXY protocol issue with Cloudflare
  - Added fix commands for disabling PROXY protocol
  - Included comprehensive troubleshooting steps
