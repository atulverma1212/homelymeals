select * from stock_items;

ALTER TABLE stock_items
DROP CONSTRAINT stock_items_quantity_check;

INSERT INTO settings (name, value, description) VALUES
                                                    ('low_stock_threshold', '10', 'Items below this quantity will be highlighted in red'),
                                                    ('order_prep_time', '30', 'Default preparation time in minutes'),
                                                    ('sms_notification_to_customer', 'true', 'Flag to enable/disable SMS notifications to customers'),
                                                    ('delivery_radius', '5', 'Maximum delivery radius in kilometers');

ALTER TABLE orders
    ADD CONSTRAINT orders_status_check
        CHECK ((status)::text = ANY
    ((ARRAY ['PENDING'::character varying, 'CONFIRMED'::character varying, 'PREPARING'::character varying, 'READY'::character varying, 'COMPLETED'::character varying, 'CANCELLED'::character varying, 'RETURNED'::character varying])::text[]));


INSERT INTO settings (name, value, description) VALUES
 ('sms_notification_to_customer', 'true', 'Flag to enable/disable SMS notifications to customers');
