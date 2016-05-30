# Simple UAA (User Authentication and Authorization)

### Build

``` console
$ ./mvnw clean package
```

### Run locally

``` console
$ java -jar target/uaa-0.0.1-SNAPSHOT.jar
```

Go http://localhost:9999/uaa

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


## Issue access token


``` console
$ curl -u 00000000-0000-0000-0000-000000000000:00000000-0000-0000-0000-000000000000 localhost:19999/uaa/oauth/token -d grant_type=password -d username=maki@example.com -d password=demo
{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sInVzZXJfaWQiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJ1c2VyX25hbWUiOiJ5YW1hZGFAZXhhbXBsZS5jb20iLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNDY0NjU5OTMzLCJnaXZlbl9uYW1lIjoi5aSq6YOOIiwiZGlzcGxheV9uYW1lIjoi5bGx55SwIOWkqumDjiIsImZhbWlseV9uYW1lIjoi5bGx55SwIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdLCJqdGkiOiI0M2NkMDMwYi0zZjI2LTQxZTctODMwNS1iM2MwY2Y3YzQ2YzciLCJjbGllbnRfaWQiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAifQ.sa3Sd4MdgE4U2267gAiujzcj19raWWdMvgU7VE81flMcYl1LBMmKAsQOG9YusG-shfqp-m47zupIm5NGjMNZiTE1NzgfcygimEhy4Ojy7yEt1UR_tIsxvSgo9Q1Uo2NOHspC20OG2YZEwK0M_tE1Tttnbm9RB2fm_zENDwCzzdv8rz2qhiwka8ko3blg9Dtkb9f8wZ-WZ4jsxQ5SYuhOlrjXdx2UckPyhqLZ34PgXBq7HzrkF3KBjN9We7Nu6wTbeb37KqBDS9SBfTI9cD63N6FV6K7lT9eMKJgWDU6MXRu_NJaRm1SeTcyoftuF4OQwvSQH7jIfMp6IQw8-m3cC5w","token_type":"bearer","refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sInVzZXJfaWQiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJ1c2VyX25hbWUiOiJ5YW1hZGFAZXhhbXBsZS5jb20iLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiYXRpIjoiNDNjZDAzMGItM2YyNi00MWU3LTgzMDUtYjNjMGNmN2M0NmM3IiwiZXhwIjoxNDY0ODMyNzMzLCJnaXZlbl9uYW1lIjoi5aSq6YOOIiwiZGlzcGxheV9uYW1lIjoi5bGx55SwIOWkqumDjiIsImZhbWlseV9uYW1lIjoi5bGx55SwIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiIsIlJPTEVfVVNFUiJdLCJqdGkiOiJlNDdhMGRlMS1lZmZjLTQ2ODItOGNkMy04ZDM5Y2U3OTQ0ZTIiLCJjbGllbnRfaWQiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAifQ.b-46mCnOQcPA43DiKJFnn4igzBpD8PsvsCOGkEMR9e8uv5EDw-Y5kAbIMLX0iyuO17kd7Kxwi1LJArG7oNvuZtI6Gbf9xMyrKMOwNKo2XkuTla2CXnNrCwji39px4lKcirKYOBk2QuuXMKLnUEOvH6sNbCQeVHPRj13Xf6_Abag-bpA53-R8iHhNvbkgC4mIseBk48PbHWcGDMKk5-QKfZ02mKpAtAtfrbWsrzd5XOvhSSIlBpk3CCRsJ5DlYqM5w8a-sJHK-84urC-rcWCUQuVDLKEHWxYkifQ2j8c6N31Yz4xfMNOIXL_PFPRmpOLfT33mOR_ef5XPKE15EIvGhg","expires_in":86399,"scope":"read write","user_id":"00000000-0000-0000-0000-000000000000","family_name":"Maki","given_name":"Toshiaki","display_name":"Maki Toshiaki","jti":"43cd030b-3f26-41e7-8305-b3c0cf7c46c7"}
```

## Screens

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/fdbe27b8-5309-d118-399b-67c4b733121c.png">

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/a7c746f0-4012-9828-7780-a356e5dbf4f3.png">

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/c292eca0-1cba-e8ff-d063-e0eccec17e8e.png">
