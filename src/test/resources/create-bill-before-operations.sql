delete from bill;

--$2a$16$Lks0GbQmJexPHDcbt6pg5.5OTm6vRfd6pyNVwKaAxL6JdvHCHP07G (2515)

insert into bill values('1111 1111 1111 1111', 1, 'USD', 0, 3333.33, null, '$2a$16$Lks0GbQmJexPHDcbt6pg5.5OTm6vRfd6pyNVwKaAxL6JdvHCHP07G', 0, 'testType', '2027-02-02', 1),
('2222 2222 2222 2222', 0, 'USD', 0, 3333.33, null, '$2a$16$Lks0GbQmJexPHDcbt6pg5.5OTm6vRfd6pyNVwKaAxL6JdvHCHP07G', 0, 'testType', '2027-02-02', 1),
('3333 3333 3333 3333', 1, 'USD', 0, -100.00, now(), '$2a$16$Lks0GbQmJexPHDcbt6pg5.5OTm6vRfd6pyNVwKaAxL6JdvHCHP07G', 1, 'testType', '2027-02-02', 1),
('4444 4444 4444 4444', 1, 'BYN', 0, 3333.33, null, '$2a$16$Lks0GbQmJexPHDcbt6pg5.5OTm6vRfd6pyNVwKaAxL6JdvHCHP07G', 0, 'testType', '2027-02-02', 2);