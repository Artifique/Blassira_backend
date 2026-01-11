# Configuration Render pour Blasira Backend

## Option 1 : Utiliser Docker (Recommandé)

1. Dans Render, sélectionnez **"Docker"** comme Environment
2. Render utilisera automatiquement le `Dockerfile` présent dans le repository
3. Aucune configuration supplémentaire nécessaire pour le build/start

## Option 2 : Configuration manuelle (si Docker ne fonctionne pas)

Si vous ne voyez pas "Docker" ou si vous préférez configurer manuellement :

1. **Environment** : Sélectionnez **"Docker"** (ou cherchez dans le menu déroulant)
2. **Build Command** : Laissez vide (Docker gère tout)
3. **Start Command** : Laissez vide (défini dans Dockerfile)

## Option 3 : Utiliser render.yaml (Configuration automatique)

Si votre repository contient un fichier `render.yaml` à la racine, Render le détectera automatiquement et configurera le service selon ce fichier.

Pour utiliser cette option :
1. Assurez-vous que `render.yaml` est à la racine de votre repository backend
2. Render détectera automatiquement la configuration
3. Vous n'avez qu'à connecter le repository et Render fera le reste

## Variables d'environnement requises

Dans l'onglet "Environment" de votre service Render, ajoutez :

```env
PORT=8080
DATABASE_URL=jdbc:mysql://VOTRE_HOST:3306/VOTRE_DB?useSSL=true&serverTimezone=UTC
DATABASE_USERNAME=votre_username
DATABASE_PASSWORD=votre_password
JWT_SECRET=votre_clé_secrète_64_caractères_base64
CORS_ALLOWED_ORIGINS=https://votre-frontend.vercel.app
STORAGE_LOCATION=uploads
SHOW_SQL=false
HIBERNATE_SQL_LOG=WARN
HIBERNATE_BIND_LOG=WARN
FLYWAY_CLEAN_DISABLED=true
```

## Note importante

Si "Java" n'apparaît pas dans le menu déroulant, utilisez **"Docker"** - c'est la méthode recommandée pour les applications Spring Boot sur Render.
