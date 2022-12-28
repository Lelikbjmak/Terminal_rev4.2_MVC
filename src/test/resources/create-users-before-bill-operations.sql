delete from receipts;
delete from investments;
delete from bill;
delete from client;
delete from users_roles;
delete from users;

insert into users values
(1, 1, 0, null , 'testUser@gmail.com', '$2a$16$KQ3G.w0tbnCwx6t4ubAoVezFnGJeTaetbWTzHkYB.2IM6HkgaL5N6', null , 0,  'testUser'),
(2, 1, 0, null , 'testUserTo@gmail.com', '$2a$16$KQ3G.w0tbnCwx6t4ubAoVezFnGJeTaetbWTzHkYB.2IM6HkgaL5N6', null , 0,  'testUserTo');


insert into users_roles values
(1, 1),
(2, 1);

insert into client values(1, '2002-02-02', 'First Last Middle', 'AB1111111', '111111111', 1),
                         (2, '2002-02-02', 'First Last Middle', 'AB2222222', '111111111', 2);

