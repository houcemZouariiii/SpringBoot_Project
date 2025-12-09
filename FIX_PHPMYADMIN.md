# Comment corriger l'erreur phpMyAdmin "Host 'localhost' is not allowed to connect"

## Problème
phpMyAdmin ne peut pas se connecter à MySQL/MariaDB car l'utilisateur n'a pas les permissions pour se connecter depuis 'localhost'.

## Solutions

### Solution 1 : Autoriser root depuis localhost (Recommandé)

#### Étape 1 : Se connecter à MySQL en ligne de commande

**Windows (XAMPP)** :
```cmd
cd C:\xampp\mysql\bin
mysql.exe -u root
```

**Windows (WAMP)** :
```cmd
cd C:\wamp64\bin\mysql\mysql8.x.x\bin
mysql.exe -u root
```

**Linux** :
```bash
sudo mysql -u root
# ou
sudo mysql -u root -p
```

**macOS** :
```bash
mysql -u root -p
```

#### Étape 2 : Exécuter les commandes SQL

Une fois connecté à MySQL, exécutez :

```sql
-- Vérifier les utilisateurs existants
SELECT user, host FROM mysql.user;

-- Autoriser root depuis localhost
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY '' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- Si vous avez un mot de passe pour root, utilisez :
-- GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY 'VOTRE_MOT_DE_PASSE' WITH GRANT OPTION;
-- FLUSH PRIVILEGES;
```

#### Étape 3 : Vérifier

```sql
SELECT user, host FROM mysql.user WHERE user='root';
```

Vous devriez voir `root@localhost` dans la liste.

### Solution 2 : Créer un nouvel utilisateur pour phpMyAdmin

```sql
-- Créer un nouvel utilisateur
CREATE USER 'phpmyadmin'@'localhost' IDENTIFIED BY 'password123';

-- Donner tous les privilèges
GRANT ALL PRIVILEGES ON *.* TO 'phpmyadmin'@'localhost' WITH GRANT OPTION;

-- Appliquer les changements
FLUSH PRIVILEGES;
```

Puis modifiez la configuration phpMyAdmin (voir Solution 4).

### Solution 3 : Utiliser 127.0.0.1 au lieu de localhost

#### Modifier config.inc.php de phpMyAdmin

**Windows (XAMPP)** :
Fichier : `C:\xampp\phpMyAdmin\config.inc.php`

**Windows (WAMP)** :
Fichier : `C:\wamp64\apps\phpmyadminX.X.X\config.inc.php`

**Linux** :
Fichier : `/etc/phpmyadmin/config.inc.php` ou `/usr/share/phpmyadmin/config.inc.php`

**macOS** :
Fichier : `/Applications/XAMPP/phpmyadmin/config.inc.php`

Modifiez la ligne :
```php
$cfg['Servers'][$i]['host'] = 'localhost';
```

En :
```php
$cfg['Servers'][$i]['host'] = '127.0.0.1';
```

### Solution 4 : Configurer phpMyAdmin correctement

Ouvrez `config.inc.php` et vérifiez/modifiez :

```php
/* Server parameters */
$cfg['Servers'][$i]['host'] = '127.0.0.1';  // ou 'localhost'
$cfg['Servers'][$i]['port'] = '3306';
$cfg['Servers'][$i]['user'] = 'root';
$cfg['Servers'][$i]['password'] = '';  // Votre mot de passe MySQL
$cfg['Servers'][$i]['auth_type'] = 'config';  // ou 'cookie' pour plus de sécurité
```

### Solution 5 : Réinitialiser les permissions MySQL (Si rien ne fonctionne)

**ATTENTION : Cette solution réinitialise les permissions. À utiliser en dernier recours.**

```sql
-- Se connecter en tant que root
mysql -u root

-- Supprimer et recréer l'utilisateur root
DROP USER 'root'@'localhost';
CREATE USER 'root'@'localhost' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```

### Solution 6 : Vérifier que MySQL/MariaDB est démarré

**Windows (XAMPP)** :
- Ouvrez le panneau de contrôle XAMPP
- Vérifiez que MySQL est démarré (bouton vert)

**Windows (WAMP)** :
- Cliquez sur l'icône WAMP dans la barre des tâches
- Vérifiez que MySQL est vert

**Linux** :
```bash
sudo systemctl status mysql
# ou
sudo systemctl status mariadb
```

**macOS** :
```bash
brew services list
```

## Test rapide

Pour tester si MySQL accepte les connexions :

```bash
mysql -u root -h 127.0.0.1
# ou
mysql -u root -h localhost
```

Si cela fonctionne, phpMyAdmin devrait aussi fonctionner.

## Solution rapide pour XAMPP

Si vous utilisez XAMPP et que rien ne fonctionne :

1. Arrêtez MySQL dans XAMPP
2. Supprimez le fichier : `C:\xampp\mysql\data\mysql\user.MYD` (faites une sauvegarde d'abord !)
3. Redémarrez MySQL dans XAMPP
4. MySQL sera réinitialisé avec root sans mot de passe

**⚠️ ATTENTION : Cela supprimera tous les utilisateurs MySQL existants !**

## Vérification finale

1. Redémarrez MySQL/MariaDB
2. Accédez à phpMyAdmin : http://localhost/phpmyadmin
3. Vous devriez pouvoir vous connecter

