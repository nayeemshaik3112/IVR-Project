INSERT INTO policies (id, customer_id, policy_number, policy_type, status, premium_amount)
SELECT 1, 1, 'POLICY_01', 'HEALTH', 'ACTIVE', 5500.00
WHERE NOT EXISTS (SELECT 1 FROM policies WHERE id = 1);

INSERT INTO policies (id, customer_id, policy_number, policy_type, status, premium_amount)
SELECT 2, 1, 'POLICY_02', 'AUTO', 'ACTIVE', 7200.00
WHERE NOT EXISTS (SELECT 1 FROM policies WHERE id = 2);

INSERT INTO policies (id, customer_id, policy_number, policy_type, status, premium_amount)
SELECT 3, 2, 'POLICY_03', 'LIFE', 'PENDING_RENEWAL', 8900.00
WHERE NOT EXISTS (SELECT 1 FROM policies WHERE id = 3);

INSERT INTO policies (id, customer_id, policy_number, policy_type, status, premium_amount)
SELECT 4, 2, 'POLICY_04', 'LIFE', 'PENDING_RENEWAL', 8900.00
WHERE NOT EXISTS (SELECT 1 FROM policies WHERE id = 4);
