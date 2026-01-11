# Variables d'environnement - Backend

## Variables requises sur Render

```env
PORT=8080
DATABASE_URL=jdbc:mysql://host:port/database?useSSL=true&serverTimezone=UTC
DATABASE_USERNAME=votre_username
DATABASE_PASSWORD=votre_password
JWT_SECRET=votre_clé_secrète_64_caractères_base64
CORS_ALLOWED_ORIGINS=https://votre-frontend.vercel.app
```

## Variables optionnelles

```env
STORAGE_LOCATION=uploads
SHOW_SQL=false
HIBERNATE_SQL_LOG=WARN
HIBERNATE_BIND_LOG=WARN
FLYWAY_CLEAN_DISABLED=true
```

## Génération de JWT_SECRET

```bash
openssl rand -base64 64
```
