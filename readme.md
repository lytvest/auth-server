# 🌱 OAuth 2.0 Authorization Server - Теплицы школы 137

OAuth 2.0 сервер авторизации на Spring Boot Java 21 с красивой формой логина в стиле зеленой теплицы.

## 🚀 Быстрый старт

### Требования
- Java 21
- Gradle 8.x
- Nginx (для production)

### Запуск в режиме разработки

```bash
# Клонируйте проект
git clone <your-repo>
cd greenhouse-oauth-server

# Запустите сервер
./gradlew bootRun
```

Сервер запустится на `http://localhost:9000`

### Production развертывание

1. **Соберите JAR файл:**
```bash
./gradlew clean build
```

2. **Настройте Nginx:**
   Скопируйте конфигурацию из `nginx-greenbots.conf` в `/etc/nginx/sites-available/greenbots.ru`

```bash
sudo ln -s /etc/nginx/sites-available/greenbots.ru /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

3. **Запустите приложение:**
```bash
java -jar build/libs/greenhouse-oauth-server-1.0.0.jar
```

Или создайте systemd service:

```bash
sudo nano /etc/systemd/system/greenhouse-oauth.service
```

```ini
[Unit]
Description=Greenhouse OAuth Server
After=syslog.target network.target

[Service]
User=www-data
ExecStart=/usr/bin/java -jar /path/to/greenhouse-oauth-server-1.0.0.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl enable greenhouse-oauth
sudo systemctl start greenhouse-oauth
sudo systemctl status greenhouse-oauth
```

## 🔐 Конфигурация OAuth 2.0

### Endpoints

После развертывания на `https://greenbots.ru/ya-auth/`:

- **Authorization Endpoint:** `https://greenbots.ru/ya-auth/oauth2/authorize`
- **Token Endpoint:** `https://greenbots.ru/ya-auth/oauth2/token`
- **Login Page:** `https://greenbots.ru/ya-auth/login`
- **OIDC Configuration:** `https://greenbots.ru/ya-auth/.well-known/openid-configuration`
- **JWK Set:** `https://greenbots.ru/ya-auth/oauth2/jwks`

### Настройка клиента

**Client ID:** `greenhouse-school-137`  
**Client Secret:** `your-secret-key-change-me` (измените в `SecurityConfig.java`)  
**Redirect URI:** `https://social.yandex.net/broker/redirect`  
**Grant Types:** `authorization_code`, `refresh_token`  
**Scopes:** `openid`, `profile`, `email`

### Пример запроса авторизации от Яндекса

```
GET https://greenbots.ru/ya-auth/oauth2/authorize?
    response_type=code&
    client_id=greenhouse-school-137&
    redirect_uri=https://social.yandex.net/broker/redirect&
    scope=openid%20profile%20email&
    state=random_state_string
```

### Получение токена

```bash
curl -X POST https://greenbots.ru/ya-auth/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u greenhouse-school-137:your-secret-key-change-me \
  -d "grant_type=authorization_code" \
  -d "code=AUTHORIZATION_CODE" \
  -d "redirect_uri=https://social.yandex.net/broker/redirect"
```

## 👥 Тестовые пользователи

| Логин    | Пароль     | Роли        |
|----------|------------|-------------|
| admin    | admin123   | USER, ADMIN |
| teacher  | teacher123 | USER        |

**⚠️ ВАЖНО:** Измените эти учетные данные перед production развертыванием!

## 📝 Изменение конфигурации

### Добавление новых пользователей

Отредактируйте метод `userDetailsService()` в `SecurityConfig.java`:

```java
@Bean
public UserDetailsService userDetailsService() {
    UserDetails newUser = User.builder()
        .username("newuser")
        .password(passwordEncoder().encode("password"))
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(admin, teacher, newUser);
}
```

### Изменение Client Secret

В `SecurityConfig.java`:

```java
.clientSecret(passwordEncoder().encode("your-new-secret-key"))
```

### Добавление нового redirect URI

```java
.redirectUri("https://social.yandex.net/broker/redirect")
.redirectUri("https://another-redirect-uri.com/callback")
```

## 🎨 Кастомизация формы логина

Форма находится в `src/main/resources/templates/login.html`

Вы можете изменить:
- Цвета (переменные CSS в `<style>`)
- Логотип (`.logo-icon`)
- Текст заголовка
- Анимации

## 🔧 Troubleshooting

### Проблема: "Path component for issuer is not supported"

**Решение:** Убедитесь, что в `AuthorizationServerSettings` issuer указан без path component:
```java
.issuer("https://greenbots.ru") // ✅ Правильно
.issuer("https://greenbots.ru/ya-auth") // ❌ Неправильно
```

### Проблема: Редиректы не работают через прокси

**Решение:** Проверьте nginx конфигурацию:
```nginx
proxy_set_header X-Forwarded-Prefix /ya-auth;
proxy_set_header X-Forwarded-Proto https;
proxy_set_header X-Forwarded-Host $host;
```

### Проблема: CSRF token errors

**Решение:** Убедитесь, что форма использует Thymeleaf:
```html
<form method="post" th:action="@{/login}">
```

## 📚 Дополнительные ресурсы

- [Spring Authorization Server Documentation](https://docs.spring.io/spring-authorization-server/docs/current/reference/html/)
- [OAuth 2.0 RFC](https://tools.ietf.org/html/rfc6749)
- [OpenID Connect Core](https://openid.net/specs/openid-connect-core-1_0.html)

## 📄 Лицензия

MIT License

## 🤝 Поддержка

При возникновении проблем создайте issue в репозитории проекта.