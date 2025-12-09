# Guide Docker - Projet JEE

Ce guide explique comment dockeriser et exécuter l'application Spring Boot avec MySQL.

## Prérequis

- Docker installé
- Docker Compose installé

## Structure Docker

- `Dockerfile` : Image Docker pour l'application Spring Boot
- `docker-compose.yml` : Configuration pour production (MySQL + App)
- `docker-compose.dev.yml` : Configuration pour développement (H2)

## Commandes Docker

### Production (avec MySQL)

1. **Construire et démarrer les conteneurs** :
   ```bash
   docker-compose up -d
   ```

2. **Voir les logs** :
   ```bash
   docker-compose logs -f app
   ```

3. **Arrêter les conteneurs** :
   ```bash
   docker-compose down
   ```

4. **Arrêter et supprimer les volumes** :
   ```bash
   docker-compose down -v
   ```

5. **Reconstruire l'image** :
   ```bash
   docker-compose build --no-cache
   docker-compose up -d
   ```

### Développement (avec H2)

```bash
docker-compose -f docker-compose.dev.yml up -d
```

## Accès à l'application

Une fois les conteneurs démarrés :

- **Application** : http://localhost:8080
- **API REST** : http://localhost:8080/api/
- **Admin Dashboard** : http://localhost:8080/admin/dashboard
- **MySQL** : localhost:3306
  - Username: `projetjee`
  - Password: `projetjeepassword`
  - Database: `projetjee`

## Configuration

### Variables d'environnement

Vous pouvez personnaliser la configuration via des variables d'environnement dans `docker-compose.yml` :

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/projetjee
  SPRING_DATASOURCE_USERNAME: projetjee
  SPRING_DATASOURCE_PASSWORD: projetjeepassword
  JWT_SECRET: votre-secret-jwt
  MAIL_USERNAME: votre-email@gmail.com
  MAIL_PASSWORD: votre-mot-de-passe
```

### Fichier .env (recommandé)

Créez un fichier `.env` à la racine du projet :

```env
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=projetjee
MYSQL_USER=projetjee
MYSQL_PASSWORD=projetjeepassword
JWT_SECRET=MySecretKeyForJWTTokenGeneration12345678901234567890
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-password
```

Puis modifiez `docker-compose.yml` pour utiliser ces variables :

```yaml
environment:
  SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
  JWT_SECRET: ${JWT_SECRET}
  MAIL_USERNAME: ${MAIL_USERNAME}
  MAIL_PASSWORD: ${MAIL_PASSWORD}
```

## Commandes utiles

### Vérifier l'état des conteneurs
```bash
docker-compose ps
```

### Accéder au conteneur MySQL
```bash
docker exec -it projetjee-mysql mysql -u projetjee -p
```

### Accéder aux logs MySQL
```bash
docker-compose logs mysql
```

### Redémarrer un service
```bash
docker-compose restart app
```

### Supprimer tout et recommencer
```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## Dépannage

### L'application ne démarre pas

1. Vérifiez que MySQL est démarré :
   ```bash
   docker-compose ps
   ```

2. Vérifiez les logs :
   ```bash
   docker-compose logs app
   ```

3. Vérifiez la connexion à MySQL :
   ```bash
   docker exec -it projetjee-mysql mysql -u projetjee -p
   ```

### Port déjà utilisé

Si le port 8080 est déjà utilisé, modifiez dans `docker-compose.yml` :

```yaml
ports:
  - "8081:8080"  # Utilisez un autre port
```

### Problème de permissions

Sur Linux, vous pourriez avoir besoin de :

```bash
sudo docker-compose up -d
```

## Production

Pour la production, assurez-vous de :

1. Changer les mots de passe par défaut
2. Utiliser des secrets pour les variables sensibles
3. Configurer un reverse proxy (nginx)
4. Activer HTTPS
5. Configurer des backups pour MySQL

