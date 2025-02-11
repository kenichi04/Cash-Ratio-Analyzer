CREATE TABLE financial_document_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    edinet_code VARCHAR(255) NOT NULL,
    filer_name VARCHAR(255) NOT NULL,
    document_type VARCHAR(255) NOT NULL,
    form_code VARCHAR(50) NOT NULL,
    submission_date DATE NOT NULL,
    processed BOOLEAN DEFAULT FALSE NOT NULL
);
