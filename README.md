# Simple UAA (User Account and Authentication)

### Build

``` console
$ ./mvnw clean package
```

### Run locally

``` console
$ java -jar target/uaa-0.0.1-SNAPSHOT.jar
```

Go to [http://localhost:9999/uaa](http://localhost:9999/uaa)

Initial `ADMIN` user is `maki@example.com` / `demo`.

By default, MySQL and following properties are used. 

* `spring.datasource.url` ... `jdbc:mysql://localhost:3306/uaa`
* `spring.datasource.username` ... `root`
* `spring.datasource.password` ... empty

You can change like:

``` console
$ java -jar target/uaa-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:mysql://localhost:3306/foo --spring.datasource.username=foo --spring.datasource.password=foo
```

## How to `cf push`

No need to configure properties.

### Deploy to PCF

``` console
$ cf create-service p-mysql 100mb-dev uaa-db
$ cf push -d <your domain>
```

### Deploy to PCFDev

``` console
$ cf create-service p-mysql 100mb-dev uaa-db
$ cf push -b java_buildpack -d local.pcfdev.io
```

### Deploy to PWS

``` console
$ cf create-service cleardb spark uaa-db
$ cf push -b java_buildpack -d cfapps.io
```


## OAuth2 Endpoints

* `/uaa/oauth/token`
* `/uaa/oauth/authorize`
* `/uaa/oauth/check_token`
* `/uaa/oauth/token_key`
* `/uaa/user`

### Example

``` console
$ curl -u 00000000-0000-0000-0000-000000000000:00000000-0000-0000-0000-000000000000 localhost:19999/uaa/oauth/token -d grant_type=password -d username=maki@example.com -d password=demo
{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sInVzZXJfaWQiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJ1c2VyX25hbWUiOiJtYWtpQGV4YW1wbGUuY29tIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sImV4cCI6MTQ2NDU3NTcwMiwiZ2l2ZW5fbmFtZSI6IlRvc2hpYWtpIiwiZGlzcGxheV9uYW1lIjoiTWFraSBUb3NoaWFraSIsImZhbWlseV9uYW1lIjoiTWFraSIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwianRpIjoiN2UxNTk5MTQtYjE4Ny00NDE5LWE5ZTktZmZiOGQxMDJkYTNlIiwiY2xpZW50X2lkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIn0.sXQZRbomo9q6wDu4Qz8fARS9SzAJRUSb97489S90s70WKgbMVgmNURl17JUDnxRsx0GTnzGXtwAEs_RZdqGO3kUMIdD-ezfqLxLKV_hxAHsYk4u6lX5dBvLRRYLSGrRynE8og36MBUPqVQPzfc2pyM4yMN-cYrLbTPq0VVCZZl_ZUVFXtCGbTeiamgka6IxDD11KmKDXRxGbwd4OH0WZzT5siZpRhUMhLBvqm5035qkm80l5WCeG92N2HREmOiJDKhBZq7lK04k4_SBPiU3a1qGfk2nJKpWu8Jg0u1bLRNQmjyCAHCBAbCI2-8pTrmNCMrSObwpHO0RuIBrL9EYtgg","token_type":"bearer","refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sInVzZXJfaWQiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJ1c2VyX25hbWUiOiJtYWtpQGV4YW1wbGUuY29tIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sImF0aSI6IjdlMTU5OTE0LWIxODctNDQxOS1hOWU5LWZmYjhkMTAyZGEzZSIsImV4cCI6MTQ2NDgzMzEwMiwiZ2l2ZW5fbmFtZSI6IlRvc2hpYWtpIiwiZGlzcGxheV9uYW1lIjoiTWFraSBUb3NoaWFraSIsImZhbWlseV9uYW1lIjoiTWFraSIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwianRpIjoiMzFmOWRhMWMtZWM3ZS00OGFkLTg1MDUtMDI0YTk2ZmVhOWUxIiwiY2xpZW50X2lkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIn0.DhUwK2gOFRLpZ_F3f4FumlZWpB0UNJdClJqpVARkUS-SPR1gayzn_OIL4MyA5E7kH7tztcnPZFOsGSjEgPSmbhXOFrhRNCVekF5I3bmZtvmFzQGi-YqUh5ElRWVvZrV7DjJ8iW7aOn87gtO0CNvl1v5EfqxFaN1eerZi8dElphyK--S4XEI8Fq9hmp7dZqabWhj_50njh-s7V9mpdRn_owNmdXozfTd9Oc4tgCuiQEqXUGwNkgQGPY9FMY0llW6iVwcF5TdYKwb2oeco7VQWEELqMtBj8rXd5mtiFa_Jk9GULk15SyIONaZHJUtoYp1BmpOpVqnpWhuooEb-UWSMVQ","expires_in":1799,"scope":"read write","user_id":"00000000-0000-0000-0000-000000000000","family_name":"Maki","given_name":"Toshiaki","display_name":"Maki Toshiaki","jti":"7e159914-b187-4419-a9e9-ffb8d102da3e"}
```


## Screens

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/fdbe27b8-5309-d118-399b-67c4b733121c.png">

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/a7c746f0-4012-9828-7780-a356e5dbf4f3.png">

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/c292eca0-1cba-e8ff-d063-e0eccec17e8e.png">
