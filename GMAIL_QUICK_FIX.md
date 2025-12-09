# 🔧 Solution Rapide : Erreur d'Authentification Gmail

## ❌ Erreur actuelle
```
Authentication failed (Authentication failed)
```

## ✅ Solution en 3 étapes

### Étape 1 : Activer la validation en deux étapes
1. Allez sur : https://myaccount.google.com/security
2. Cherchez "Validation en deux étapes"
3. Cliquez sur "Activer" si ce n'est pas déjà fait
4. Suivez les instructions pour configurer (téléphone, etc.)

### Étape 2 : Créer une App Password
1. Allez directement sur : https://myaccount.google.com/apppasswords
   - Si le lien ne fonctionne pas, allez sur https://myaccount.google.com/security
   - Puis cherchez "Mots de passe des applications" ou "App passwords"
2. Sélectionnez :
   - **Application** : "Mail"
   - **Appareil** : "Autre (nom personnalisé)"
   - **Nom** : Tapez "Spring Boot Application"
3. Cliquez sur **"Générer"**
4. **Copiez le mot de passe** qui s'affiche (16 caractères, format : `xxxx xxxx xxxx xxxx`)

### Étape 3 : Mettre à jour la configuration
1. Ouvrez le fichier : `src/main/resources/application-dev.properties`
2. Trouvez la ligne :
   ```properties
   spring.mail.password=hz200230
   ```
3. Remplacez par votre App Password (sans espaces) :
   ```properties
   spring.mail.password=xxxxxxxxxxxxxxxx
   ```
   Exemple : Si Gmail vous donne `abcd efgh ijkl mnop`, écrivez `abcdefghijklmnop`

4. **Redémarrez l'application** (arrêtez et relancez)

## 📝 Exemple de configuration complète

```properties
# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=houcem.zouari18@gmail.com
spring.mail.password=VOTRE_APP_PASSWORD_SANS_ESPACES
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

## ⚠️ Notes importantes

- **Ne mettez PAS d'espaces** dans le mot de passe
- L'App Password est différente de votre mot de passe Gmail normal
- Si vous ne voyez pas l'option "App passwords", c'est que la validation en deux étapes n'est pas activée
- Après avoir changé le mot de passe, **redémarrez l'application**

## 🧪 Test

Après avoir fait ces modifications :
1. Redémarrez l'application
2. Essayez d'envoyer un email
3. Vérifiez les logs pour voir "Email envoyé avec succès"
4. Vérifiez votre boîte de réception (et les spams)

## ❓ Problèmes courants

**"Je ne vois pas l'option App passwords"**
→ Activez d'abord la validation en deux étapes

**"Le mot de passe ne fonctionne toujours pas"**
→ Vérifiez qu'il n'y a pas d'espaces dans le mot de passe
→ Vérifiez que vous avez copié les 16 caractères complets

**"L'email part mais n'arrive pas"**
→ Vérifiez les spams
→ Vérifiez que l'adresse email de destination est valide

