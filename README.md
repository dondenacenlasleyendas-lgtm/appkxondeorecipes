# appkxondeorecipes

Proyecto independiente para la aplicación Android de **Kxondeo Recipes**.

## Configuración de Independencia
Este proyecto está configurado para ser independiente de la aplicación web:
- **Package Name**: `com.kxondeo.appkxondeorecipes`
- **Firebase**: Requiere un proyecto exclusivo en la consola de Firebase con el nombre `appkxondeorecipes`.
- **GitHub**: Este repositorio debe llamarse `appkxondeorecipes`.

## Requisitos de Publicación
1.  **Google Services**: Reemplaza `app/google-services.json` con el archivo generado en tu nuevo proyecto de Firebase.
2.  **Firma**: Genera un Keystore (.jks) para firmar la aplicación antes de subirla a Google Play.
3.  **Digital Asset Links**: Sube el archivo `assetlinks.json` a tu servidor si deseas habilitar App Links.

## Estructura del Proyecto
- `app/src/main/java`: Contiene la lógica nativa en Kotlin y Compose.
- `app/src/main/res`: Recursos de la aplicación, iconos y strings.
