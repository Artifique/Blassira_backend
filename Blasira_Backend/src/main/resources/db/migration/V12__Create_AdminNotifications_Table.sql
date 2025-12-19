CREATE TABLE admin_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_admin_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    recipient_ids_json TEXT,
    sent_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (sender_admin_id) REFERENCES user_accounts(id)
);