# Configuration Gmail pour l'envoi d'emails

## Problème courant
Gmail bloque les connexions depuis les applications tierces par défaut pour des raisons de sécurité.

## Solution : Utiliser une "App Password" (Mot de passe d'application)

### Étapes pour créer une App Password :

1. **Activez la validation en deux étapes** (si ce n'est pas déjà fait) :
   - Allez sur https://myaccount.google.com/security
   - Activez "Validation en deux étapes"

2. **Créez une App Password** :
   - Allez sur https://myaccount.google.com/apppasswords
   - Sélectionnez "Application" : "Mail"
   - Sélectionnez "Appareil" : "Autre (nom personnalisé)"
   - Entrez "Spring Boot Application"
   - Cliquez sur "Générer"
   - **Copiez le mot de passe généré** (16 caractères sans espaces)

3. **Mettez à jour la configuration** :
   - Ouvrez `src/main/resources/application-dev.properties`
   - Remplacez `spring.mail.password=hz200230` par votre App Password
   - Exemple : `spring.mail.password=abcd efgh ijkl mnop` (sans espaces)

### Configuration actuelle dans application-dev.properties :

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=houcem.zouari18@gmail.com
spring.mail.password=VOTRE_APP_PASSWORD_ICI
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

## Vérification

1. **Vérifiez les logs** :
   - Les logs affichent maintenant des messages détaillés lors de l'envoi
   - Cherchez "Email envoyé avec succès" dans les logs

2. **Vérifiez votre boîte de réception** :
   - Les emails peuvent arriver dans les spams
   - Vérifiez aussi le dossier "Promotions" dans Gmail

3. **Erreurs courantes** :
   - `535-5.7.8 Username and Password not accepted` : App Password incorrecte
   - `534-5.7.9 Application-specific password required` : Vous devez utiliser une App Password
   - `550-5.7.1 Message rejected` : L'adresse "from" doit être la même que l'email configuré

## Alternative : Utiliser un autre service email

Si vous préférez ne pas utiliser Gmail, vous pouvez utiliser :
- **Mailtrap** (pour le développement) : https://mailtrap.io
- **SendGrid** : https://sendgrid.com
- **Mailgun** : https://www.mailgun.com

Pour Mailtrap (gratuit pour le développement), la configuration serait :
```properties
spring.mail.host=smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=votre_username_mailtrap
spring.mail.password=votre_password_mailtrap
```

