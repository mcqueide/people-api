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