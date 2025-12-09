# Configuration des Profils Spring Boot

## 📋 Profils Disponibles

L'application utilise **3 profils** différents selon l'environnement :

### 1. **`dev`** (Profil par défaut) ✅ Recommandé pour le développement

**Fichier** : `application-dev.properties`

**Caractéristiques** :
- ✅ **Base de données** : H2 (en mémoire) - Pas besoin de configuration
- ✅ **Console H2** : Activée sur `/h2-console`
- ✅ **Démarrage rapide** : Aucune configuration externe requise
- ✅ **Parfait pour tester** : Les données sont réinitialisées à chaque redémarrage

**Configuration actuelle** :
```properties
spring.profiles.active=dev
```

**Avantages** :
- 🚀 Démarrage immédiat sans configuration
- 🧪 Parfait pour les tests et le développement
- 📊 Console H2 accessible pour voir les données

---

### 2. **`mysql`** (Pour phpMyAdmin)

**Fichier** : `application-mysql.properties`

**Caractéristiques** :
- 📊 **Base de données** : MySQL/MariaDB
- 🔧 **Nécessite** : MySQL installé et configuré
- 📝 **Persistance** : Les données sont sauvegardées
- 🌐 **phpMyAdmin** : Accessible pour gérer la base de données

**Pour activer** :
1. Modifiez `application.properties` :
   ```properties
   spring.profiles.active=mysql
   ```
2. Configurez MySQL :
   - Créez la base de données `projetjee`
   - Vérifiez les credentials dans `application-mysql.properties`

**Configuration MySQL** :
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/projetjee
spring.datasource.username=root
spring.datasource.password=
```

---

### 3. **`prod`** (Production)

**Fichier** : `application-prod.properties`

**Caractéristiques** :
- 🏭 **Environnement** : Production
- 🔒 **Sécurité** : Configuration optimisée
- 📊 **Base de données** : MySQL (persistante)
- 🚫 **Console H2** : Désactivée

**Pour activer** :
```properties
spring.profiles.active=prod
```

---

## 🎯 Quel Profil Utiliser ?

### Pour le Développement (Recommandé) :
```properties
spring.profiles.active=dev
```
✅ **Utilisez ce profil** si vous voulez :
- Démarrer rapidement sans configuration
- Tester l'application
- Développer de nouvelles fonctionnalités

### Pour Utiliser phpMyAdmin :
```properties
spring.profiles.active=mysql
```
✅ **Utilisez ce profil** si vous voulez :
- Voir les données dans phpMyAdmin
- Avoir des données persistantes
- Utiliser MySQL

---

## ⚙️ Comment Changer de Profil

### Méthode 1 : Modifier `application.properties`
```properties
# Changez cette ligne :
spring.profiles.active=dev
# Par :
spring.profiles.active=mysql
```

### Méthode 2 : Via les Arguments JVM (Eclipse/IntelliJ)
Dans les **Run Configurations** :
- **VM arguments** : `-Dspring.profiles.active=mysql`
- Ou **Program arguments** : `--spring.profiles.active=mysql`

### Méthode 3 : Variable d'environnement
```bash
export SPRING_PROFILES_ACTIVE=mysql
```

---

## 📝 Configuration Actuelle

**Profil actif** : `dev` ✅

**Fichier** : `src/main/resources/application.properties`
```properties
spring.profiles.active=dev
```

**Base de données** : H2 (en mémoire)
- URL : `jdbc:h2:mem:testdb`
- Console : http://localhost:8080/h2-console
- JDBC URL : `jdbc:h2:mem:testdb`
- Username : `sa`
- Password : (vide)

---

## 🔍 Vérifier le Profil Actif

Lors du démarrage, vous verrez dans les logs :
```
The following profiles are active: dev
```

Ou dans la console :
```
Active profiles: dev
```

---

## 💡 Recommandation

**Pour commencer** : Utilisez le profil `dev` (déjà configuré)
- ✅ Pas de configuration nécessaire
- ✅ Démarrage immédiat
- ✅ Parfait pour tester toutes les fonctionnalités

**Pour phpMyAdmin** : Passez au profil `mysql` quand vous voulez voir les données dans phpMyAdmin

