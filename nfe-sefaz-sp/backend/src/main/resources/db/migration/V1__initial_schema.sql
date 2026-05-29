CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(160) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_user_id UUID NOT NULL REFERENCES users(id),
    cnpj VARCHAR(14) NOT NULL,
    corporate_name VARCHAR(180) NOT NULL,
    trade_name VARCHAR(180),
    state_registration VARCHAR(30),
    municipal_registration VARCHAR(30),
    cnae VARCHAR(20) NOT NULL,
    tax_regime VARCHAR(60) NOT NULL,
    crt VARCHAR(5) NOT NULL,
    zip_code VARCHAR(8) NOT NULL,
    street VARCHAR(180) NOT NULL,
    number VARCHAR(30) NOT NULL,
    complement VARCHAR(120),
    district VARCHAR(120) NOT NULL,
    city_code_ibge VARCHAR(10) NOT NULL,
    city_name VARCHAR(120) NOT NULL,
    uf CHAR(2) NOT NULL,
    country_code VARCHAR(4) NOT NULL DEFAULT '1058',
    country_name VARCHAR(80) NOT NULL DEFAULT 'Brasil',
    phone VARCHAR(30),
    email VARCHAR(180),
    environment VARCHAR(20) NOT NULL DEFAULT 'HOMOLOGACAO',
    default_series INTEGER NOT NULL DEFAULT 1,
    next_nfe_number BIGINT NOT NULL DEFAULT 1,
    default_nature_operation VARCHAR(120) NOT NULL,
    default_presence_indicator VARCHAR(5) NOT NULL DEFAULT '9',
    default_emission_type VARCHAR(5) NOT NULL DEFAULT '1',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_companies_owner_cnpj UNIQUE (owner_user_id, cnpj)
);

CREATE TABLE user_company_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    role VARCHAR(40) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_user_company_role UNIQUE (user_id, company_id, role)
);

CREATE TABLE digital_certificates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id),
    encrypted_file_path TEXT NOT NULL,
    encrypted_password TEXT NOT NULL,
    subject_name TEXT,
    issuer_name TEXT,
    serial_number VARCHAR(120),
    valid_from TIMESTAMPTZ,
    valid_until TIMESTAMPTZ,
    certificate_document VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id),
    person_type VARCHAR(20) NOT NULL,
    cpf VARCHAR(11),
    cnpj VARCHAR(14),
    foreign_id VARCHAR(60),
    name VARCHAR(180) NOT NULL,
    trade_name VARCHAR(180),
    state_registration_indicator VARCHAR(40) NOT NULL,
    state_registration VARCHAR(30),
    municipal_registration VARCHAR(30),
    email VARCHAR(180),
    phone VARCHAR(30),
    zip_code VARCHAR(8),
    street VARCHAR(180),
    number VARCHAR(30),
    complement VARCHAR(120),
    district VARCHAR(120),
    city_code_ibge VARCHAR(10),
    city_name VARCHAR(120),
    uf CHAR(2),
    country_code VARCHAR(4) DEFAULT '1058',
    country_name VARCHAR(80) DEFAULT 'Brasil',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id),
    internal_code VARCHAR(60) NOT NULL,
    ean VARCHAR(20),
    description VARCHAR(240) NOT NULL,
    ncm VARCHAR(8) NOT NULL,
    cest VARCHAR(7),
    cfop_internal VARCHAR(4),
    cfop_interstate VARCHAR(4),
    cfop_external VARCHAR(4),
    commercial_unit VARCHAR(10) NOT NULL,
    taxable_unit VARCHAR(10) NOT NULL,
    conversion_factor NUMERIC(18,6) NOT NULL DEFAULT 1,
    unit_price NUMERIC(18,4) NOT NULL DEFAULT 0,
    origin VARCHAR(2) NOT NULL DEFAULT '0',
    item_type VARCHAR(60) NOT NULL,
    gross_weight NUMERIC(18,4),
    net_weight NUMERIC(18,4),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_products_company_code UNIQUE (company_id, internal_code)
);

