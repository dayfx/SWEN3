FROM maven:3.9.11-eclipse-temurin-21 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD .. $HOME
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre

# Install Tesseract OCR and required native libraries for Tess4J
RUN apt-get update && \
    apt-get install -y \
    tesseract-ocr \
    libtesseract-dev \
    libleptonica-dev \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Download English language package directly from GitHub
RUN mkdir -p /usr/share/tessdata
ADD https://github.com/tesseract-ocr/tessdata/raw/main/eng.traineddata /usr/share/tessdata/eng.traineddata

# Set TESSDATA_PREFIX environment variable
ENV TESSDATA_PREFIX=/usr/share/tessdata

# Check the installation status
RUN tesseract --list-langs
RUN tesseract -v

COPY --from=build /usr/app/PaperlessServices/target/*.jar /app/runner.jar
ENTRYPOINT ["java", "-jar", "/app/runner.jar"]
