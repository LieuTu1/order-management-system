CREATE TABLE verification_tokens (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     token VARCHAR(255) NOT NULL,
                                     user_id BIGINT NOT NULL,
                                     expiry_date TIMESTAMP NOT NULL,

                                     PRIMARY KEY (id),

                                     CONSTRAINT uk_verification_tokens_token
                                         UNIQUE (token),

                                     CONSTRAINT fk_verification_tokens_user
                                         FOREIGN KEY (user_id)
                                             REFERENCES users(id)
                                             ON DELETE CASCADE
);