CREATE TABLE product_tax_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id),
    product_id UUID NOT NULL REFERENCES products(id),
    uf_origin CHAR(2) NOT NULL,
    uf_destination CHAR(2) NOT NULL,
    operation_type VARCHAR(40) NOT NULL,
    tax_regime VARCHAR(60) NOT NULL,
    cfop VARCHAR(4) NOT NULL,
    icms_cst VARCHAR(3),
    icms_csosn VARCHAR(4),
    icms_mod_bc VARCHAR(2),
    icms_rate NUMERIC(9,4),
    icms_base_reduction NUMERIC(9,4),
    fcp_rate NUMERIC(9,4),
    icms_st_mod_bc VARCHAR(2),
    icms_st_mva NUMERIC(9,4),
    icms_st_rate NUMERIC(9,4),
    icms_st_base_reduction NUMERIC(9,4),
    ipi_cst VARCHAR(2),
    ipi_rate NUMERIC(9,4),
    ipi_enquadramento VARCHAR(5),
    pis_cst VARCHAR(2),
    pis_rate NUMERIC(9,4),
    pis_calculation_type VARCHAR(30),
    cofins_cst VARCHAR(2),
    cofins_rate NUMERIC(9,4),
    cofins_calculation_type VARCHAR(30),
    benefit_code VARCHAR(20),
    valid_from DATE NOT NULL,
    valid_until DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE nfe (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id),
    customer_id UUID REFERENCES customers(id),
    model VARCHAR(2) NOT NULL DEFAULT '55',
    series INTEGER NOT NULL,
    number BIGINT NOT NULL,
    access_key VARCHAR(44),
    nature_operation VARCHAR(120) NOT NULL,
    operation_type VARCHAR(20) NOT NULL,
    destination_type VARCHAR(20) NOT NULL,
    issue_datetime TIMESTAMPTZ NOT NULL,
    exit_datetime TIMESTAMPTZ,
    purpose VARCHAR(30) NOT NULL,
    consumer_final BOOLEAN NOT NULL DEFAULT FALSE,
    presence_indicator VARCHAR(5) NOT NULL,
    intermediary_indicator VARCHAR(5),
    emission_type VARCHAR(5) NOT NULL,
    print_type VARCHAR(5) NOT NULL DEFAULT '1',
    process_type VARCHAR(5) NOT NULL DEFAULT '0',
    app_version VARCHAR(20) NOT NULL DEFAULT '0.1.0',
    status VARCHAR(40) NOT NULL DEFAULT 'RASCUNHO',
    sefaz_status_code VARCHAR(10),
    sefaz_status_reason TEXT,
    receipt_number VARCHAR(80),
    protocol_number VARCHAR(80),
    authorization_datetime TIMESTAMPTZ,
    digest_value TEXT,
    total_products NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_icms_base NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_icms NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_icms_desonerado NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_fcp NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_icms_st_base NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_icms_st NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_fcp_st NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_ipi NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_pis NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_cofins NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_freight NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_insurance NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_discount NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_other_expenses NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_invoice NUMERIC(18,2) NOT NULL DEFAULT 0,
    additional_fisco_info TEXT,
    additional_customer_info TEXT,
    internal_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_nfe_company_environment_number UNIQUE (company_id, series, number)
);

