variable "IMAGE_NAME" {
  default = "mcqueide/people-api"
}

target "docker-metadata-action" {
}

group "default" {
  targets = [ "prod" ]
}

target "prod" {
    inherits = ["docker-metadata-action"]
    context = "."
    dockerfile = "./Dockerfile"
    tags = ["${IMAGE_NAME}:latest"]
}

target "ci"  {
  context = "."
  dockerfile = "./Dockerfile"
  tags = [ "${IMAGE_NAME}:ci" ]
  target = "ci"
}

// It failed when I tried to use the multi-platform in the ci-bake.yaml file, so I duplicated prod target.
target "multi-platform" {
    inherits = ["docker-metadata-action"]
    context = "."
    dockerfile = "./Dockerfile"
    tags = ["${IMAGE_NAME}:latest"]
    platforms = [ "linux/amd64", "linux/arm64" ]
}