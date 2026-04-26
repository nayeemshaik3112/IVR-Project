INSERT INTO customers (id, first_name, last_name, phone, email)
SELECT 1, 'Shaik', 'Nayeem', '6301898948', 'shaik.nayeem@example.com'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE id = 1);

INSERT INTO customers (id, first_name, last_name, phone, email)
SELECT 2, 'Gooty', 'Shaik', '8142379997', 'gooty.shaik@example.com'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE id = 2);
