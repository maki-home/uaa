# Simple UAA (User Account and Authentication)

a.k.a. Maki UAA

### Build

``` console
$ cd uaa-server
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

## How to deploy to Cloud Foundry

No need to configure properties.

### Deploy to PCF

``` console
$ cf create-service p-mysql 100mb-dev uaa-db
$ cf push your-uaa-b java_buildpack_offline
```

### Deploy to PCFDev

``` console
$ cf create-service p-mysql 100mb-dev uaa-db
$ cf push your-uaa
```

### Deploy to PWS

``` console
$ cf create-service cleardb spark uaa-db
$ cf push your-uaa
```


## OAuth2 Endpoints

* `/uaa/oauth/token`
* `/uaa/oauth/authorize`
* `/uaa/check_token`
* `/uaa/token_key`
* `/uaa/userinfo`

### Example

``` console
$ curl -u 00000000-0000-0000-0000-000000000000:00000000-0000-0000-0000-000000000000 localhost:19999/uaa/oauth/token -d grant_type=password -d username=maki@example.com -d password=demo
{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sInVzZXJfaWQiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJ1c2VyX25hbWUiOiJtYWtpQGV4YW1wbGUuY29tIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sImV4cCI6MTQ2NDU3NTcwMiwiZ2l2ZW5fbmFtZSI6IlRvc2hpYWtpIiwiZGlzcGxheV9uYW1lIjoiTWFraSBUb3NoaWFraSIsImZhbWlseV9uYW1lIjoiTWFraSIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwianRpIjoiN2UxNTk5MTQtYjE4Ny00NDE5LWE5ZTktZmZiOGQxMDJkYTNlIiwiY2xpZW50X2lkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIn0.sXQZRbomo9q6wDu4Qz8fARS9SzAJRUSb97489S90s70WKgbMVgmNURl17JUDnxRsx0GTnzGXtwAEs_RZdqGO3kUMIdD-ezfqLxLKV_hxAHsYk4u6lX5dBvLRRYLSGrRynE8og36MBUPqVQPzfc2pyM4yMN-cYrLbTPq0VVCZZl_ZUVFXtCGbTeiamgka6IxDD11KmKDXRxGbwd4OH0WZzT5siZpRhUMhLBvqm5035qkm80l5WCeG92N2HREmOiJDKhBZq7lK04k4_SBPiU3a1qGfk2nJKpWu8Jg0u1bLRNQmjyCAHCBAbCI2-8pTrmNCMrSObwpHO0RuIBrL9EYtgg","token_type":"bearer","refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sInVzZXJfaWQiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJ1c2VyX25hbWUiOiJtYWtpQGV4YW1wbGUuY29tIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sImF0aSI6IjdlMTU5OTE0LWIxODctNDQxOS1hOWU5LWZmYjhkMTAyZGEzZSIsImV4cCI6MTQ2NDgzMzEwMiwiZ2l2ZW5fbmFtZSI6IlRvc2hpYWtpIiwiZGlzcGxheV9uYW1lIjoiTWFraSBUb3NoaWFraSIsImZhbWlseV9uYW1lIjoiTWFraSIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwianRpIjoiMzFmOWRhMWMtZWM3ZS00OGFkLTg1MDUtMDI0YTk2ZmVhOWUxIiwiY2xpZW50X2lkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIn0.DhUwK2gOFRLpZ_F3f4FumlZWpB0UNJdClJqpVARkUS-SPR1gayzn_OIL4MyA5E7kH7tztcnPZFOsGSjEgPSmbhXOFrhRNCVekF5I3bmZtvmFzQGi-YqUh5ElRWVvZrV7DjJ8iW7aOn87gtO0CNvl1v5EfqxFaN1eerZi8dElphyK--S4XEI8Fq9hmp7dZqabWhj_50njh-s7V9mpdRn_owNmdXozfTd9Oc4tgCuiQEqXUGwNkgQGPY9FMY0llW6iVwcF5TdYKwb2oeco7VQWEELqMtBj8rXd5mtiFa_Jk9GULk15SyIONaZHJUtoYp1BmpOpVqnpWhuooEb-UWSMVQ","expires_in":1799,"scope":"read write","user_id":"00000000-0000-0000-0000-000000000000","family_name":"Maki","given_name":"Toshiaki","display_name":"Maki Toshiaki","jti":"7e159914-b187-4419-a9e9-ffb8d102da3e"}
```

#### Protect Your Spring Boot Resource Server
In your [Resource Server](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security-oauth2-resource-server),
Put `@EnableResourceServer` on you app and configure `application.properties` :

``` properties
auth-server=http://localhost:9999/uaa
security.oauth2.resource.user-info-uri=${auth-server}/userinfo
```

(This API requires `openid` scope)

or

``` properties
auth-server=http://localhost:9999/uaa
security.oauth2.resource.token-info-uri=${auth-server}/check_token
```

If you prefer using JWT,

``` properties
auth-server=http://localhost:9999/uaa
security.oauth2.resource.jwt.key-uri=${auth-server}/token_key
```

#### OAuth2 SSO with Spring Boot
This UAA can be used Account service for [Single Sign On](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security-oauth2-single-sign-on).

Put `@EnableOAuth2Sso` on you app and configure `application.properties` :

``` properties
auth-server=http://localhost:9999/uaa
security.oauth2.client.client-id=<You can see ClientId in Dashboard>
security.oauth2.client.client-secret=<You can see ClientSecret in Dashboard>
security.oauth2.client.access-token-uri=${auth-server}/oauth/token
security.oauth2.client.user-authorization-uri=${auth-server}/oauth/authorize
security.oauth2.client.scope=read,write,openid
security.oauth2.resource.user-info-uri=${auth-server}/userinfo
```

If you prefer using JWT,

``` properties
auth-server=http://localhost:9999/uaa
security.oauth2.client.client-id=<You can see ClientId in Dashboard>
security.oauth2.client.client-secret=<You can see ClientSecret in Dashboard>
security.oauth2.client.access-token-uri=${auth-server}/oauth/token
security.oauth2.client.user-authorization-uri=${auth-server}/oauth/authorize
security.oauth2.client.scope=read,write
security.oauth2.resource.jwt.key-uri=${auth-server}/token_key
```

## Enable Cloud Foundry Service Broker

Maki UAA implements [Service Broker API v 2.8](https://docs.cloudfoundry.org/services/api-v2.8.html) which is compatible with [Single Sign-On for PCF](http://docs.pivotal.io/p-identity).

### How to enable Service Broker

```
$ cf create-service-broker your-uaa user password http://your-uaa.<domain>/uaa --space-scoped
```

username and password can be configured by setting `security.user.name` and `security.user.password` before `cf create-service-broker` as follows:

``` console
$ cf set-env your-uaa security.user.name admin
$ cf set-env your-uaa security.user.password pass
$ cf restart your-uaa
```

If you are CF admin and want to publish this service to all/specific organization, you can use `enable-service-access` like following:


``` console
$ cf create-service-broker your-uaa user password http://your-uaa.<domain>/uaa
$ cf enable-service-access your-uaa [-o ORG]
```

Then, you'll see `maki-uaa` in marketplace.

``` console
$ cf marketplace

service                       plans                     description
maki-uaa                      standard                  Simple UAA (User Account and Authentication)
...
```

### Create service instance

This service broker create an application, which means "OAuth Client", per not service instance binding but **service instance**. 

``` console
$ cf create-service maki-uaa standard my-sso -c '{"appUrl":"https://my-app.example.com", "appName":"My App", "redirectUrls":["https://my-app.example.com/login"]}'
$ cf bind-service my-app my-sso
$ cf env my-app

System-Provided:
{
 "VCAP_SERVICES": {
  "maki-uaa": [
   {
    "credentials": {
     "auth_domain": "https://your-uaa.cfapps.io/uaa",
     "client_id": "3571c28d-083b-4319-a242-f161b9ccb7e2",
     "client_secret": "021bd44b-ea24-45af-950e-136e5a3e19d2"
    },
    "label": "maki-uaa",
    "name": "my-sso",
    "plan": "standard",
    "provider": null,
    "syslog_drain_url": null,
    "tags": [
     "sso",
     "oauth2"
    ],
    "volume_mounts": []
   }
  ]
}
```

> **Note:**
> 
> [Spring Cloud Single Sign-On Connector](https://github.com/pivotal-cf/spring-cloud-sso-connector) is not available at this moment because [it checks whether `label` in `VCAP_SERVICES` is `p-identity`](https://github.com/pivotal-cf/spring-cloud-sso-connector/blob/1.1.0.RELEASE/src/main/java/io/pivotal/spring/cloud/SsoServiceInfoCreator.java#L16).
> You could use use this connector if you change [catalog.json](https://github.com/maki-home/uaa/blob/master/src/main/resources/catalog.json#L5) from `maki-uaa` to `p-identity`.
>
> It's still under the [pul request](https://github.com/pivotal-cf/spring-cloud-sso-connector/pull/5).

## Screens

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/fdbe27b8-5309-d118-399b-67c4b733121c.png">

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/a7c746f0-4012-9828-7780-a356e5dbf4f3.png">

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/c292eca0-1cba-e8ff-d063-e0eccec17e8e.png">
