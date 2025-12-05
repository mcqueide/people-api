# K8S

## Prerequisites

- Kubernetes
- Helm

## Install Kubernetes with Kind

1. You can use any Kubernetes cluster, or install [Kind](https://kind.sigs.k8s.io/docs/user/quick-start/#installation) in your Docker.

2. Create cluster.

```
kind create cluster --config=.k8s/kind.yaml --name=kind
```
    
3. Set kubectl content for the new created cluster.

```
kubectl cluster-info --context kind-kind
```

## Install Helm

1. Follow the [Helm installation guide](https://helm.sh/docs/intro/install/).

## Help Cheat Sheet

```
# Run tests to examine a chart and identify possible issues:
helm lint helm/postgres 

# Run a test installation to validate chart (p)
helm template <name> helm/postgres --values helm/postgres/values.yaml --debug 

# Run a test installation to validate chart (p)
helm template <name> helm/postgres --dry-run --debug 

# Install the chart
helm install <name> helm/postgres

# Install the chart in a specific namespace
helm install <name> helm/postgres --namespace <namespace> 

# Lists all of the releases for a specified namespace, uses current namespace context if namespace not specified
helm list

# Uninstalls a release from the current (default) namespace
help uninstall <name> 

# Uninstalls a release from the specified namespace
helm uninstall <release-name> --namespace <namespace> 
```