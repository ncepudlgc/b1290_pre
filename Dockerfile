FROM eclipse-temurin:17

# Install necessary packages for development
RUN apt-get update && \
    apt-get install -y \
    bash \
    curl \
    vim \
    && rm -rf /var/lib/apt/lists/*

# Create a non-root user for development
RUN useradd -m -s /bin/bash developer
USER developer

# Set working directory
WORKDIR /home/developer/app

# Copy the project files
COPY --chown=developer:developer . .

# Default to bash shell
CMD ["/bin/bash"]