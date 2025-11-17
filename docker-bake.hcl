group "default" {
  targets = [ "backend" ]
}

target "backend" {
    context = "."
    dockerfile = "Dockerfile"
    tags = ["mcqueide/people-api:0.0.1-SNAPSHOT"]
    platforms = [ "linux/amd64", "linux/arm64" ]
}