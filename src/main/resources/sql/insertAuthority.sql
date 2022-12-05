insert into authorities (authority, user_id)
values (:AUTHORITY, :USER_ID)
RETURNING id;
