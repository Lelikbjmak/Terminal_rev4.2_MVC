## Description
Spring MVC online bank interpretation.<br>
Imitation of online bank, make traansactions between users. Withdraw money, dep money, unfortunately not real :), but still.

### Functional
As far as u know online bank system include such operations:
* user branch:
  * register user
  * update credentials (username, password)
  * reset password (if forgot it) via email
* bill branch:
  * apply for bill
* investment branch:
  * apply for invest
  * close invest
* operations:
  * P2P
  * deposit
  * cash withdrawal
  * convert
  * browse relevant currency rates ( to obtain relevant currenmcy rates we use [Currency data API](https://apilayer.com/marketplace/currency_data-api))

Among other things some system functions were implemented:
* Temporary lock user after several failed attempts to sign in
* Temporary locked bill after several failed attempts to approve transaction
* Deactivate all expired bills (scheduled - accomplish once per day)
* Deactivate all expired users (scheduled - accomplish once per day)
* Deactivate investments that are expired (scheduled - accomplish once per day)
* notify users about all their bills, invetsts if the will be expired after 7, 3, 1 days

### Authentication
Authentication is proccessed by Spring Security.<br>
Authentication type - Form login.<br>

Implemented Failure & Success AuthenticationListeners to lock users after invalid credentials. Unlock users if the make successfull attempt to sign in after the were temporary locked. Among other things we redirect user to different pages after succesfull, failed sign in.

### Technologies
* Spring boot
* Spring Security
* Hibernate
* Spring MVC
* MySQL
* Maven

### Storage
Database - MySQL.

