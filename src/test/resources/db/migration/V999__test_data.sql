INSERT INTO recycle."user" (id, user_login, password_hash, email, user_name, user_role, created_at, updated_at)
VALUES (
           gen_random_uuid(),
           'test_admin',
           '$2a$10$nWSrnbErrPRnAR88/1Y.dO1/.3d/7odof1IeO352jm.YnVCQW394K',
           'admin@test.com',
           'Test Admin',
           '1',
           now(),
           now()
       );