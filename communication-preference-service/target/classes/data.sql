INSERT INTO communication_preference (id, customer_id, preferred_channel, sms_opt_in, email_opt_in, voice_opt_in,
                                      quiet_hours_start, quiet_hours_end, escalation_channel, language_code)
SELECT 1, 1, 'SMS', true, true, true, '22:00:00', '07:00:00', 'EMAIL', 'en'
WHERE NOT EXISTS (SELECT 1 FROM communication_preference WHERE id = 1);