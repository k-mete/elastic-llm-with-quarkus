-- Create hibernate sequence
CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

-- Insert Categories (no dependencies)
INSERT INTO categories (uuid, tenant_id, name, description, created_at, created_by, is_active, is_deleted, version) 
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'default', 'Hardware', 'Computer hardware and components', CURRENT_TIMESTAMP, 'system', true, false, 0);

INSERT INTO categories (uuid, tenant_id, name, description, created_at, created_by, is_active, is_deleted, version) 
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'default', 'Software', 'Software licenses and applications', CURRENT_TIMESTAMP, 'system', true, false, 0);

-- Insert Brands (no dependencies)
INSERT INTO brands (uuid, tenant_id, name, description, created_at, created_by, is_active, is_deleted, version) 
VALUES ('550e8400-e29b-41d4-a716-446655440002', 'default', 'Dell', 'Dell Technologies', CURRENT_TIMESTAMP, 'system', true, false, 0);

INSERT INTO brands (uuid, tenant_id, name, description, created_at, created_by, is_active, is_deleted, version) 
VALUES ('550e8400-e29b-41d4-a716-446655440003', 'default', 'HP', 'Hewlett-Packard', CURRENT_TIMESTAMP, 'system', true, false, 0);

-- Insert Types (no dependencies)
INSERT INTO types (uuid, tenant_id, name, description, created_at, created_by, is_active, is_deleted, version) 
VALUES ('550e8400-e29b-41d4-a716-446655440004', 'default', 'Laptop', 'Portable computers', CURRENT_TIMESTAMP, 'system', true, false, 0);

INSERT INTO types (uuid, tenant_id, name, description, created_at, created_by, is_active, is_deleted, version) 
VALUES ('550e8400-e29b-41d4-a716-446655440005', 'default', 'Desktop', 'Desktop computers', CURRENT_TIMESTAMP, 'system', true, false, 0);

-- Insert Sub-Categories (depends on categories)
INSERT INTO sub_categories (uuid, tenant_id, name, description, category_id, created_at, created_by, is_active, is_deleted, version) 
VALUES ('550e8400-e29b-41d4-a716-446655440006', 'default', 'Processors', 'CPU and processors', 1, CURRENT_TIMESTAMP, 'system', true, false, 0);

INSERT INTO sub_categories (uuid, tenant_id, name, description, category_id, created_at, created_by, is_active, is_deleted, version) 
VALUES ('550e8400-e29b-41d4-a716-446655440007', 'default', 'Operating Systems', 'OS licenses', 2, CURRENT_TIMESTAMP, 'system', true, false, 0);

-- Insert Assets (depends on brands, categories, types, and optionally sub_categories)
INSERT INTO assets (uuid, tenant_id, name, serial_number, brand_id, category_id, type_id, sub_category_id, purchase_date, purchase_price, description, status, created_at, created_by, is_active, is_deleted, version) 
VALUES (
    '550e8400-e29b-41d4-a716-446655440008',
    'default',
    'Dell Latitude 5420',
    'LAT5420-001',
    1, -- Dell brand_id
    1, -- Hardware category_id
    1, -- Laptop type_id
    1, -- Processors sub_category_id
    CURRENT_TIMESTAMP,
    1299.99,
    'Business laptop with 16GB RAM, 512GB SSD',
    'IN_USE',
    CURRENT_TIMESTAMP,
    'system',
    true,
    false,
    0
);

-- Insert Asset Location History (depends on assets)
INSERT INTO asset_location_history (uuid, tenant_id, asset_id, location, building, floor, room, notes, moved_at, moved_by, created_at, created_by, is_active, is_deleted, version) 
VALUES (
    '550e8400-e29b-41d4-a716-446655440009',
    'default',
    1, -- asset_id from above
    'Headquarters',
    'Main Building',
    '2nd Floor',
    'Room 201',
    'Initial deployment location',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    true,
    false,
    0
);

-- Insert Asset Condition History (depends on assets)
INSERT INTO asset_condition_history (uuid, tenant_id, asset_id, condition, notes, recorded_at, recorded_by, created_at, created_by, is_active, is_deleted, version) 
VALUES (
    '550e8400-e29b-41d4-a716-446655440010',
    'default',
    1, -- asset_id
    'NEW',
    'Asset received in perfect condition',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    true,
    false,
    0
);

-- Insert Issues (depends on assets)
INSERT INTO issues (uuid, tenant_id, asset_id, title, description, priority, status, resolution, reported_at, reported_by, created_at, created_by, is_active, is_deleted, version) 
VALUES (
    '550e8400-e29b-41d4-a716-446655440011',
    'default',
    1, -- asset_id
    'Initial Setup Required',
    'New laptop needs initial setup and software installation',
    'LOW',
    'OPEN',
    null,
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    true,
    false,
    0
);

-- Insert Audit Trail (depends on assets)
INSERT INTO audit_trails (uuid, tenant_id, entity_type, entity_id, action, old_value, new_value, action_at, action_by, ip_address, user_agent, created_at, created_by, is_active, is_deleted, version) 
VALUES (
    '550e8400-e29b-41d4-a716-446655440012',
    'default',
    'Asset',
    1, -- entity_id (asset_id)
    0, -- CREATE action (ORDINAL type)
    null,
    '{"name": "Dell Latitude 5420", "status": "IN_USE"}',
    CURRENT_TIMESTAMP,
    'system',
    '127.0.0.1',
    'Initial Import',
    CURRENT_TIMESTAMP,
    'system',
    true,
    false,
    0
); 