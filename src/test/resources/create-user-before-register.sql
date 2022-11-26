delete from client;
delete from users_roles;
delete from ver_token;
delete from users;

insert into users values
(1, 1, 0, null , 'test@mail.ru', '$2a$16$KQ3G.w0tbnCwx6t4ubAoVezFnGJeTaetbWTzHkYB.2IM6HkgaL5N6', null, 0,  'test');

insert into ver_token values (1, null , now() + interval 1 day, 'testToken', 1);

insert into users_roles values
(1, 1);

insert into client values(1, '2000-02-02', 'Test Test', 'AB1234567', '1234567', 1);
