# Dépannage MySQL/MariaDB - Erreur "Host 'localhost' is not allowed to connect"

## Problème
L'erreur `"Host 'localhost' is not allowed to connect to this MariaDB server"` indique que l'utilisateur MySQL n'a pas les permissions pour se connecter depuis localhost.

## Solutions

### Solution 1 : Utiliser 127.0.0.1 au lieu de localhost

Modifiez `application-mysql.properties` :

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/projetjee?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

### Solution 2 : Autoriser l'utilisateur root depuis localhost

Connectez-vous à MySQL/MariaDB en tant qu'administrateur et exécutez :

```sql
-- Se connecter à MySQL
mysql -u root -p

-- Autoriser root depuis localhost
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY 'VOTRE_MOT_DE_PASSE' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- Ou créer un nouvel utilisateur
CREATE USER 'projetjee'@'localhost' IDENTIFIED BY 'VOTRE_MOT_DE_PASSE';
GRANT ALL PRIVILEGES ON projetjee.* TO 'projetjee'@'localhost';
FLUSH PRIVILEGES;
```

Puis modifiez `application-mysql.properties` :
```properties
spring.datasource.username=projetjee
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

### Solution 3 : Vérifier que MySQL/MariaDB est démarré

**Windows (XAMPP/WAMP)** :
- Ouvrez le panneau de contrôle XAMPP/WAMP
- Assurez-vous que MySQL est démarré (bouton "Start")

**Linux** :
```bash
sudo systemctl status mysql
# ou
sudo systemctl status mariadb

# Démarrer si nécessaire
sudo systemctl start mysql
```

**macOS** :
```bash
brew services list
brew services start mysql
```

### Solution 4 : Vérifier le port MySQL

Par défaut, MySQL utilise le port 3306. Vérifiez :

**Windows** :
```cmd
netstat -an | findstr 3306
```

**Linux/macOS** :
```bash
netstat -an | grep 3306
# ou
lsof -i :3306
```

Si le port est différent, modifiez l'URL dans `application-mysql.properties`.

### Solution 5 : Utiliser H2 temporairement

En attendant de résoudre le problème MySQL, utilisez le profil `dev` avec H2 :

Dans `application.properties` :
```properties
spring.profiles.active=dev
```

## Configuration recommandée pour phpMyAdmin

1. **Installer XAMPP** (inclut MySQL et phpMyAdmin)
2. **Démarrer MySQL** depuis le panneau XAMPP
3. **Accéder à phpMyAdmin** : http://localhost/phpmyadmin
4. **Créer un utilisateur** avec tous les privilèges :
   ```sql
   CREATE USER 'projetjee'@'localhost' IDENTIFIED BY 'password123';
   GRANT ALL PRIVILEGES ON projetjee.* TO 'projetjee'@'localhost';
   FLUSH PRIVILEGES;
   ```
5. **Mettre à jour** `application-mysql.properties` avec les nouvelles credentials

## Test de connexion

Pour tester la connexion MySQL depuis la ligne de commande :

```bash
mysql -u root -p
# ou
mysql -u projetjee -p
```

Si cela fonctionne, l'application devrait aussi fonctionner.

