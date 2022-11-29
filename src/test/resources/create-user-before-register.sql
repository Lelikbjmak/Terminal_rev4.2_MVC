delete from client;
delete from users_roles;
delete from ver_token;
delete from users;

insert into users values
(1, 1, 0, null , 'testUser@gmail.com', '$2a$16$KQ3G.w0tbnCwx6t4ubAoVezFnGJeTaetbWTzHkYB.2IM6HkgaL5N6', 'testResetPasswordToken', 0,  'testUser');

insert into ver_token values (1, null , now() + interval 1 day, 'testVerificationToken', 1);

insert into users_roles values
(1, 1);

insert into client values(1, '2002-02-02', 'First Last Middle', 'AB1111111', '111111111', 1);
