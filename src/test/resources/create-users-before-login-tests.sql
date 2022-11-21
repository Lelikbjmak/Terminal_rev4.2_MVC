delete from client;
delete from users_roles;
delete from users;

insert into users values
(1, 1, 0, null , 'test@mail.ru', '$2a$16$KQ3G.w0tbnCwx6t4ubAoVezFnGJeTaetbWTzHkYB.2IM6HkgaL5N6', null, 0,  'test'),
(2, 0, 0, null, 'test2@mail.ru', '$2a$16$KQ3G.w0tbnCwx6t4ubAoVezFnGJeTaetbWTzHkYB.2IM6HkgaL5N6',null, 0, 'test2'),
(3, 1, 3, '2025-11-09 22:10:36.895625', 'test3@mail.ru', '$2a$16$8G6RReUHW8rma7mBLbLIduTBKeouL3w68Y85oMB/RFAkGw0HB6XTm', null, 1, 'test3');

insert into users_roles values
(1, 1),
(2, 1),
(3, 1);

