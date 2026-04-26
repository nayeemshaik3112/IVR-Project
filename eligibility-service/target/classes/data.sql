INSERT INTO eligibility_record (id, customer_id, policy_number, plan_code, coverage_status, effective_date, expiry_date,
                                deductible_remaining, copay_percentage, network_code, last_verified_at)
SELECT 1, 1, 'POL-10001', 'HMO-GOLD', 'ACTIVE', CURRENT_DATE - INTERVAL 30 DAY, CURRENT_DATE + INTERVAL 335 DAY,
       1200.00, 20.00, 'NATIONAL', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM eligibility_record WHERE id = 1);