CREATE TABLE orders (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  buyer_user_id     UUID NOT NULL REFERENCES users(id),
  status            VARCHAR(20) NOT NULL CHECK (status IN ('PLACED','COMPLETED','CANCELLED')),
  shipping_address  TEXT NOT NULL,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- product_id is nullable with ON DELETE SET NULL, not a hard requirement: order_items snapshot
-- everything display-relevant (name/price/currency/craft_type) at order time, so a product can
-- still be safely hard-deleted later (docs/stories-sana3-ma-sprint2.md Story 3.2) without
-- corrupting historical orders. product_id is only kept as a best-effort link back to a still-live
-- product page, not something order history logic depends on.
CREATE TABLE order_items (
  id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id                 UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id               UUID REFERENCES products(id) ON DELETE SET NULL,
  product_name_snapshot    VARCHAR(150) NOT NULL,
  price_amount_snapshot    NUMERIC(10,2) NOT NULL CHECK (price_amount_snapshot > 0),
  price_currency_snapshot  VARCHAR(3) NOT NULL,
  craft_type_snapshot      VARCHAR(100) NOT NULL,
  artisan_profile_id       UUID NOT NULL REFERENCES artisan_profiles(id),
  quantity                 INTEGER NOT NULL CHECK (quantity > 0),
  completed_at             TIMESTAMPTZ,
  created_at               TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_buyer_user_id ON orders (buyer_user_id);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
-- Story 6.3: an artisan lists order_items for their own products across all orders.
CREATE INDEX idx_order_items_artisan_profile_id ON order_items (artisan_profile_id);
