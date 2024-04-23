Document OCR Project with Spring Boot and Tess4J
================================================

Overview
--------

This Document OCR Project integrates Optical Character Recognition (OCR) capabilities into a Spring Boot application using Tess4J, a Java wrapper for the Tesseract OCR engine. This solution is designed to help you effortlessly convert scanned documents, images, and PDFs into editable and searchable text.

Features
--------

-   Spring Boot Integration: Seamlessly integrates with Spring Boot for easy deployment and scalability.
-   Tesseract OCR: Leverages the powerful Tesseract OCR engine through Tess4J for high-accuracy text extraction.
-   Support for Multiple Formats: Process images to extract text.
-   RESTful API: Provides a simple RESTful API to upload documents and receive extracted text.

Getting Started
---------------

### Prerequisites

-   Java 11 or newer
-   Maven 3.6 or newer

### Installation

1.  Clone the Repository:

    `git clone https://github.com/nickngn/document-ocr.git
    cd document-ocr`

2.  Build the Project:

    `mvn clean install`

### Running the Application

1.  Start the Spring Boot application:

    `mvn spring-boot:run`

2.  The application will be available at: `http://localhost:8080`

### Usage

To use the OCR functionality:

-   POST `/ocr`: Endpoint to upload a document for OCR processing. This read all texts in the file

    Example using `curl`:

    `curl -F "file=@path_to_your_document" http://localhost:8080/ocr`

    
-   POST `/ocr/{docTyp}`: Endpoint to upload a document for OCR processing. This read all texts in the file

    Example using `curl`:

    `curl -F "file=@path_to_your_document" http://localhost:8080/ocr/idCard`

    which idCard is preconfigured in the `src/main/resources/bounding-config.json`

    Example:

    ```
    {
        "idCard": {
            "standardWidth": 900,
            "standardHeight": 557,
            "fields": [
              {"name": "id", "x": 344, "y": 69, "width": 272, "height": 50, "type": "text"},
              {"name": "image", "x": 38, "y": 132, "width": 264, "height": 285, "type": "image"},
              {"name": "name", "x": 300, "y": 178, "width": 501, "height": 86, "type": "text"},
              {"name": "race", "x": 300, "y": 362, "width": 306, "height": 41, "type": "text"},
              {"name": "dob", "x": 300, "y": 425, "width": 179, "height": 33, "type": "text"},
              {"name": "sex", "x": 494, "y": 392, "width": 125, "height": 88, "type": "text"},
              {"name": "cob", "x": 300, "y": 488, "width": 501, "height": 41, "type": "text"}
            ]
        },
        // ... others configs
    }
    ```
Contributing
------------

We welcome contributions to the Document OCR Project. To contribute:

1.  Fork the Repository

2.  Clone Your Fork

    bashCopy code

    `git clone https://github.com/your-username/document-ocr.git`

3.  Create a New Branch

    bashCopy code

    `git checkout -b your-branch-name`

4.  Make Changes and Commit

    bashCopy code

    `git add .
    git commit -m "Describe your changes here"`

5.  Push Changes to GitHub

    bashCopy code

    `git push origin your-branch-name`

6.  Submit a Pull Request

    -   Navigate to your fork on GitHub and create a pull request to the original repository.

License
-------

No License!
