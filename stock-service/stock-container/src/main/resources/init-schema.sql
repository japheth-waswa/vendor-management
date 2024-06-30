CREATE SCHEMA IF NOT EXISTS "stock";

--stock product categories
CREATE TABLE IF NOT EXISTS "stock".product_categories
(
    id uuid NOT NULL,
    vendor_id uuid NOT NULL,
    name character varying(100) COLLATE pg_catalog."default" NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT product_categories_pkey PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS "idx_vendorId"
    ON "stock".product_categories
    (vendor_id);
CREATE INDEX IF NOT EXISTS "idx_name"
    ON "stock".product_categories
    (name);
CREATE INDEX IF NOT EXISTS "idx_updatedAt"
    ON "stock".product_categories
    (updated_at);
CREATE INDEX IF NOT EXISTS "idx_createdAt"
    ON "stock".product_categories
    (created_at);
CREATE UNIQUE INDEX IF NOT EXISTS "idx_vendorId_name"
    ON "stock".product_categories
    (vendor_id, name);

--stock products
CREATE TABLE IF NOT EXISTS "stock".products
(
    id uuid NOT NULL,
    vendor_id uuid NOT NULL,
    category_id uuid NOT NULL,
    name character varying(200) COLLATE pg_catalog."default" NOT NULL,
    description character varying(300) COLLATE pg_catalog."default" NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    quantity integer NOT NULL,
    product_status character varying(100) COLLATE pg_catalog."default" NOT NULL,
    file_url character varying COLLATE pg_catalog."default",
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS "idx_vendor_id"
    ON "stock".products
    (vendor_id);
CREATE INDEX IF NOT EXISTS "idx_category_id"
    ON "stock".products
    (category_id);
CREATE INDEX IF NOT EXISTS "idx_name"
    ON "stock".products
    (name);
CREATE INDEX IF NOT EXISTS "idx_description"
    ON "stock".products
    (description);
CREATE INDEX IF NOT EXISTS "idx_unit_price"
    ON "stock".products
    (unit_price);
CREATE INDEX IF NOT EXISTS "idx_quantity"
    ON "stock".products
    (quantity);
CREATE INDEX IF NOT EXISTS "idx_product_status"
    ON "stock".products
    (product_status);
CREATE INDEX IF NOT EXISTS "idx_file_url"
    ON "stock".products
    (file_url);
CREATE INDEX IF NOT EXISTS "idx_updated_at"
    ON "stock".products
    (updated_at);
CREATE INDEX IF NOT EXISTS "idx_created_at"
    ON "stock".products
    (created_at);
CREATE UNIQUE INDEX IF NOT EXISTS "idx_vendor_id_name"
    ON "stock".products
    (vendor_id, name);

--stock order
CREATE TABLE IF NOT EXISTS "stock".orders
(
    id uuid NOT NULL,
    vendor_id uuid NOT NULL,
    customer_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    order_status character varying(100) COLLATE pg_catalog."default" NOT NULL,
    failure_messages character varying COLLATE pg_catalog."default",
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS "idx_vendor_id"
    ON "stock".orders
    (vendor_id);
CREATE INDEX IF NOT EXISTS "idx_customer_id"
    ON "stock".orders
    (customer_id);
CREATE INDEX IF NOT EXISTS "idx_price"
    ON "stock".orders
    (price);
CREATE INDEX IF NOT EXISTS "idx_order_status"
    ON "stock".orders
    (order_status);
CREATE INDEX IF NOT EXISTS "idx_failure_messages"
    ON "stock".orders
    (failure_messages);
CREATE INDEX IF NOT EXISTS "idx_updated_at"
    ON "stock".orders
    (updated_at);
CREATE INDEX IF NOT EXISTS "idx_created_at"
    ON "stock".orders
    (created_at);

--stock order items
CREATE TABLE IF NOT EXISTS "stock".order_items
(
     id bigint NOT NULL,
     order_id uuid NOT NULL,
     product_id uuid NOT NULL,
     quantity integer NOT NULL,
     price numeric(10,2) NOT NULL,
     sub_total numeric(10,2) NOT NULL,
     updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (id,order_id)
);
CREATE INDEX IF NOT EXISTS "idx_product_id"
    ON "stock".order_items
    (product_id);
CREATE INDEX IF NOT EXISTS "idx_quantity"
    ON "stock".order_items
    (quantity);
CREATE INDEX IF NOT EXISTS "idx_price"
    ON "stock".order_items
    (price);
CREATE INDEX IF NOT EXISTS "idx_sub_total"
    ON "stock".order_items
    (sub_total);
CREATE INDEX IF NOT EXISTS "idx_updated_at"
    ON "stock".order_items
    (updated_at);
CREATE INDEX IF NOT EXISTS "idx_created_at"
    ON "stock".order_items
    (created_at);

--ALTER TABLE "stock".order_items
--    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (order_id)
--    REFERENCES "stock".orders (id) MATCH SIMPLE
--    ON UPDATE NO ACTION
--    ON DELETE CASCADE
--    NOT VALID;
--
--ALTER TABLE "stock".order_items
--    ADD CONSTRAINT "FK_PRODUCT_ID" FOREIGN KEY (product_id)
--    REFERENCES "stock".products (id) MATCH SIMPLE
--    ON UPDATE NO ACTION
--    ON DELETE NO ACTION
--    NOT VALID;

-- order file outbox
CREATE TABLE IF NOT EXISTS "stock".order_file_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status character varying(100) COLLATE pg_catalog."default" NOT NULL,
    saga_status character varying(100) COLLATE pg_catalog."default" NOT NULL,
    order_status character varying(100) COLLATE pg_catalog."default" NOT NULL,
    version integer NOT NULL,
    CONSTRAINT order_file_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS "idx_type_outbox_saga"
    ON "stock".order_file_outbox
    (type, outbox_status, saga_status);
CREATE INDEX IF NOT EXISTS "idx_saga_id"
    ON "stock".order_file_outbox
    (saga_id);
CREATE INDEX IF NOT EXISTS "idx_created_at"
    ON "stock".order_file_outbox
    (created_at);
CREATE INDEX IF NOT EXISTS "idx_processed_at"
    ON "stock".order_file_outbox
    (processed_at);
CREATE INDEX IF NOT EXISTS "idx_type"
    ON "stock".order_file_outbox
    (type);
CREATE INDEX IF NOT EXISTS "idx_payload"
    ON "stock".order_file_outbox
    (payload);
CREATE INDEX IF NOT EXISTS "idx_outbox_status"
    ON "stock".order_file_outbox
    (outbox_status);
CREATE INDEX IF NOT EXISTS "idx_saga_status"
    ON "stock".order_file_outbox
    (saga_status);
CREATE INDEX IF NOT EXISTS "idx_order_status"
    ON "stock".order_file_outbox
    (order_status);
CREATE INDEX IF NOT EXISTS "idx_version"
    ON "stock".order_file_outbox
    (version);

-- order finance outbox
CREATE TABLE IF NOT EXISTS "stock".order_finance_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status character varying(100) COLLATE pg_catalog."default" NOT NULL,
    saga_status character varying(100) COLLATE pg_catalog."default" NOT NULL,
    order_status character varying(100) COLLATE pg_catalog."default" NOT NULL,
    version integer NOT NULL,
    CONSTRAINT order_finance_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS "idx_type_outbox_saga"
    ON "stock".order_finance_outbox
    (type, outbox_status, saga_status);
CREATE INDEX IF NOT EXISTS "idx_saga_id"
    ON "stock".order_finance_outbox
    (saga_id);
CREATE INDEX IF NOT EXISTS "idx_created_at"
    ON "stock".order_finance_outbox
    (created_at);
CREATE INDEX IF NOT EXISTS "idx_processed_at"
    ON "stock".order_finance_outbox
    (processed_at);
CREATE INDEX IF NOT EXISTS "idx_type"
    ON "stock".order_finance_outbox
    (type);
CREATE INDEX IF NOT EXISTS "idx_payload"
    ON "stock".order_finance_outbox
    (payload);
CREATE INDEX IF NOT EXISTS "idx_outbox_status"
    ON "stock".order_finance_outbox
    (outbox_status);
CREATE INDEX IF NOT EXISTS "idx_saga_status"
    ON "stock".order_finance_outbox
    (saga_status);
CREATE INDEX IF NOT EXISTS "idx_order_status"
    ON "stock".order_finance_outbox
    (order_status);
CREATE INDEX IF NOT EXISTS "idx_version"
    ON "stock".order_finance_outbox
    (version);