CREATE TABLE nfe_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nfe_id UUID NOT NULL REFERENCES nfe(id) ON DELETE CASCADE,
    product_id UUID REFERENCES products(id),
    item_number INTEGER NOT NULL,
    product_code VARCHAR(60) NOT NULL,
    description VARCHAR(240) NOT NULL,
    ncm VARCHAR(8) NOT NULL,
    cest VARCHAR(7),
    cfop VARCHAR(4) NOT NULL,
    commercial_unit VARCHAR(10) NOT NULL,
    commercial_quantity NUMERIC(18,4) NOT NULL,
    commercial_unit_value NUMERIC(18,10) NOT NULL,
    gross_total NUMERIC(18,2) NOT NULL,
    taxable_unit VARCHAR(10) NOT NULL,
    taxable_quantity NUMERIC(18,4) NOT NULL,
    taxable_unit_value NUMERIC(18,10) NOT NULL,
    freight_value NUMERIC(18,2) NOT NULL DEFAULT 0,
    insurance_value NUMERIC(18,2) NOT NULL DEFAULT 0,
    discount_value NUMERIC(18,2) NOT NULL DEFAULT 0,
    other_expenses NUMERIC(18,2) NOT NULL DEFAULT 0,
    include_in_total BOOLEAN NOT NULL DEFAULT TRUE,
    icms_origin VARCHAR(2),
    icms_cst VARCHAR(3),
    icms_csosn VARCHAR(4),
    icms_base NUMERIC(18,2) DEFAULT 0,
    icms_rate NUMERIC(9,4),
    icms_value NUMERIC(18,2) DEFAULT 0,
    icms_st_base NUMERIC(18,2) DEFAULT 0,
    icms_st_rate NUMERIC(9,4),
    icms_st_value NUMERIC(18,2) DEFAULT 0,
    fcp_rate NUMERIC(9,4),
    fcp_value NUMERIC(18,2) DEFAULT 0,
    ipi_cst VARCHAR(2),
    ipi_base NUMERIC(18,2) DEFAULT 0,
    ipi_rate NUMERIC(9,4),
    ipi_value NUMERIC(18,2) DEFAULT 0,
    pis_cst VARCHAR(2),
    pis_base NUMERIC(18,2) DEFAULT 0,
    pis_rate NUMERIC(9,4),
    pis_value NUMERIC(18,2) DEFAULT 0,
    cofins_cst VARCHAR(2),
    cofins_base NUMERIC(18,2) DEFAULT 0,
    cofins_rate NUMERIC(9,4),
    cofins_value NUMERIC(18,2) DEFAULT 0,
    additional_info TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE nfe_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nfe_id UUID NOT NULL REFERENCES nfe(id) ON DELETE CASCADE,
    payment_indicator VARCHAR(5) NOT NULL,
    payment_method VARCHAR(5) NOT NULL,
    value NUMERIC(18,2) NOT NULL,
    change_value NUMERIC(18,2) DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE nfe_installments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nfe_id UUID NOT NULL REFERENCES nfe(id) ON DELETE CASCADE,
    installment_number VARCHAR(20) NOT NULL,
    due_date DATE NOT NULL,
    value NUMERIC(18,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE nfe_transport (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nfe_id UUID NOT NULL REFERENCES nfe(id) ON DELETE CASCADE,
    freight_mode VARCHAR(5) NOT NULL,
    carrier_name VARCHAR(180),
    carrier_cpf_cnpj VARCHAR(14),
    carrier_state_registration VARCHAR(30),
    carrier_address VARCHAR(180),
    carrier_city VARCHAR(120),
    carrier_uf CHAR(2),
    vehicle_plate VARCHAR(10),
    vehicle_uf CHAR(2),
    rntc VARCHAR(30),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE nfe_volumes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nfe_transport_id UUID NOT NULL REFERENCES nfe_transport(id) ON DELETE CASCADE,
    quantity NUMERIC(18,4),
    species VARCHAR(60),
    brand VARCHAR(60),
    numbering VARCHAR(60),
    net_weight NUMERIC(18,4),
    gross_weight NUMERIC(18,4)
);

CREATE TABLE nfe_xml_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nfe_id UUID REFERENCES nfe(id) ON DELETE CASCADE,
    document_type VARCHAR(40) NOT NULL,
    file_path TEXT,
    xml_content TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE nfe_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id),
    nfe_id UUID REFERENCES nfe(id),
    event_type VARCHAR(40) NOT NULL,
    sequence_number INTEGER,
    justification_or_text TEXT NOT NULL,
    protocol_number VARCHAR(80),
    sefaz_status_code VARCHAR(10),
    sefaz_status_reason TEXT,
    event_datetime TIMESTAMPTZ NOT NULL DEFAULT now(),
    xml_path TEXT,
    response_xml_path TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sefaz_communication_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id),
    user_id UUID REFERENCES users(id),
    nfe_id UUID REFERENCES nfe(id),
    service_name VARCHAR(80) NOT NULL,
    environment VARCHAR(20) NOT NULL,
    endpoint_url TEXT,
    request_xml_path TEXT,
    response_xml_path TEXT,
    http_status INTEGER,
    sefaz_status_code VARCHAR(10),
    sefaz_status_reason TEXT,
    error_message TEXT,
    stack_trace TEXT,
    started_at TIMESTAMPTZ NOT NULL,
    finished_at TIMESTAMPTZ,
    duration_ms BIGINT,
    ip VARCHAR(80),
    user_agent TEXT
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    company_id UUID REFERENCES companies(id),
    action VARCHAR(80) NOT NULL,
    entity_name VARCHAR(120) NOT NULL,
    entity_id VARCHAR(80),
    old_value JSONB,
    new_value JSONB,
    ip VARCHAR(80),
    user_agent TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sefaz_rejection_catalog (
    code VARCHAR(10) PRIMARY KEY,
    message TEXT NOT NULL,
    friendly_message TEXT,
    possible_cause TEXT,
    suggested_fix TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_customers_company ON customers(company_id);
CREATE INDEX idx_products_company ON products(company_id);
CREATE INDEX idx_tax_rules_company_product ON product_tax_rules(company_id, product_id);
CREATE INDEX idx_nfe_company_status ON nfe(company_id, status);
CREATE INDEX idx_audit_company_created ON audit_logs(company_id, created_at);
CREATE INDEX idx_sefaz_logs_company_started ON sefaz_communication_logs(company_id, started_at);
